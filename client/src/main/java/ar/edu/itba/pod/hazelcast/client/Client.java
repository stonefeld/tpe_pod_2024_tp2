package ar.edu.itba.pod.hazelcast.client;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public abstract class Client {

    // Required by all queries
    protected static String[] addresses;
    protected static String city, inPath, outPath;

    // Required by queries 3 and 4
    protected static Integer n;
    protected static String from, to, agency;

    // Optional arguments
    protected static String clusterName, clusterPassword;

    public static void processProperties() {
        addresses = System.getProperty("addresses", "").split(":");
        city = System.getProperty("city", "");
        inPath = System.getProperty("inPath", "");
        outPath = System.getProperty("outPath", "");

        if (addresses.length == 0) {
            throw new IllegalArgumentException("IP addresses are required");
        }

        if (city.compareTo("NYC") != 0 && city.compareTo("CHI") != 0) {
            throw new IllegalArgumentException("City is required");
        }

        if (inPath.isEmpty()) {
            throw new IllegalArgumentException("Input path is required");
        }

        if (outPath.isEmpty()) {
            throw new IllegalArgumentException("Output path is required");
        }

        String nStr = System.getProperty("n", "");
        from = System.getProperty("from", "");
        to = System.getProperty("to", "");
        agency = System.getProperty("agency", "").replaceAll("_", " ");

        if (!nStr.isEmpty()) {
            try {
                n = Integer.parseInt(nStr);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid value for n");
            }
        }

        clusterName = System.getProperty("clusterName", "g7-tpe2");
        clusterPassword = System.getProperty("clusterPassword", "g7-tpe2-pass");
    }

    public static HazelcastInstance getHazelcastInstance() {
        // Group Config
        GroupConfig groupConfig = new GroupConfig().setName(clusterName).setPassword(clusterPassword);

        // Client Network Config
        ClientNetworkConfig clientNetworkConfig = new ClientNetworkConfig();
        for (String address : addresses) {
            clientNetworkConfig.addAddress(address);
        }

        // Client Config
        ClientConfig clientConfig = new ClientConfig().setGroupConfig(groupConfig).setNetworkConfig(clientNetworkConfig);
        clientConfig.setInstanceName(clusterName);

        // Node Client
        return HazelcastClient.newHazelcastClient(clientConfig);
    }

    public static <T> void writeToCSV(String fileName, String header, Iterator<T> dataList, Function<T, String> csvLineMapper) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(header);

        while (dataList.hasNext()) {
            lines.add(csvLineMapper.apply(dataList.next()));
        }

        Files.write(Paths.get(outPath, fileName), lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

}
