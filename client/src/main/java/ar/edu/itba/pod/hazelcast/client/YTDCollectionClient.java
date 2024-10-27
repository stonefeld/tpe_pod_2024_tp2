package ar.edu.itba.pod.hazelcast.client;

import ar.edu.itba.pod.common.TicketRow;
import ar.edu.itba.pod.ytdcollection.YTDCollectionCollator;
import ar.edu.itba.pod.ytdcollection.YTDCollectionMapper;
import ar.edu.itba.pod.ytdcollection.YTDCollectionReducerFactory;
import ar.edu.itba.pod.ytdcollection.YTDCollectionResult;
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
import java.time.LocalDate;
import java.util.SortedSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Stream;

public class YTDCollectionClient extends Client {

    private static final Logger logger = LoggerFactory.getLogger(YTDCollectionClient.class);

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        logger.info("YTD Collection Client Starting ...");

        try {
            // Parse all properties
            processProperties();

            // Node Client
            HazelcastInstance hazelcastInstance = getHazelcastInstance();

            // Key Value Source
            MultiMap<String, TicketRow> ticketsMultiMap = hazelcastInstance.getMultiMap("tickets");
            KeyValueSource<String, TicketRow> wordsKeyValueSource = KeyValueSource.fromMultiMap(ticketsMultiMap);

            IMap<String, Integer> agenciesMap = hazelcastInstance.getMap("agencies");

            // Job Tracker
            JobTracker jobTracker = hazelcastInstance.getJobTracker("ytd-collection");

            logger.info("Inicio de la lectura del archivo");

            // Text File Reading and Key Value Source Loading
            try (Stream<String> lines = Files.lines(Paths.get(inPath, "tickets" + city + ".csv"), StandardCharsets.UTF_8)) {
                lines.skip(1)
                        .map(line -> line.split(";"))
                        .map(line -> new TicketRow(line[0], line[1], line[3], line[5],
                                (int) Double.parseDouble(line[2]), LocalDate.parse(line[4]))
                        ).forEach(ticketRow -> ticketsMultiMap.put(ticketRow.getAgency(), ticketRow));
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
                    .mapper(new YTDCollectionMapper())
                    .reducer(new YTDCollectionReducerFactory())
                    .submit(new YTDCollectionCollator(hazelcastInstance));

            // Wait and retrieve the result
            SortedSet<YTDCollectionResult> result = future.get();

            logger.info("Fin del trabajo map/reduce");

            // Sort entries ascending by count and print
            String header = "Agency;Year;Month;YTD";
            String fileName = "output.csv";
            Function<YTDCollectionResult, String> csvLineMapper = YTDCollectionResult::toString;

            writeToCSV(fileName, header, result.iterator(), csvLineMapper);
        } finally {
            HazelcastClient.shutdownAll();
        }
    }

}
