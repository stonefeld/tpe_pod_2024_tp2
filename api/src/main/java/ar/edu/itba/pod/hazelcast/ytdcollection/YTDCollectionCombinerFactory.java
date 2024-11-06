package ar.edu.itba.pod.hazelcast.ytdcollection;

import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;

public class YTDCollectionCombinerFactory implements CombinerFactory<AgencyYearPair, Integer, Integer> {

    @Override
    public Combiner<Integer, Integer> newCombiner(AgencyYearPair key) {
        return new YTDCollectionCombiner();
    }

    private static class YTDCollectionCombiner extends Combiner<Integer, Integer> {
        private int sum = 0;

        @Override
        public void reset() {
            sum = 0;
        }

        @Override
        public void combine(Integer value) {
            sum += value;
        }

        @Override
        public Integer finalizeChunk() {
            return sum;
        }
    }

}
