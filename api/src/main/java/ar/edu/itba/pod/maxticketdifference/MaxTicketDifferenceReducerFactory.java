package ar.edu.itba.pod.maxticketdifference;

import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

public class MaxTicketDifferenceReducerFactory implements ReducerFactory<String, Integer, IntegerPair> {

    @Override
    public Reducer<Integer, IntegerPair> newReducer(String key) {
        return new MaxTicketDifferenceReducer();
    }

    private static class MaxTicketDifferenceReducer extends Reducer<Integer, IntegerPair> {
        private Integer max = 0;
        private Integer min = 0;

        @Override
        public void reduce(Integer value) {
            if (value > max) {
                max = value;
            }
            if (value < min) {
                min = value;
            }
        }

        @Override
        public IntegerPair finalizeReduce() {
            return new IntegerPair(max, min);
        }
    }
}
