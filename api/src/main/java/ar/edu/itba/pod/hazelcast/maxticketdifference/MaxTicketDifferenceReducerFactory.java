package ar.edu.itba.pod.hazelcast.maxticketdifference;

import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

public class MaxTicketDifferenceReducerFactory implements ReducerFactory<String, Integer, IntegerPair> {

    @Override
    public Reducer<Integer, IntegerPair> newReducer(String key) {
        return new MaxTicketDifferenceReducer();
    }

    private static class MaxTicketDifferenceReducer extends Reducer<Integer, IntegerPair> {

        private Integer min = null, max = null;

        @Override
        public void reduce(Integer value) {
            if (max == null || value > max)
                max = value;
            if (min == null || value < min)
                min = value;
        }

        @Override
        public IntegerPair finalizeReduce() {
            return new IntegerPair(max, min);
        }

    }

}
