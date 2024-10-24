package ar.edu.itba.pod.hazelcast.client;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class Client {

    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        logger.info("hz-word-count Client Starting ...");

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
            IMap<String, String> wordsIMap = hazelcastInstance.getMap("words");
            KeyValueSource<String, String> wordsKeyValueSource = KeyValueSource.fromMap(wordsIMap);

            // Job Tracker
            JobTracker jobTracker = hazelcastInstance.getJobTracker("word-count");

            // Text File Reading and Key Value Source Loading
            final AtomicInteger auxKey = new AtomicInteger();
            try (Stream<String> lines = Files.lines(Paths.get(args[0]), StandardCharsets.UTF_8)) {
                lines.forEach(line -> wordsIMap.put(String.valueOf(auxKey.getAndIncrement()), line));
            }

            // MapReduce Job
            Job<String, String> job = jobTracker.newJob(wordsKeyValueSource);
//            ICompletableFuture<Map<String, String>> future = job
//                    .mapper(new TokenizerMapper2())
//                    .reducer(new WordCountReducerFactory2())
//                    .submit();

            // Wait and retrieve the result
//            Map<String, String> result = future.get();

            // Sort entries ascending by count and print
//            result.entrySet().stream()
//                    .sorted((Map.Entry.<String, String>comparingByValue()))
//                    .forEach(System.out::println);
        } finally {
            HazelcastClient.shutdownAll();
        }
    }

}
