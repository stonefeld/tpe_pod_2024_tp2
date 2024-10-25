package ar.edu.itba.pod.repeatedplates;

import ar.edu.itba.pod.common.TicketRow;
import com.hazelcast.core.MultiMap;
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
        return key != null && key.isAfter(from) && key.isBefore(to);
    }

}

