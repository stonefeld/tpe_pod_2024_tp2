package ar.edu.itba.pod.hazelcast.repeatedplates;

import com.hazelcast.mapreduce.Collator;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class RepeatedPlatesCollator implements Collator<Map.Entry<String, Double>, SortedSet<RepeatedPlatesResult>> {

    @Override
    public SortedSet<RepeatedPlatesResult> collate(Iterable<Map.Entry<String, Double>> values) {
        SortedSet<RepeatedPlatesResult> result = new TreeSet<>(Comparator
                .comparing(RepeatedPlatesResult::percentage).reversed()
                .thenComparing(RepeatedPlatesResult::county));

        for (Map.Entry<String, Double> entry : values) {
            if (entry.getValue() != 0) {
                result.add(new RepeatedPlatesResult(
                        entry.getKey(),
                        entry.getValue()
                ));
            }
        }

        return result;
    }

}
