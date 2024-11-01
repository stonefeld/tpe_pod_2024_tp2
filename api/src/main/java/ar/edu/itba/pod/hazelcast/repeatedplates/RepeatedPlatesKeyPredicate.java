package ar.edu.itba.pod.hazelcast.repeatedplates;

import com.hazelcast.mapreduce.KeyPredicate;

import java.time.LocalDate;

public class RepeatedPlatesKeyPredicate implements KeyPredicate<LocalDate> {

    private final LocalDate from, to;

    public RepeatedPlatesKeyPredicate(LocalDate from, LocalDate to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean evaluate(LocalDate key) {
        return key != null && ((key.isAfter(from) && key.isBefore(to)) || key.equals(from) || key.equals(to));
    }

}

