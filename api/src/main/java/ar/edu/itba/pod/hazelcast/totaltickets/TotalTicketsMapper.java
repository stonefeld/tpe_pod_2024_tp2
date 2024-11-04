package ar.edu.itba.pod.hazelcast.totaltickets;

import ar.edu.itba.pod.hazelcast.common.TicketRow;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

public class TotalTicketsMapper implements Mapper<AgencyInfractionNamesPair, TicketRow, AgencyInfractionIdsPair, Long> {

    private static final long ONE = 1L;

    @Override
    public void map(AgencyInfractionNamesPair key, TicketRow value, Context<AgencyInfractionIdsPair, Long> context) {
        IMap<String, Integer> agenciesMap = Hazelcast.getHazelcastInstanceByName("g2-tpe2").getMap("g2-agencies");
        context.emit(new AgencyInfractionIdsPair(agenciesMap.get(value.getAgency()), value.getInfractionId()), ONE);
    }

}
