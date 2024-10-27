package ar.edu.itba.pod.ytdcollection;

import ar.edu.itba.pod.utils.AgenciesMapUtils;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.Collator;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class YTDCollectionCollator implements Collator<Map.Entry<AgencyMonthYearTriplet, Integer>, SortedSet<YTDCollectionResult>> {

    private final HazelcastInstance hazelcastInstance;

    public YTDCollectionCollator(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    @Override
    public SortedSet<YTDCollectionResult> collate(Iterable<Map.Entry<AgencyMonthYearTriplet, Integer>> values) {
        IMap<String, Integer> agenciesMap = hazelcastInstance.getMap("agencies");

        SortedSet<YTDCollectionResult> result = new TreeSet<>(
                Comparator.comparing(YTDCollectionResult::agency)
                        .thenComparing(YTDCollectionResult::year)
                        .thenComparing(YTDCollectionResult::month)
        );

        for (Map.Entry<AgencyMonthYearTriplet, Integer> entry : values) {
            result.add(new YTDCollectionResult(
                    AgenciesMapUtils.getAgencyName(agenciesMap, entry.getKey().getAgencyId()),
                    entry.getKey().getYear(),
                    entry.getKey().getMonth(),
                    entry.getValue())
            );
        }

        return result;
    }

}
