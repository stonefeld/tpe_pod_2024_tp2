package ar.edu.itba.pod.hazelcast.ytdcollection;

import ar.edu.itba.pod.hazelcast.common.AgencyIssueDateAmountTriplet;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

public class YTDCollectionMapper implements Mapper<String, AgencyIssueDateAmountTriplet, AgencyYearPair, MonthAmountPair> {

    @Override
    public void map(String key, AgencyIssueDateAmountTriplet value, Context<AgencyYearPair, MonthAmountPair> context) {
        IMap<String, Integer> agenciesMap = Hazelcast.getHazelcastInstanceByName("g2-tpe2").getMap("g2-agencies");
        context.emit(
                new AgencyYearPair(agenciesMap.get(value.getAgency()), value.getIssueDate().getYear()),
                new MonthAmountPair((int) value.getAmount(), value.getIssueDate().getMonthValue())
        );
    }

}
