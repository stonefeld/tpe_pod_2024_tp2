package ar.edu.itba.pod.hazelcast.common;

import com.hazelcast.nio.serialization.DataSerializable;

import java.util.Objects;

public class InfractionAmountPair implements DataSerializable {

    private String infractionId;
    private int id, amount;

    public InfractionAmountPair() {
    }

    public InfractionAmountPair(int id, String infractionId, int amount) {
        this.id = id;
        this.infractionId = infractionId;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public String getInfractionId() {
        return infractionId;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public void writeData(com.hazelcast.nio.ObjectDataOutput out) throws java.io.IOException {
        out.writeInt(id);
        out.writeUTF(infractionId);
        out.writeInt(amount);
    }

    @Override
    public void readData(com.hazelcast.nio.ObjectDataInput in) throws java.io.IOException {
        id = in.readInt();
        infractionId = in.readUTF();
        amount = in.readInt();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InfractionAmountPair that)) return false;
        return id == that.id && amount == that.amount && Objects.equals(infractionId, that.infractionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(infractionId, id, amount);
    }
}
