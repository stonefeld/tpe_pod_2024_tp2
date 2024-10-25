package ar.edu.itba.pod.repeatedplates;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;

public class PlateNumberPair implements DataSerializable {
    private String plate;
    private long count;

    public PlateNumberPair() {
    }

    public PlateNumberPair(String plate, long count) {
        this.plate = plate;
        this.count = count;
    }

    public String getPlate() {
        return plate;
    }

    public long getCount() {
        return count;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(plate);
        out.writeLong(count);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        plate = in.readUTF();
        count = in.readLong();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PlateNumberPair plateNumberPair && plate.equals(plateNumberPair.plate);
    }

    @Override
    public int hashCode() {
        return plate.hashCode();
    }
}
