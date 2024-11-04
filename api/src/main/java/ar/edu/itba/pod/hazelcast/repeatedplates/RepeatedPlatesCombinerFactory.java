package ar.edu.itba.pod.hazelcast.repeatedplates;

import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;

import java.util.HashMap;
import java.util.Map;

public class RepeatedPlatesCombinerFactory implements CombinerFactory<String, PlateNumberInfractionTriplet, Map<String, Long>> {

    @Override
    public Combiner<PlateNumberInfractionTriplet, Map<String, Long>> newCombiner(String key) {
        return new RepeatedPlatesCombiner();
    }

    private static class RepeatedPlatesCombiner extends Combiner<PlateNumberInfractionTriplet, Map<String, Long>> {

        private final Map<String, Long> localCountByInfraction = new HashMap<>();

        @Override
        public void reset() {
            localCountByInfraction.clear();
        }

        @Override
        public void combine(PlateNumberInfractionTriplet value) {
            String infractionId = value.getInfractionId();
            long count = value.getCount();

            localCountByInfraction.merge(infractionId, count, Long::sum);
        }

        @Override
        public Map<String, Long> finalizeChunk() {
            // TODO: Check if this is the correct way to return
            return new HashMap<>(localCountByInfraction);
        }
    }
}
