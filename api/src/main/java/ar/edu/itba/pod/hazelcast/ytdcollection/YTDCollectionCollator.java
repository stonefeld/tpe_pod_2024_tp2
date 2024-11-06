package ar.edu.itba.pod.hazelcast.ytdcollection;

import ar.edu.itba.pod.hazelcast.utils.AgenciesMapUtils;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.Collator;

import java.util.*;

public class YTDCollectionCollator implements Collator<Map.Entry<AgencyYearPair, SortedMap<Integer, Integer>>, SortedSet<YTDCollectionResult>> {

    private final HazelcastInstance hazelcastInstance;

    public YTDCollectionCollator(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    @Override
    public SortedSet<YTDCollectionResult> collate(Iterable<Map.Entry<AgencyYearPair, SortedMap<Integer, Integer>>> values) {
        IMap<String, Integer> agenciesMap = hazelcastInstance.getMap("g2-agencies");

        SortedSet<YTDCollectionResult> result = new TreeSet<>(
                Comparator.comparing(YTDCollectionResult::agency)
                        .thenComparing(YTDCollectionResult::year)
                        .thenComparing(YTDCollectionResult::month)
        );

        for (Map.Entry<AgencyYearPair, SortedMap<Integer, Integer>> entry : values) {
            int year = entry.getKey().getYear(), sum = 0;
            String agencyName = AgenciesMapUtils.getAgencyName(agenciesMap, entry.getKey().getAgencyId());

            for (Map.Entry<Integer, Integer> monthEntry : entry.getValue().entrySet()) {
                sum += monthEntry.getValue();
                result.add(new YTDCollectionResult(agencyName, year, monthEntry.getKey(), sum));
            }
        }

        return result;
    }

}
