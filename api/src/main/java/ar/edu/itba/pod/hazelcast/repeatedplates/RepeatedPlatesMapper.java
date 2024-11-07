package ar.edu.itba.pod.hazelcast.repeatedplates;

import ar.edu.itba.pod.hazelcast.common.CountyPlateInfractionTriplet;
import ar.edu.itba.pod.hazelcast.common.TicketRow;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

import java.time.LocalDate;

public class RepeatedPlatesMapper implements Mapper<LocalDate, CountyPlateInfractionTriplet, String, PlateNumberInfractionTriplet> {

    private static final long ONE = 1L;

    @Override
    public void map(LocalDate key, CountyPlateInfractionTriplet value, Context<String, PlateNumberInfractionTriplet> context) {
        context.emit(value.getCounty(), new PlateNumberInfractionTriplet(value.getPlateId(), value.getInfractionId(), ONE));
    }

}
