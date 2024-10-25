package ar.edu.itba.pod.repeatedplates;

import ar.edu.itba.pod.common.TicketRow;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

import java.time.LocalDate;

public class RepeatedPlatesMapper implements Mapper<LocalDate, TicketRow, String, PlateNumberPair> {
    private static final long ONE = 1L;

    @Override
    public void map(LocalDate key, TicketRow value, Context<String, PlateNumberPair> context) {
        context.emit(value.getCounty(), new PlateNumberPair(value.getPlateId(), ONE));
    }
}
