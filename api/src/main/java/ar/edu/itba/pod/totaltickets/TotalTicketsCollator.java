package ar.edu.itba.pod.totaltickets;

import com.hazelcast.mapreduce.Collator;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class TotalTicketsCollator implements Collator<Map.Entry<AgencyInfractionPair, Long>, SortedSet<TotalTicketsResult>> {
    @Override
    public SortedSet<TotalTicketsResult> collate(Iterable<Map.Entry<AgencyInfractionPair, Long>> values) {
        SortedSet<TotalTicketsResult> result = new TreeSet<>(
                Comparator.comparing(TotalTicketsResult::totalTickets).reversed()
                        .thenComparing(TotalTicketsResult::infraction)
                        .thenComparing(TotalTicketsResult::agency)
        );

        for (Map.Entry<AgencyInfractionPair, Long> entry : values) {
            result.add(new TotalTicketsResult(
                    entry.getKey().getAgency(),
                    entry.getKey().getInfractionId(),
                    entry.getValue())
            );
        }

        return result;
    }
}
