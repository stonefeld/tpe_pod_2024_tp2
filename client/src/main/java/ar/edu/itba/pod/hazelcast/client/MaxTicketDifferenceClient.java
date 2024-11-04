package ar.edu.itba.pod.hazelcast.client;

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
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.SortedSet;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Stream;

public class MaxTicketDifferenceClient extends Client {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        try {
            // Parse all properties
            processProperties();

            // Setup the logger
            Logger logger = setUpLogger(TotalTicketsClient.class, "time4.txt");

            if (n == null)
                throw new IllegalArgumentException("N is required");
            if (agency == null)
                throw new IllegalArgumentException("Agency is required");

            // Node Client
            HazelcastInstance hazelcastInstance = getHazelcastInstance();

            // Key Value Source
            MultiMap<String, TicketRow> ticketsMultiMap = hazelcastInstance.getMultiMap("g2-tickets");
            KeyValueSource<String, TicketRow> wordsKeyValueSource = KeyValueSource.fromMultiMap(ticketsMultiMap);

            IMap<String, String> infractionsMap = hazelcastInstance.getMap("g2-infractions");

            // Job Tracker
            JobTracker jobTracker = hazelcastInstance.getJobTracker("g2-max-difference");

            logger.info("Inicio de la lectura del archivo");

            // Text File Reading and Key Value Source Loading
            try (Stream<String> lines = Files.lines(Paths.get(inPath, "tickets" + city + ".csv"), StandardCharsets.UTF_8)) {
                Function<String[], TicketRow> mapper = city.equals("NYC") ? mapperNYC : mapperCHI;
                lines.skip(1).map(line -> line.split(";")).map(mapper)
                        .forEach(ticketRow -> ticketsMultiMap.put(ticketRow.getAgency(), ticketRow));
            }

            try (Stream<String> lines = Files.lines(Paths.get(inPath, "infractions" + city + ".csv"), StandardCharsets.UTF_8)) {
                lines.skip(1)
                        .map(line -> line.split(";"))
                        .forEach(line -> infractionsMap.put(line[0], line[1]));
            }

            logger.info("Fin de lectura del archivo");
            logger.info("Inicio del trabajo map/reduce");

            // MapReduce Job
            Job<String, TicketRow> job = jobTracker.newJob(wordsKeyValueSource);
            JobCompletableFuture<SortedSet<MaxTicketDifferenceResult>> future = job
                    .keyPredicate(new MaxTicketDifferenceKeyPredicate(agency))
                    .mapper(new MaxTicketDifferenceMapper())
                    .reducer(new MaxTicketDifferenceReducerFactory())
                    .submit(new MaxTicketDifferenceCollator(hazelcastInstance, n));

            // Wait and retrieve the result
            SortedSet<MaxTicketDifferenceResult> result = future.get();

            // Destroy the data
            ticketsMultiMap.destroy();
            infractionsMap.destroy();

            // Sort entries ascending by count and print
            String header = "Infraction;Min;Max;Diff";
            String fileName = "query4.csv";
            Function<MaxTicketDifferenceResult, String> csvLineMapper = MaxTicketDifferenceResult::toString;

            writeToCSV(fileName, header, result.iterator(), csvLineMapper);

            logger.info("Fin del trabajo map/reduce");
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        } finally {
            HazelcastClient.shutdownAll();
        }
    }

}
