package ar.edu.itba.pod.hazelcast.ytdcollection;

import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

public class YTDCollectionReducerFactory implements ReducerFactory<AgencyMonthYearTriplet, Integer, Integer> {

    @Override
    public Reducer<Integer, Integer> newReducer(AgencyMonthYearTriplet key) {
        return new YTDCollectionReducer();
    }

    private static class YTDCollectionReducer extends Reducer<Integer, Integer> {
        private int sum = 0;

        @Override
        public void reduce(Integer value) {
            sum += value;
        }

        @Override
        public Integer finalizeReduce() {
            return sum;
        }
    }
}
