package ar.edu.itba.pod.hazelcast.ytdcollection;

import ar.edu.itba.pod.hazelcast.common.TicketRow;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

public class YTDCollectionMapper implements Mapper<String, TicketRow, AgencyYearPair, MonthAmountPair> {

    @Override
    public void map(String key, TicketRow value, Context<AgencyYearPair, MonthAmountPair> context) {
        IMap<String, Integer> agenciesMap = Hazelcast.getHazelcastInstanceByName("g2-tpe2").getMap("g2-agencies");
        context.emit(
                new AgencyYearPair(agenciesMap.get(value.getAgency()), value.getIssueDate().getYear()),
                new MonthAmountPair(value.getAmount(), value.getIssueDate().getMonthValue())
        );
    }

}
