package ar.edu.itba.pod.ytdcollection;

import ar.edu.itba.pod.common.TicketRow;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

public class YTDCollectionMapper implements Mapper<String, TicketRow,AgencyMonthYearTriplet, Integer> {

    @Override
    public void map(String key, TicketRow value, Context<AgencyMonthYearTriplet, Integer> context) {
        for (int i = value.getIssueDate().getMonthValue(); i<= 12; i++) {
            context.emit(new AgencyMonthYearTriplet(value.getAgency(), i, value.getIssueDate().getYear()), value.getAmount());
        }
    }
}
