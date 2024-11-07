package ar.edu.itba.pod.hazelcast.maxticketdifference;

import ar.edu.itba.pod.hazelcast.common.InfractionAmountPair;
import ar.edu.itba.pod.hazelcast.common.TicketRow;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

public class MaxTicketDifferenceMapper implements Mapper<String, InfractionAmountPair, String, Integer> {

    @Override
    public void map(String key, InfractionAmountPair value, Context<String, Integer> context) {
        context.emit(value.getInfractionId(), value.getAmount());
    }

}
