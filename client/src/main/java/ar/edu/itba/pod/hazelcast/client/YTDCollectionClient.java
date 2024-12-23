package ar.edu.itba.pod.hazelcast.client;

import ar.edu.itba.pod.hazelcast.common.TicketRow;
import ar.edu.itba.pod.hazelcast.ytdcollection.*;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MultiMap;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobCompletableFuture;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.SortedSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Stream;

public class YTDCollectionClient extends Client {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        try {
            // Parse all properties
            processProperties();

            // Setup the logger
            Logger logger = setUpLogger(YTDCollectionClient.class, "time2.txt");

            // Node Client
            HazelcastInstance hazelcastInstance = getHazelcastInstance();

            // Limpiamos los datos
            hazelcastInstance.getMultiMap("g2-tickets").destroy();
            hazelcastInstance.getMap("g2-agencies").destroy();

            // Key Value Source
            MultiMap<String, TicketRow> ticketsMultiMap = hazelcastInstance.getMultiMap("g2-tickets");
            KeyValueSource<String, TicketRow> ticketRowKeyValueSource = KeyValueSource.fromMultiMap(ticketsMultiMap);

            IMap<String, Integer> agenciesMap = hazelcastInstance.getMap("g2-agencies");

            // Job Tracker
            JobTracker jobTracker = hazelcastInstance.getJobTracker("g2-ytd-collection");

            logger.info("Inicio de la lectura del archivo");

            // Text File Reading and Key Value Source Loading
            try (Stream<String> lines = Files.lines(Paths.get(inPath, "tickets" + city + ".csv"), StandardCharsets.UTF_8)) {
                AtomicInteger id = new AtomicInteger();
                lines.skip(1).forEach(line -> {
                    TicketRow ticketRow = mapper.apply(new Pair<>(line.split(";"), id.getAndIncrement()));
                    ticketsMultiMap.put(ticketRow.getAgency(), ticketRow);
                });
            }

            try (Stream<String> lines = Files.lines(Paths.get(inPath, "agencies" + city + ".csv"), StandardCharsets.UTF_8)) {
                AtomicInteger id = new AtomicInteger();
                lines.skip(1).forEach(line -> {
                    String[] split = line.split(";");
                    agenciesMap.put(split[0], id.getAndIncrement());
                });
            }

            logger.info("Fin de lectura del archivo");
            logger.info("Inicio del trabajo map/reduce");

            // MapReduce Job
            Job<String, TicketRow> job = jobTracker.newJob(ticketRowKeyValueSource);
            JobCompletableFuture<SortedSet<YTDCollectionResult>> future = job
                    .keyPredicate(new YTDCollectionKeyPredicate())
                    .mapper(new YTDCollectionMapper())
                    .reducer(new YTDCollectionReducerFactory())
                    .submit(new YTDCollectionCollator(hazelcastInstance));

            // Wait and retrieve the result
            SortedSet<YTDCollectionResult> result = future.get();

            // Sort entries ascending by count and print
            String header = "Agency;Year;Month;YTD";
            String fileName = "query2.csv";
            Function<YTDCollectionResult, String> csvLineMapper = YTDCollectionResult::toString;

            writeToCSV(fileName, header, result.iterator(), csvLineMapper);

            logger.info("Fin del trabajo map/reduce");
            logger.info("Inicio del trabajo map/reduce (con Combiner)");

            // MapReduce Job
            Job<String, TicketRow> combinerJob = jobTracker.newJob(ticketRowKeyValueSource);
            JobCompletableFuture<SortedSet<YTDCollectionResult>> combinerFuture = combinerJob
                    .keyPredicate(new YTDCollectionKeyPredicate())
                    .mapper(new YTDCollectionMapper())
                    .combiner(new YTDCollectionCombinerFactory())
                    .reducer(new YTDCollectionReducerFactoryWithCombiner())
                    .submit(new YTDCollectionCollator(hazelcastInstance));

            // Wait and retrieve the result
            SortedSet<YTDCollectionResult> combinerResult = combinerFuture.get();

            // Sort entries ascending by count and print
            String combinerFileName = "query2_combiner.csv";

            writeToCSV(combinerFileName, header, combinerResult.iterator(), csvLineMapper);

            logger.info("Fin del trabajo map/reduce (con Combiner)");
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        } finally {
            HazelcastClient.shutdownAll();
        }
    }

}
