package ar.edu.itba.pod.repeatedplates;

import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

import java.util.HashMap;
import java.util.Map;

public class RepeatedPlatesReducerFactory implements ReducerFactory<String, PlateNumberInfractionTriplet, Double> {
    private final int n;

    public RepeatedPlatesReducerFactory(int n) {
        this.n = n;
    }

    @Override
    public Reducer<PlateNumberInfractionTriplet, Double> newReducer(String key) {
        return new RepeatedPlatesReducer(n);
    }

    private static class RepeatedPlatesReducer extends Reducer<PlateNumberInfractionTriplet, Double> {
        private final Map<String, Map<String, Long>> map = new HashMap<>();
        private final int n;

        public RepeatedPlatesReducer(int n) {
            this.n = n;
        }

        @Override
        public void reduce(PlateNumberInfractionTriplet value) {
            String plate = value.getPlate();
            long count = value.getCount();
            String infractionId = value.getInfractionId();

            map.putIfAbsent(plate, new HashMap<>());
            if (map.get(plate).containsKey(infractionId)) {
                map.get(plate).put(infractionId, map.get(plate).get(infractionId) + count);
            } else {
                map.get(plate).put(infractionId, count);
            }
        }

        @Override
        public Double finalizeReduce() {
            double repeatedPlates = 0;
            for (Map.Entry<String, Map<String, Long>> entry : map.entrySet()) {
                for (Map.Entry<String, Long> innerEntry : entry.getValue().entrySet()) {
                    if (innerEntry.getValue() >= n) {
                        repeatedPlates++;
                        break;
                    }
                }
            }

            double toTruncate = (repeatedPlates / (double) map.size()) * 100;
            return Math.floor(toTruncate * 100) / 100;
        }
    }
}
