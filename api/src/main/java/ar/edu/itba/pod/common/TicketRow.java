package ar.edu.itba.pod.common;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;
import java.time.LocalDate;

public class TicketRow implements DataSerializable {

    private String plateId, infractionId, agency, county;
    private double amount;
    private LocalDate issueDate;

    public TicketRow() {
    }

    public TicketRow(String plateId, String infractionId, String agency, String county, double amount, LocalDate issueDate) {
        this.plateId = plateId;
        this.infractionId = infractionId;
        this.agency = agency;
        this.county = county;
        this.amount = amount;
        this.issueDate = issueDate;
    }

    public String getPlateId() {
        return plateId;
    }

    public String getInfractionId() {
        return infractionId;
    }

    public String getAgency() {
        return agency;
    }

    public String getCounty() {
        return county;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(plateId);
        out.writeUTF(infractionId);
        out.writeUTF(agency);
        out.writeUTF(county);
        out.writeDouble(amount);
        out.writeObject(issueDate);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        plateId = in.readUTF();
        infractionId = in.readUTF();
        agency = in.readUTF();
        county = in.readUTF();
        amount = in.readDouble();
        issueDate = in.readObject();
    }
}
