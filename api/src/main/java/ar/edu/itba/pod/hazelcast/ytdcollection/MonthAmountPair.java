package ar.edu.itba.pod.hazelcast.ytdcollection;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;

public class MonthAmountPair implements DataSerializable {

    private int amount, month;

    public MonthAmountPair() {
    }

    public MonthAmountPair(int amount, int month) {
        this.amount = amount;
        this.month = month;
    }

    public int getAmount() {
        return amount;
    }

    public int getMonth() {
        return month;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(amount);
        out.writeInt(month);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        amount = in.readInt();
        month = in.readInt();
    }

}
