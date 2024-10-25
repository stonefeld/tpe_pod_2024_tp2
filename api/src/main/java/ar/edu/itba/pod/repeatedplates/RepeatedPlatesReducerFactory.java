package ar.edu.itba.pod.repeatedplates;

import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

import java.util.HashMap;
import java.util.Map;

public class RepeatedPlatesReducerFactory implements ReducerFactory<String, PlateNumberPair, Double> {

    @Override
    public Reducer<PlateNumberPair, Double> newReducer(String key) {
        return new RepeatedPlatesReducer();
    }

    private static class RepeatedPlatesReducer extends Reducer<PlateNumberPair, Double> {
        private final Map<String, Long> map = new HashMap<>();

        @Override
        public void reduce(PlateNumberPair value) {
            String plate = value.getPlate();
            long count = value.getCount();

            if (map.containsKey(plate)) {
                map.put(plate, map.get(plate) + count);
            } else {
                map.put(plate, count);
            }
        }

        @Override
        public Double finalizeReduce() {
            double toTruncate = (map.values().stream().filter(value -> value > 1).count() / (double) map.size()) * 100;
            return Math.floor(toTruncate * 100) / 100;
        }
    }
}
