package ar.edu.itba.pod.hazelcast.common;

import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;

public class IssueDateAmountPair implements DataSerializable {

    private int id;
    private LocalDate issueDate;
    private double amount;

    public IssueDateAmountPair() {
    }

    public IssueDateAmountPair(int id, LocalDate issueDate, double amount) {
        this.id = id;
        this.issueDate = issueDate;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public void writeData(com.hazelcast.nio.ObjectDataOutput out) throws IOException {
        out.writeInt(id);
        out.writeLong(issueDate.toEpochDay());
        out.writeDouble(amount);
    }

    @Override
    public void readData(com.hazelcast.nio.ObjectDataInput in) throws IOException {
        id = in.readInt();
        issueDate = LocalDate.ofEpochDay(in.readLong());
        amount = in.readDouble();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IssueDateAmountPair that)) return false;
        return id == that.id && Double.compare(amount, that.amount) == 0 && Objects.equals(issueDate, that.issueDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, issueDate, amount);
    }

}
