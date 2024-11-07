package ar.edu.itba.pod.hazelcast.ytdcollection;

import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

import java.util.SortedMap;
import java.util.TreeMap;

public class YTDCollectionReducerFactoryWithCombiner implements ReducerFactory<AgencyYearPair, SortedMap<Integer, Integer>, SortedMap<Integer, Integer>> {

    @Override
    public Reducer<SortedMap<Integer, Integer>, SortedMap<Integer, Integer>> newReducer(AgencyYearPair key) {
        return new YTDCollectionReducerWithCombiner();
    }

    private static class YTDCollectionReducerWithCombiner extends Reducer<SortedMap<Integer, Integer>, SortedMap<Integer, Integer>> {

        private final SortedMap<Integer, Integer> amountByMonth = new TreeMap<>();

        @Override
        public void beginReduce() {
            amountByMonth.clear();
        }

        @Override
        public void reduce(SortedMap<Integer, Integer> value) {
            if (value != null) {
                for (Integer month : value.keySet()) {
                    amountByMonth.merge(month, value.get(month), Integer::sum);
                }
            }
        }

        @Override
        public SortedMap<Integer, Integer> finalizeReduce() {
            return amountByMonth;
        }

    }

}
