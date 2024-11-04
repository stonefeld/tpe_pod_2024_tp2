package ar.edu.itba.pod.hazelcast.maxticketdifference;

import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

public class MaxTicketDifferenceReducerFactoryWithCombiner implements ReducerFactory<String, IntegerPair, IntegerPair> {

    @Override
    public Reducer<IntegerPair, IntegerPair> newReducer(String key) {
        return new MaxTicketDifferenceReducerWithCombiner();
    }

    private static class MaxTicketDifferenceReducerWithCombiner extends Reducer<IntegerPair, IntegerPair> {

        private Integer min = null, max = null;

        @Override
        public void reduce(IntegerPair pair) {
            if (max == null || pair.getMax() > max)
                max = pair.getMax();
            if (min == null || pair.getMin() < min)
                min = pair.getMin();
        }

        @Override
        public IntegerPair finalizeReduce() {
            return new IntegerPair(max, min);
        }

    }

}
