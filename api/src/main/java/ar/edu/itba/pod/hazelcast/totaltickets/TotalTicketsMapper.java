package ar.edu.itba.pod.hazelcast.totaltickets;

import ar.edu.itba.pod.hazelcast.common.TicketRow;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

public class TotalTicketsMapper implements Mapper<String, TicketRow, AgencyInfractionPair, Long> {

    private static final long ONE = 1L;

    @Override
    public void map(String key, TicketRow value, Context<AgencyInfractionPair, Long> context) {
        IMap<String, Integer> agenciesMap = Hazelcast.getHazelcastInstanceByName("g2-tpe2").getMap("g2-agencies");
        context.emit(new AgencyInfractionPair(agenciesMap.get(value.getAgency()), value.getInfractionId()), ONE);
    }

}
