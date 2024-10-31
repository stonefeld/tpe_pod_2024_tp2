package ar.edu.itba.pod.hazelcast.client;

import ar.edu.itba.pod.common.TicketRow;
import ar.edu.itba.pod.repeatedplates.*;
import ar.edu.itba.pod.totaltickets.TotalTicketsCollator;
import ar.edu.itba.pod.totaltickets.TotalTicketsMapper;
import ar.edu.itba.pod.totaltickets.TotalTicketsReducerFactory;
import ar.edu.itba.pod.totaltickets.TotalTicketsResult;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.config.GroupConfig;
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
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Stream;

public class RepeatedPlatesClient extends Client {

    private static final Logger logger = LoggerFactory.getLogger(RepeatedPlatesClient.class);

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        logger.info("Repeated Plates Client Starting ...");

        try {
            // Parse all properties
            // TODO: lanzar exception si n, from y/o to no estan seteados
            processProperties();

            // Node Client
            HazelcastInstance hazelcastInstance = getHazelcastInstance();

            // Key Value Source
            MultiMap<LocalDate, TicketRow> ticketsMultiMap = hazelcastInstance.getMultiMap("tickets");
            KeyValueSource<LocalDate, TicketRow> wordsKeyValueSource = KeyValueSource.fromMultiMap(ticketsMultiMap);

            // Job Tracker
            JobTracker jobTracker = hazelcastInstance.getJobTracker("repeated-plates");

            // Text File Reading and Key Value Source Loading
            try (Stream<String> lines = Files.lines(Paths.get(inPath, "tickets" + city + ".csv"), StandardCharsets.UTF_8)) {
                lines.skip(1)
                        .map(line -> line.split(";"))
                        .map(line -> new TicketRow(line[0], line[1], line[3], line[5],
                                (int) Double.parseDouble(line[2]), line[4])
                        ).forEach(ticketRow -> ticketsMultiMap.put(ticketRow.getIssueDate(), ticketRow));
            }

            // MapReduce Job
            Job<LocalDate, TicketRow> job = jobTracker.newJob(wordsKeyValueSource);
            JobCompletableFuture<SortedSet<RepeatedPlatesResult>> future = job
                    .keyPredicate(new RepeatedPlatesKeyPredicate(from, to))
                    .mapper(new RepeatedPlatesMapper())
                    .reducer(new RepeatedPlatesReducerFactory(n))
                    .submit(new RepeatedPlatesCollator());

            // Wait and retrieve the result
            SortedSet<RepeatedPlatesResult> result = future.get();

            // Sort entries ascending by count and print
            String header = "County;Percentage";
            String fileName = "output.csv";
            Function<RepeatedPlatesResult, String> csvLineMapper = RepeatedPlatesResult::toString;

            writeToCSV(fileName, header, result.iterator(), csvLineMapper);
        } finally {
            HazelcastClient.shutdownAll();
        }
    }

}
