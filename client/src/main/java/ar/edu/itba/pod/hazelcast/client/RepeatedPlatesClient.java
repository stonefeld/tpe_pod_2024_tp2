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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Stream;

public class RepeatedPlatesClient {

    private static final Logger logger = LoggerFactory.getLogger(RepeatedPlatesClient.class);

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        logger.info("Repeated Plates Client Starting ...");

//        final String addresses = System.getProperty("addresses", "");
//        final String city = System.getProperty("city", "");
//        final String inPath = System.getProperty("inPath", "");
//        final String outPath = System.getProperty("outPath", "");
//
//
//        if (addresses.isEmpty()) {
//            System.out.println("IP addresses are required");
//            return;
//        }
//
//        if (city.compareTo("NYC") != 0 && city.compareTo("CHI") != 0) {
//            System.out.println("City is required");
//            return;
//        }
//
//        if (inPath.isEmpty()) {
//            System.out.println("Input path is required");
//            return;
//        }
//
//        if (outPath.isEmpty()) {
//            System.out.println("Output path is required");
//            return;
//        }

        try {
            // Group Config
            GroupConfig groupConfig = new GroupConfig().setName("l12345").setPassword("l12345-pass");

            // Client Network Config
            ClientNetworkConfig clientNetworkConfig = new ClientNetworkConfig();
            clientNetworkConfig.addAddress("127.0.0.1");

            // Client Config
            ClientConfig clientConfig = new ClientConfig().setGroupConfig(groupConfig).setNetworkConfig(clientNetworkConfig);

            // Node Client
            HazelcastInstance hazelcastInstance = HazelcastClient.newHazelcastClient(clientConfig);

            // Key Value Source
            MultiMap<LocalDate, TicketRow> ticketsMultiMap = hazelcastInstance.getMultiMap("tickets");
            KeyValueSource<LocalDate, TicketRow> wordsKeyValueSource = KeyValueSource.fromMultiMap(ticketsMultiMap);

            // Job Tracker
            JobTracker jobTracker = hazelcastInstance.getJobTracker("repeated-plates");

            // Text File Reading and Key Value Source Loading
            try (Stream<String> lines = Files.lines(Paths.get(args[0]), StandardCharsets.UTF_8)) {
                lines.skip(1)
                        .map(line -> line.split(";"))
                        .map(line -> new TicketRow(
                                line[0],
                                line[1],
                                line[3],
                                line[5],
                                (int) Double.parseDouble(line[2]),
                                LocalDate.parse(line[4]))
                        ).forEach(ticketRow -> ticketsMultiMap.put(ticketRow.getIssueDate(), ticketRow));
            }

            LocalDate from = LocalDate.of(2010, 1, 1);
            LocalDate to = LocalDate.of(2023, 12, 31);
            int n = 2;

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

    private static <T> void writeToCSV(String fileName, String header, Iterator<T> dataList, Function<T, String> csvLineMapper) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(header);

        while (dataList.hasNext()) {
            lines.add(csvLineMapper.apply(dataList.next()));
        }

        Files.write(Paths.get(fileName), lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

}
