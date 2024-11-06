package ar.edu.itba.pod.hazelcast.ytdcollection;

import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

import java.util.SortedMap;
import java.util.TreeMap;

public class YTDCollectionReducerFactory implements ReducerFactory<AgencyYearPair, MonthAmountPair, SortedMap<Integer, Integer>> {

    @Override
    public Reducer<MonthAmountPair, SortedMap<Integer, Integer>> newReducer(AgencyYearPair key) {
        return new YTDCollectionReducer();
    }

    private static class YTDCollectionReducer extends Reducer<MonthAmountPair, SortedMap<Integer, Integer>> {

        private SortedMap<Integer, Integer> amountByMonth = new TreeMap<>();

        @Override
        public void reduce(MonthAmountPair value) {
//            sum += value.getAmount();
            amountByMonth.merge(value.getMonth(), value.getAmount(), Integer::sum);
        }

        @Override
        public SortedMap<Integer, Integer> finalizeReduce() {
            return amountByMonth;
        }

    }

}
