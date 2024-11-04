package ar.edu.itba.pod.hazelcast.maxticketdifference;

import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;

public class MaxTicketDifferenceCombinerFactory implements CombinerFactory<String, Integer, IntegerPair> {

        @Override
        public MaxTicketDifferenceCombiner newCombiner(String key) {
            return new MaxTicketDifferenceCombiner();
        }

        private static class MaxTicketDifferenceCombiner extends Combiner<Integer, IntegerPair> {

            private Integer min = null, max = null;

            @Override
            public void reset() {
                min = null;
                max = null;
            }

            @Override
            public void combine(Integer value) {
                if (max == null || value > max)
                    max = value;
                if (min == null || value < min)
                    min = value;
            }

            @Override
            public IntegerPair finalizeChunk() {
                return new IntegerPair(max, min);
            }

        }
}
