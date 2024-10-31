package ar.edu.itba.pod.hazelcast.client;

import ar.edu.itba.pod.common.TicketRow;
import ar.edu.itba.pod.maxticketdifference.*;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Stream;

public class MaxTicketDifferenceClient extends Client {

    private static final Logger logger = LoggerFactory.getLogger(MaxTicketDifferenceClient.class);

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        logger.info("Total Tickets Client Starting ...");

        try {
            // Parse all properties
            // TODO: lanzar exception si n y/o agency no estan seteados
            processProperties();

            // Node Client
            HazelcastInstance hazelcastInstance = getHazelcastInstance();

            // Key Value Source
            MultiMap<String, TicketRow> ticketsMultiMap = hazelcastInstance.getMultiMap("tickets");
            KeyValueSource<String, TicketRow> wordsKeyValueSource = KeyValueSource.fromMultiMap(ticketsMultiMap);

            // Job Tracker
            JobTracker jobTracker = hazelcastInstance.getJobTracker("ticket-count");

            // Text File Reading and Key Value Source Loading
            try (Stream<String> lines = Files.lines(Paths.get(args[0]), StandardCharsets.UTF_8)) {
                lines.skip(1)
                        .map(line -> line.split(";"))
                        .map(line -> new TicketRow(line[0], line[1], line[3], line[5],
                                (int) Double.parseDouble(line[2]), line[4])
                        ).forEach(ticketRow -> ticketsMultiMap.put(ticketRow.getAgency(), ticketRow));
            }

            String agency = "TRAFFIC";
            int n = 20;

            // MapReduce Job
            Job<String, TicketRow> job = jobTracker.newJob(wordsKeyValueSource);
            JobCompletableFuture<SortedSet<MaxTicketDifferenceResult>> future = job
                    .keyPredicate(new MaxTicketDifferenceKeyPredicate(agency))
                    .mapper(new MaxTicketDifferenceMapper())
                    .reducer(new MaxTicketDifferenceReducerFactory())
                    .submit(new MaxTicketDifferenceCollator(n));

            // Wait and retrieve the result
            SortedSet<MaxTicketDifferenceResult> result = future.get();

            // Sort entries ascending by count and print
            String header = "Infraction;Min;Max;Diff";
            String fileName = "output.csv";
            Function<MaxTicketDifferenceResult, String> csvLineMapper = MaxTicketDifferenceResult::toString;

            writeToCSV(fileName, header, result.iterator(), csvLineMapper);
        } finally {
            HazelcastClient.shutdownAll();
        }
    }

}
