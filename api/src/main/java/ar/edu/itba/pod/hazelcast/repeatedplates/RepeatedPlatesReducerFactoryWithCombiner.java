package ar.edu.itba.pod.hazelcast.repeatedplates;

import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

import java.util.HashMap;
import java.util.Map;

public class RepeatedPlatesReducerFactoryWithCombiner implements ReducerFactory<String, Map<String, Map<String, Long>>, Double> {

    private final int n;

    public RepeatedPlatesReducerFactoryWithCombiner(int n) {
        this.n = n;
    }

    @Override
    public Reducer<Map<String, Map<String, Long>>, Double> newReducer(String key) {
        return new RepeatedPlatesReducerWithCombiner(n);
    }

    private static class RepeatedPlatesReducerWithCombiner extends Reducer<Map<String, Map<String, Long>>, Double> {

        private final Map<String, Map<String, Long>> repeatedInfractionsByPlate = new HashMap<>();
        private final int n;

        public RepeatedPlatesReducerWithCombiner(int n) {
            this.n = n;
        }

        @Override
        public void reduce(Map<String, Map<String, Long>> value) {
            for (Map.Entry<String, Map<String, Long>> plateEntry : value.entrySet()) {
                String plate = plateEntry.getKey();
                Map<String, Long> infractionMap = plateEntry.getValue();

                repeatedInfractionsByPlate.computeIfAbsent(plate, k -> new HashMap<>());
                for (Map.Entry<String, Long> infractionEntry : infractionMap.entrySet()) {
                    repeatedInfractionsByPlate.get(plate).merge(infractionEntry.getKey(), infractionEntry.getValue(), Long::sum);
                }
            }
        }

        @Override
        public Double finalizeReduce() {
            double repeatedPlates = 0;
            for (Map.Entry<String, Map<String, Long>> plateEntry : repeatedInfractionsByPlate.entrySet()) {
                for (Map.Entry<String, Long> infractionEntry : plateEntry.getValue().entrySet()) {
                    if (infractionEntry.getValue() >= n) {
                        repeatedPlates++;
                        break;
                    }
                }
            }

            double toTruncate = (repeatedPlates / (double) repeatedInfractionsByPlate.size()) * 100;
            return Math.floor(toTruncate * 100) / 100;
        }

    }

}
