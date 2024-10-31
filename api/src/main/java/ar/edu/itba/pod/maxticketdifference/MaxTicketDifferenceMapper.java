package ar.edu.itba.pod.maxticketdifference;

import ar.edu.itba.pod.common.TicketRow;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

public class MaxTicketDifferenceMapper implements Mapper<String, TicketRow, String, Integer> {

    @Override
    public void map(String key, TicketRow value, Context<String, Integer> context) {
        context.emit(value.getInfractionId(), value.getAmount());
    }

}
