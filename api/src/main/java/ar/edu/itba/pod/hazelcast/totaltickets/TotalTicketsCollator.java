package ar.edu.itba.pod.hazelcast.totaltickets;

import ar.edu.itba.pod.hazelcast.utils.AgenciesMapUtils;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.Collator;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class TotalTicketsCollator implements Collator<Map.Entry<AgencyInfractionPair, Long>, SortedSet<TotalTicketsResult>> {

    private final HazelcastInstance hazelcastInstance;

    public TotalTicketsCollator(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    @Override
    public SortedSet<TotalTicketsResult> collate(Iterable<Map.Entry<AgencyInfractionPair, Long>> values) {
        IMap<String, Integer> agenciesMap = hazelcastInstance.getMap("g2-agencies");
        IMap<String, String> infractionsMap = hazelcastInstance.getMap("g2-infractions");

        SortedSet<TotalTicketsResult> result = new TreeSet<>(
                Comparator.comparing(TotalTicketsResult::totalTickets).reversed()
                        .thenComparing(TotalTicketsResult::infraction)
                        .thenComparing(TotalTicketsResult::agency)
        );

        for (Map.Entry<AgencyInfractionPair, Long> entry : values) {
            result.add(new TotalTicketsResult(
                    AgenciesMapUtils.getAgencyName(agenciesMap, entry.getKey().getAgencyId()),
                    infractionsMap.get(entry.getKey().getInfractionId()), entry.getValue()
            ));
        }

        return result;
    }

}
