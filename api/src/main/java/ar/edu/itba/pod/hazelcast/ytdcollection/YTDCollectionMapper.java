package ar.edu.itba.pod.hazelcast.ytdcollection;

import ar.edu.itba.pod.hazelcast.common.TicketRow;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

public class YTDCollectionMapper implements Mapper<String, TicketRow, AgencyMonthYearTriplet, Integer> {

    @Override
    public void map(String key, TicketRow value, Context<AgencyMonthYearTriplet, Integer> context) {
        IMap<String, Integer> agenciesMap = Hazelcast.getHazelcastInstanceByName("g7-tpe2").getMap("agencies");
        for (int i = value.getIssueDate().getMonthValue(); i <= 12; i++) {
            context.emit(new AgencyMonthYearTriplet(agenciesMap.get(value.getAgency()), i, value.getIssueDate().getYear()), value.getAmount());
        }
    }

}
