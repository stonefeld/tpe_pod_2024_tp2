package ar.edu.itba.pod.hazelcast.totaltickets;

import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;

public class TotalTicketsCombinerFactory implements CombinerFactory<AgencyInfractionIdsPair, Long, Long> {

    @Override
    public Combiner<Long, Long> newCombiner(AgencyInfractionIdsPair key) {
        return new TotalTicketsCombiner();
    }

    private static class TotalTicketsCombiner extends Combiner<Long, Long> {

        private Long sum = 0L;

        @Override
        public void reset() {
            sum = 0L;
        }

        @Override
        public void combine(Long value) {
            sum += value;
        }

        @Override
        public Long finalizeChunk() {
            return sum;
        }

    }

}
