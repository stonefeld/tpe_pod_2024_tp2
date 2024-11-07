package ar.edu.itba.pod.hazelcast.client;

import ar.edu.itba.pod.hazelcast.common.InfractionAmountPair;
import ar.edu.itba.pod.hazelcast.common.TicketRow;
import ar.edu.itba.pod.hazelcast.maxticketdifference.*;
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

public class MaxTicketDifferenceClient extends Client {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        try {
            // Parse all properties
            processProperties();

            // Setup the logger
            Logger logger = setUpLogger(MaxTicketDifferenceClient.class, "time4.txt");

            if (n == null)
                throw new IllegalArgumentException("N is required");
            if (agency == null)
                throw new IllegalArgumentException("Agency is required");

            // Node Client
            HazelcastInstance hazelcastInstance = getHazelcastInstance();

            // Limpiamos los datos
            hazelcastInstance.getMultiMap("g2-tickets").destroy();
            hazelcastInstance.getMap("g2-infractions").destroy();

            // Key Value Source
            MultiMap<String, InfractionAmountPair> ticketsMultiMap = hazelcastInstance.getMultiMap("g2-tickets");
            KeyValueSource<String, InfractionAmountPair> ticketRowKeyValueSource = KeyValueSource.fromMultiMap(ticketsMultiMap);

            IMap<String, String> infractionsMap = hazelcastInstance.getMap("g2-infractions");

            // Job Tracker
            JobTracker jobTracker = hazelcastInstance.getJobTracker("g2-max-difference");

            logger.info("Inicio de la lectura del archivo");

            // Text File Reading and Key Value Source Loading
            try (Stream<String> lines = Files.lines(Paths.get(inPath, "tickets" + city + ".csv"), StandardCharsets.UTF_8)) {
                AtomicInteger id = new AtomicInteger();
                lines.skip(1).forEach(line -> {
                    String[] split = line.split(";");
                    double amount = Double.parseDouble(split[2]);
                    ticketsMultiMap.put(split[3], new InfractionAmountPair(id.getAndIncrement(), split[1], (int) amount));
                });
            }

            try (Stream<String> lines = Files.lines(Paths.get(inPath, "infractions" + city + ".csv"), StandardCharsets.UTF_8)) {
                lines.skip(1).forEach(line -> {
                    String[] split = line.split(";");
                    infractionsMap.put(split[0], split[1]);
                });
            }

            logger.info("Fin de lectura del archivo");
            logger.info("Inicio del trabajo map/reduce");

            // MapReduce Job
            Job<String, InfractionAmountPair> job = jobTracker.newJob(ticketRowKeyValueSource);
            JobCompletableFuture<SortedSet<MaxTicketDifferenceResult>> future = job
                    .keyPredicate(new MaxTicketDifferenceKeyPredicate(agency))
                    .mapper(new MaxTicketDifferenceMapper())
                    .reducer(new MaxTicketDifferenceReducerFactory())
                    .submit(new MaxTicketDifferenceCollator(hazelcastInstance, n));

            // Wait and retrieve the result
            SortedSet<MaxTicketDifferenceResult> result = future.get();

            // Sort entries ascending by count and print
            String header = "Infraction;Min;Max;Diff";
            String fileName = "query4.csv";
            Function<MaxTicketDifferenceResult, String> csvLineMapper = MaxTicketDifferenceResult::toString;

            writeToCSV(fileName, header, result.iterator(), csvLineMapper);

            logger.info("Fin del trabajo map/reduce");
            logger.info("Inicio del trabajo map/reduce (con Combiner)");

            // MapReduce Job
            Job<String, InfractionAmountPair> combinerJob = jobTracker.newJob(ticketRowKeyValueSource);
            JobCompletableFuture<SortedSet<MaxTicketDifferenceResult>> combinerFuture = combinerJob
                    .keyPredicate(new MaxTicketDifferenceKeyPredicate(agency))
                    .mapper(new MaxTicketDifferenceMapper())
                    .combiner(new MaxTicketDifferenceCombinerFactory())
                    .reducer(new MaxTicketDifferenceReducerFactoryWithCombiner())
                    .submit(new MaxTicketDifferenceCollator(hazelcastInstance, n));

            // Wait and retrieve the result
            SortedSet<MaxTicketDifferenceResult> combinerResult = combinerFuture.get();

            // Sort entries ascending by count and print
            String combinerFileName = "query4_combiner.csv";

            writeToCSV(combinerFileName, header, combinerResult.iterator(), csvLineMapper);

            logger.info("Fin del trabajo map/reduce (con Combiner)");
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        } finally {
            HazelcastClient.shutdownAll();
        }
    }

}
