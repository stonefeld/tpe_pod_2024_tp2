package ar.edu.itba.pod.repeatedplates;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;
import java.util.Objects;

public class PlateNumberInfractionTriplet implements DataSerializable {

    private String plate, infractionId;
    private long count;

    public PlateNumberInfractionTriplet() {
    }

    public PlateNumberInfractionTriplet(String plate, String infractionId, long count) {
        this.plate = plate;
        this.infractionId = infractionId;
        this.count = count;
    }

    public String getPlate() {
        return plate;
    }

    public long getCount() {
        return count;
    }

    public String getInfractionId() {
        return infractionId;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(plate);
        out.writeLong(count);
        out.writeUTF(infractionId);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        plate = in.readUTF();
        count = in.readLong();
        infractionId = in.readUTF();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PlateNumberInfractionTriplet plateNumberPair &&
                plate.equals(plateNumberPair.plate) && count == plateNumberPair.count &&
                infractionId.equals(plateNumberPair.infractionId
                );
    }

    @Override
    public int hashCode() {
        return Objects.hash(plate, count, infractionId);
    }

}
