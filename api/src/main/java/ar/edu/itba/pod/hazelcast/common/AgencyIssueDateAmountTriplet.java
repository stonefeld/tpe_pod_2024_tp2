package ar.edu.itba.pod.hazelcast.common;

import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;

public class AgencyIssueDateAmountTriplet implements DataSerializable {

    private int id;
    private String agency;
    private LocalDate issueDate;
    private double amount;

    public AgencyIssueDateAmountTriplet() {
    }

    public AgencyIssueDateAmountTriplet(int id, String agency, LocalDate issueDate, double amount) {
        this.id = id;
        this.agency = agency;
        this.issueDate = issueDate;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public String getAgency() {
        return agency;
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
        out.writeUTF(agency);
        out.writeLong(issueDate.toEpochDay());
        out.writeDouble(amount);
    }

    @Override
    public void readData(com.hazelcast.nio.ObjectDataInput in) throws IOException {
        id = in.readInt();
        agency = in.readUTF();
        issueDate = LocalDate.ofEpochDay(in.readLong());
        amount = in.readDouble();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AgencyIssueDateAmountTriplet that)) return false;
        return id == that.id && Double.compare(amount, that.amount) == 0 && Objects.equals(agency, that.agency) && Objects.equals(issueDate, that.issueDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, agency, issueDate, amount);
    }

}
