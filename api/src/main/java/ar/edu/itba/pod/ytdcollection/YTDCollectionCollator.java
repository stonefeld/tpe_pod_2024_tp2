package ar.edu.itba.pod.ytdcollection;

import com.hazelcast.mapreduce.Collator;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class YTDCollectionCollator implements Collator<Map.Entry<AgencyMonthYearTriplet, Integer>, SortedSet<YTDCollectionResult>> {
    @Override
    public SortedSet<YTDCollectionResult> collate(Iterable<Map.Entry<AgencyMonthYearTriplet, Integer>> values) {
        SortedSet<YTDCollectionResult> result = new TreeSet<>(
                Comparator.comparing(YTDCollectionResult::agency)
                        .thenComparing(YTDCollectionResult::year)
                        .thenComparing(YTDCollectionResult::month)
        );

        for (Map.Entry<AgencyMonthYearTriplet, Integer> entry : values) {
            result.add(new YTDCollectionResult(
                    entry.getKey().getAgency(),
                    entry.getKey().getYear(),
                    entry.getKey().getMonth(),
                    entry.getValue())
            );
        }

        return result;
    }
}
