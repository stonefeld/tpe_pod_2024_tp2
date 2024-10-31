package ar.edu.itba.pod.repeatedplates;

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
        return key != null
                && (key.isAfter(from) || key.isEqual(from))
                && (key.isBefore(to) || key.isEqual(to));
    }

}

