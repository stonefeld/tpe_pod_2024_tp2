package ar.edu.itba.pod.totaltickets;

import ar.edu.itba.pod.common.TicketRow;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

public class TotalTicketsMapper implements Mapper<String, TicketRow, AgencyInfractionPair, Long> {
    private static final long ONE = 1L;

    @Override
    public void map(String key, TicketRow value, Context<AgencyInfractionPair, Long> context) {
        context.emit(new AgencyInfractionPair(value.getAgency(), value.getInfractionId()), ONE);
    }
}
