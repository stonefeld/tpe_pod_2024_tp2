package ar.edu.itba.pod.maxticketdifference;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.hazelcast.mapreduce.Collator;

public class MaxTicketDifferenceCollator implements Collator<Map.Entry<String, IntegerPair>, SortedSet<MaxTicketDifferenceResult>> {
    private final int n;

    public MaxTicketDifferenceCollator(int n) {
        this.n = n;
    }

    @Override
    public SortedSet<MaxTicketDifferenceResult> collate(Iterable<Map.Entry<String, IntegerPair>> values){
        TreeSet<MaxTicketDifferenceResult> result = new TreeSet<>(
                Comparator.comparing(MaxTicketDifferenceResult::difference).reversed()
                        .thenComparing(MaxTicketDifferenceResult::infractionId)
        );

        for (Map.Entry<String, IntegerPair> entry : values) {
            result.add(new MaxTicketDifferenceResult(
                    entry.getKey(),
                    entry.getValue().getMax(),
                    entry.getValue().getMin(),
                    entry.getValue().getDifference()
                    )
            );
        }

        // TODO: Revisar
        SortedSet<MaxTicketDifferenceResult> top = new TreeSet<>(
                Comparator.comparing(MaxTicketDifferenceResult::difference).reversed()
                        .thenComparing(MaxTicketDifferenceResult::infractionId)
        );

        for (int i = 0; i < n; i++) {
            top.add(result.pollFirst());
        }

        return top;
    }
}
