package ar.edu.itba.pod.hazelcast.maxticketdifference;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.Collator;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class MaxTicketDifferenceCollator implements Collator<Map.Entry<String, IntegerPair>, SortedSet<MaxTicketDifferenceResult>> {

    private final HazelcastInstance hazelcastInstance;
    private final int n;

    public MaxTicketDifferenceCollator(HazelcastInstance hazelcastInstance, int n) {
        this.hazelcastInstance = hazelcastInstance;
        this.n = n;
    }

    @Override
    public SortedSet<MaxTicketDifferenceResult> collate(Iterable<Map.Entry<String, IntegerPair>> values) {
        IMap<String, String> infractionsMap = hazelcastInstance.getMap("g2-infractions");
        Comparator<MaxTicketDifferenceResult> cmp = Comparator
                .comparing(MaxTicketDifferenceResult::difference).reversed()
                .thenComparing(MaxTicketDifferenceResult::infraction);

        SortedSet<MaxTicketDifferenceResult> result = new TreeSet<>(cmp);
        SortedSet<MaxTicketDifferenceResult> top = new TreeSet<>(cmp);

        for (Map.Entry<String, IntegerPair> entry : values) {
            result.add(new MaxTicketDifferenceResult(
                    infractionsMap.get(entry.getKey()),
                    entry.getValue().getMax(),
                    entry.getValue().getMin(),
                    entry.getValue().getDifference()
            ));
        }

        for (int i = 0; i < Math.min(n, result.size()); i++) {
            top.add(result.removeFirst());
        }

        return top;
    }

}
