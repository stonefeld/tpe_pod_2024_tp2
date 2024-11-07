package ar.edu.itba.pod.hazelcast.ytdcollection;

import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;

import java.util.SortedMap;
import java.util.TreeMap;

public class YTDCollectionCombinerFactory implements CombinerFactory<AgencyYearPair, MonthAmountPair, SortedMap<Integer, Integer>> {

    @Override
    public Combiner<MonthAmountPair, SortedMap<Integer, Integer>> newCombiner(AgencyYearPair key) {
        return new YTDCollectionCombiner();
    }

    private static class YTDCollectionCombiner extends Combiner<MonthAmountPair, SortedMap<Integer, Integer>> {

        private final SortedMap<Integer, Integer> monthMap = new TreeMap<>();

        @Override
        public void reset() {
            monthMap.clear();
        }

        @Override
        public void combine(MonthAmountPair value) {
            monthMap.merge(value.getMonth(), value.getAmount(), Integer::sum);
        }

        @Override
        public SortedMap<Integer, Integer> finalizeChunk() {
            return new TreeMap<>(monthMap);
        }

    }

}
