package ar.edu.itba.pod.hazelcast.client;

import ar.edu.itba.pod.hazelcast.common.TicketRow;
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
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public abstract class Client {

    private static final Function<Pair<String[], Integer>, TicketRow> mapperNYC = pair -> new TicketRow(pair.left[0], pair.left[1], pair.left[3], pair.left[5], (int) Double.parseDouble(pair.left[2]), pair.right, pair.left[4]);
    private static final Function<Pair<String[], Integer>, TicketRow> mapperCHI = pair -> new TicketRow(pair.left[3], pair.left[4], pair.left[2], pair.left[1], (int) Double.parseDouble(pair.left[5]), pair.right, pair.left[0]);

    // Required by all queries
    protected static String[] addresses;
    protected static String city, inPath, outPath;

    // Required by queries 3 and 4
    protected static Integer n;
    protected static String agency;
    protected static LocalDate from, to;

    // Optional arguments
    protected static String clusterName, clusterPassword;

    // Mapper to create TicketRow from each CSV line
    protected static Function<Pair<String[], Integer>, TicketRow> mapper;

    public static void processProperties() {
        addresses = System.getProperty("addresses", "").split(";");
        city = System.getProperty("city", "");
        inPath = System.getProperty("inPath", "");
        outPath = System.getProperty("outPath", "");

        if (addresses.length == 0)
            throw new IllegalArgumentException("IP addresses are required");

        mapper = switch(city) {
            case "NYC" -> mapperNYC;
            case "CHI" -> mapperCHI;
            default -> throw new IllegalArgumentException("A valid city is required");
        };

        if (inPath.isEmpty())
            throw new IllegalArgumentException("Input path is required");
        if (outPath.isEmpty())
            throw new IllegalArgumentException("Output path is required");

        String nStr = System.getProperty("n", "");
        String fromStr = System.getProperty("from", "");
        String toStr = System.getProperty("to", "");
        agency = System.getProperty("agency", "").replaceAll("_", " ");

        if (!nStr.isEmpty()) {
            try {
                n = Integer.parseInt(nStr);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid value for n");
            }
        }

        if (!fromStr.isEmpty()) {
            try {
                from = LocalDate.parse(fromStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } catch (DateTimeException e) {
                throw new IllegalArgumentException("Invalid value for from");
            }
        }

        if (!toStr.isEmpty()) {
            try {
                to = LocalDate.parse(toStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } catch (DateTimeException e) {
                throw new IllegalArgumentException("Invalid value for to");
            }
        }

        clusterName = System.getProperty("clusterName", "g2-tpe2");
        clusterPassword = System.getProperty("clusterPassword", "g2-tpe2-pass");
    }

    public static HazelcastInstance getHazelcastInstance() {
        // Group Config
        GroupConfig groupConfig = new GroupConfig().setName(clusterName).setPassword(clusterPassword);

        // Client Network Config
        ClientNetworkConfig clientNetworkConfig = new ClientNetworkConfig();
        for (String address : addresses)
            clientNetworkConfig.addAddress(address);

        // Client Config
        ClientConfig clientConfig = new ClientConfig().setGroupConfig(groupConfig).setNetworkConfig(clientNetworkConfig);
        clientConfig.setInstanceName(clusterName);

        // Node Client
        return HazelcastClient.newHazelcastClient(clientConfig);
    }

    public static <T> void writeToCSV(String fileName, String header, Iterator<T> dataList, Function<T, String> csvLineMapper) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(header);
        while (dataList.hasNext())
            lines.add(csvLineMapper.apply(dataList.next()));
        Files.write(Paths.get(outPath, fileName), lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public static Logger setUpLogger(Class<?> clazz, String fileName) {
        if (outPath.isEmpty())
            throw new IllegalArgumentException("Output path is required");
        System.setProperty("log4j.outputname", Paths.get(outPath, fileName).toString());
        return LoggerFactory.getLogger(clazz);
    }

    public static class Pair<L, R> {

        private final L left;
        private final R right;

        public Pair(L left, R right) {
            this.left = left;
            this.right = right;
        }

    }

}
