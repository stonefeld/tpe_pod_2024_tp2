package ar.edu.itba.pod.hazelcast.repeatedplates;

import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;

import java.util.HashMap;
import java.util.Map;

public class RepeatedPlatesCombinerFactory implements CombinerFactory<String, PlateNumberInfractionTriplet, Map<String, Map<String, Long>>> {

    @Override
    public Combiner<PlateNumberInfractionTriplet, Map<String, Map<String, Long>>> newCombiner(String key) {
        return new RepeatedPlatesCombiner();
    }

    private static class RepeatedPlatesCombiner extends Combiner<PlateNumberInfractionTriplet, Map<String, Map<String, Long>>> {

        private final Map<String, Map<String, Long>> localCountByInfraction = new HashMap<>();

        @Override
        public void reset() {
            localCountByInfraction.clear();
        }

        @Override
        public void combine(PlateNumberInfractionTriplet value) {
            String infractionId = value.getInfractionId();
            String plate = value.getPlate();
            long count = value.getCount();

            localCountByInfraction.computeIfAbsent(plate, k -> new HashMap<>());
            localCountByInfraction.get(plate).merge(infractionId, count, Long::sum);
        }

        @Override
        public Map<String, Map<String, Long>> finalizeChunk() {
            return new HashMap<>(localCountByInfraction);
        }
    }
}
