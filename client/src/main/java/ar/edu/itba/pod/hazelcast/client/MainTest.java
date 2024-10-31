package ar.edu.itba.pod.hazelcast.client;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainTest {

    public static void main(String[] args) {
        LocalDate date;
        String a = "2013-12-02";

        date = LocalDateTime.parse(a, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toLocalDate();
        System.out.println(date);
    }

}
