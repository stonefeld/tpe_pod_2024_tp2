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

        private final Map<String, Map<String, Long>> repeatedInfractionsByPlate = new HashMap<>();
        private final int n;

        public RepeatedPlatesReducer(int n) {
            this.n = n;
        }

        @Override
        public void reduce(PlateNumberInfractionTriplet value) {
            String plate = value.getPlate();
            String infractionId = value.getInfractionId();
            long count = value.getCount();

//            repeatedInfractionsByPlate.putIfAbsent(plate, new HashMap<>());
//            if (repeatedInfractionsByPlate.get(plate).containsKey(infraction)) {
//                repeatedInfractionsByPlate.get(plate).put(infraction, repeatedInfractionsByPlate.get(plate).get(infraction) + count);
//            } else {
//                repeatedInfractionsByPlate.get(plate).put(infraction, count);
//            }

            repeatedInfractionsByPlate.computeIfAbsent(plate, k -> new HashMap<>());
            repeatedInfractionsByPlate.get(plate).merge(infractionId, count, Long::sum);
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
