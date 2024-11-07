package ar.edu.itba.pod.hazelcast.client;

import ar.edu.itba.pod.hazelcast.common.TicketRow;
import ar.edu.itba.pod.hazelcast.totaltickets.*;
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

public class TotalTicketsClient extends Client {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        try {
            // Parse all properties
            processProperties();

            // Setup the logger
            Logger logger = setUpLogger(TotalTicketsClient.class, "time1.txt");

            // Node Client
            HazelcastInstance hazelcastInstance = getHazelcastInstance();

            // Limpiamos los datos
            hazelcastInstance.getMultiMap("g2-tickets").destroy();
            hazelcastInstance.getMap("g2-agencies").destroy();
            hazelcastInstance.getMap("g2-infractions").destroy();

            // Key Value Source
            MultiMap<AgencyInfractionNamesPair, TicketRow> ticketsMultiMap = hazelcastInstance.getMultiMap("g2-tickets");
            KeyValueSource<AgencyInfractionNamesPair, TicketRow> ticketRowKeyValueSource = KeyValueSource.fromMultiMap(ticketsMultiMap);

            IMap<String, String> infractionsMap = hazelcastInstance.getMap("g2-infractions");
            IMap<String, Integer> agenciesMap = hazelcastInstance.getMap("g2-agencies");

            // Job Tracker
            JobTracker jobTracker = hazelcastInstance.getJobTracker("g2-ticket-count");

            logger.info("Inicio de la lectura del archivo");

            // Text File Reading and Key Value Source Loading
            try (Stream<String> lines = Files.lines(Paths.get(inPath, "tickets" + city + ".csv"), StandardCharsets.UTF_8)) {
                AtomicInteger id = new AtomicInteger();
                lines.skip(1).forEach(line -> {
                    TicketRow ticketRow = mapper.apply(new Pair<>(line.split(";"), id.getAndIncrement()));
                    ticketsMultiMap.put(new AgencyInfractionNamesPair(ticketRow.getAgency(), ticketRow.getInfractionId()), ticketRow);
                });
            }

            try (Stream<String> lines = Files.lines(Paths.get(inPath, "infractions" + city + ".csv"), StandardCharsets.UTF_8)) {
                lines.skip(1).forEach(line -> {
                    String[] split = line.split(";");
                    infractionsMap.put(split[0], split[1]);
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
            Job<AgencyInfractionNamesPair, TicketRow> job = jobTracker.newJob(ticketRowKeyValueSource);
            JobCompletableFuture<SortedSet<TotalTicketsResult>> future = job
                    .keyPredicate(new TotalTicketsKeyPredicate())
                    .mapper(new TotalTicketsMapper())
                    .reducer(new TotalTicketsReducerFactory())
                    .submit(new TotalTicketsCollator(hazelcastInstance));

            // Wait and retrieve the result
            SortedSet<TotalTicketsResult> result = future.get();

            // Sort entries ascending by count and print
            String header = "Infraction;Agency;Tickets";
            String fileName = "query1.csv";
            Function<TotalTicketsResult, String> csvLineMapper = TotalTicketsResult::toString;

            writeToCSV(fileName, header, result.iterator(), csvLineMapper);

            logger.info("Fin del trabajo map/reduce");
            logger.info("Inicio del trabajo map/reduce (con Combiner)");

            // MapReduce Job
            Job<AgencyInfractionNamesPair, TicketRow> combinerJob = jobTracker.newJob(ticketRowKeyValueSource);
            JobCompletableFuture<SortedSet<TotalTicketsResult>> combinerFuture = combinerJob
                    .keyPredicate(new TotalTicketsKeyPredicate())
                    .mapper(new TotalTicketsMapper())
                    .combiner(new TotalTicketsCombinerFactory())
                    .reducer(new TotalTicketsReducerFactory())
                    .submit(new TotalTicketsCollator(hazelcastInstance));

            // Wait and retrieve the result
            SortedSet<TotalTicketsResult> combinerResult = combinerFuture.get();

            // Sort entries ascending by count and print
            String combinerFileName = "query1_combiner.csv";

            writeToCSV(combinerFileName, header, combinerResult.iterator(), csvLineMapper);

            logger.info("Fin del trabajo map/reduce (con Combiner)");
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        } finally {
            HazelcastClient.shutdownAll();
        }
    }

}
