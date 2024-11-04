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
import org.slf4j.LoggerFactory;

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
            Logger logger = setUpLogger(TotalTicketsClient.class, "time2.txt");

            // Node Client
            HazelcastInstance hazelcastInstance = getHazelcastInstance();

            // Key Value Source
            MultiMap<String, TicketRow> ticketsMultiMap = hazelcastInstance.getMultiMap("g2-tickets");
            KeyValueSource<String, TicketRow> wordsKeyValueSource = KeyValueSource.fromMultiMap(ticketsMultiMap);

            IMap<String, Integer> agenciesMap = hazelcastInstance.getMap("g2-agencies");

            // Job Tracker
            JobTracker jobTracker = hazelcastInstance.getJobTracker("g2-ytd-collection");

            logger.info("Inicio de la lectura del archivo");

            // Text File Reading and Key Value Source Loading
            try (Stream<String> lines = Files.lines(Paths.get(inPath, "tickets" + city + ".csv"), StandardCharsets.UTF_8)) {
                Function<String[], TicketRow> mapper = city.equals("NYC") ? mapperNYC : mapperCHI;
                lines.skip(1).map(line -> line.split(";")).map(mapper)
                        .forEach(ticketRow -> ticketsMultiMap.put(ticketRow.getAgency(), ticketRow));
            }

            try (Stream<String> lines = Files.lines(Paths.get(inPath, "agencies" + city + ".csv"), StandardCharsets.UTF_8)) {
                AtomicInteger id = new AtomicInteger();
                lines.skip(1)
                        .map(line -> line.split(";"))
                        .forEach(line -> agenciesMap.put(line[0], id.getAndIncrement()));
            }

            logger.info("Fin de lectura del archivo");
            logger.info("Inicio del trabajo map/reduce");

            // MapReduce Job
            Job<String, TicketRow> job = jobTracker.newJob(wordsKeyValueSource);
            JobCompletableFuture<SortedSet<YTDCollectionResult>> future = job
                    .keyPredicate(new YTDCollectionKeyPredicate())
                    .mapper(new YTDCollectionMapper())
                    .reducer(new YTDCollectionReducerFactory())
                    .submit(new YTDCollectionCollator(hazelcastInstance));

            // Wait and retrieve the result
            SortedSet<YTDCollectionResult> result = future.get();

            // Destroy the data
            ticketsMultiMap.destroy();
            agenciesMap.destroy();

            // Sort entries ascending by count and print
            String header = "Agency;Year;Month;YTD";
            String fileName = "query2.csv";
            Function<YTDCollectionResult, String> csvLineMapper = YTDCollectionResult::toString;

            writeToCSV(fileName, header, result.iterator(), csvLineMapper);

            logger.info("Fin del trabajo map/reduce");
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        } finally {
            HazelcastClient.shutdownAll();
        }
    }

}
