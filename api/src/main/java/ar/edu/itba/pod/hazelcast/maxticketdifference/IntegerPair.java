package ar.edu.itba.pod.hazelcast.maxticketdifference;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;

public class IntegerPair implements DataSerializable {

    private Integer min, max;

    public IntegerPair() {
    }

    public IntegerPair(Integer max, Integer min) {
        this.max = max;
        this.min = min;
    }

    public Integer getMax() {
        return max;
    }

    public Integer getMin() {
        return min;
    }

    public Integer getDifference() {
        return max - min;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
//        out.writeInt(max);
//        out.writeInt(min);
        out.writeObject(max);
        out.writeObject(min);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
//        max = in.readInt();
//        min = in.readInt();
        max = in.readObject();
        min = in.readObject();
    }

}
