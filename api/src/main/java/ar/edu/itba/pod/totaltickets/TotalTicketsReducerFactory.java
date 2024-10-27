package ar.edu.itba.pod.totaltickets;

import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

public class TotalTicketsReducerFactory implements ReducerFactory<AgencyInfractionPair, Long, Long> {

    @Override
    public Reducer<Long, Long> newReducer(AgencyInfractionPair key) {
        return new TotalTicketsReducer();
    }

    private static class TotalTicketsReducer extends Reducer<Long, Long> {

        private long count = 0L;

        @Override
        public void reduce(Long value) {
            count += value;
        }

        @Override
        public Long finalizeReduce() {
            return count;
        }

    }

}
