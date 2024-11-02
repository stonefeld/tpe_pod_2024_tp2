package ar.edu.itba.pod.hazelcast.client;

import ar.edu.itba.pod.hazelcast.common.TicketRow;
import ar.edu.itba.pod.hazelcast.repeatedplates.*;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
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
import java.time.LocalDate;
import java.util.SortedSet;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Stream;

public class RepeatedPlatesClient extends Client {

    private static final Logger logger = LoggerFactory.getLogger(RepeatedPlatesClient.class);

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        try {
            // Parse all properties
            processProperties();

            if (n == null)
                throw new IllegalArgumentException("N is required");
            if (from == null)
                throw new IllegalArgumentException("From is required");
            if (to == null)
                throw new IllegalArgumentException("To is required");

            // Node Client
            HazelcastInstance hazelcastInstance = getHazelcastInstance();

            // Key Value Source
            MultiMap<LocalDate, TicketRow> ticketsMultiMap = hazelcastInstance.getMultiMap("g2-tickets");
            KeyValueSource<LocalDate, TicketRow> wordsKeyValueSource = KeyValueSource.fromMultiMap(ticketsMultiMap);

            // Job Tracker
            JobTracker jobTracker = hazelcastInstance.getJobTracker("g2-repeated-plates");

            logger.info("Inicio de la lectura del archivo");

            // Text File Reading and Key Value Source Loading
            try (Stream<String> lines = Files.lines(Paths.get(inPath, "tickets" + city + ".csv"), StandardCharsets.UTF_8)) {
                Function<String[], TicketRow> mapper = city.equals("NYC") ? mapperNYC : mapperCHI;
                lines.skip(1).map(line -> line.split(";")).map(mapper)
                        .forEach(ticketRow -> ticketsMultiMap.put(ticketRow.getIssueDate(), ticketRow));
            }

            logger.info("Fin de lectura del archivo");
            logger.info("Inicio del trabajo map/reduce");

            // MapReduce Job
            Job<LocalDate, TicketRow> job = jobTracker.newJob(wordsKeyValueSource);
            JobCompletableFuture<SortedSet<RepeatedPlatesResult>> future = job
                    .keyPredicate(new RepeatedPlatesKeyPredicate(from, to))
                    .mapper(new RepeatedPlatesMapper())
                    .reducer(new RepeatedPlatesReducerFactory(n))
                    .submit(new RepeatedPlatesCollator());

            // Wait and retrieve the result
            SortedSet<RepeatedPlatesResult> result = future.get();

            // Destroy the data
            ticketsMultiMap.destroy();

            // Sort entries ascending by count and print
            String header = "County;Percentage";
            String fileName = "query3.csv";
            Function<RepeatedPlatesResult, String> csvLineMapper = RepeatedPlatesResult::toString;

            writeToCSV(fileName, header, result.iterator(), csvLineMapper);

            logger.info("Fin del trabajo map/reduce");
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
        } finally {
            HazelcastClient.shutdownAll();
        }
    }

}
