package ar.edu.itba.pod.hazelcast.common;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class TicketRow implements DataSerializable {

    private String plateId, infractionId, agency, county;
    private int amount, id;
    private LocalDate issueDate;

    public TicketRow() {
    }

    public TicketRow(String plateId, String infractionId, String agency, String county, int amount, int id, String issueDate) {
        this.plateId = plateId;
        this.infractionId = infractionId;
        this.agency = agency;
        this.county = county;
        this.amount = amount;
        this.id = id;

        try {
            LocalDateTime dateTime = LocalDateTime.parse(issueDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            this.issueDate = dateTime.toLocalDate();
        } catch (DateTimeException e) {
            this.issueDate = LocalDate.parse(issueDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
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

    public int getAmount() {
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
        out.writeInt(amount);
        out.writeInt(id);
        out.writeObject(issueDate);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        plateId = in.readUTF();
        infractionId = in.readUTF();
        agency = in.readUTF();
        county = in.readUTF();
        amount = in.readInt();
        id = in.readInt();
        issueDate = in.readObject();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof TicketRow t
                && plateId.equals(t.plateId) && infractionId.equals(t.infractionId)
                && agency.equals(t.agency) && county.equals(t.county)
                && amount == t.amount && id == t.id
                && issueDate.equals(t.issueDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(plateId, infractionId, agency, county, amount, id, issueDate);
    }

    @Override
    public String toString() {
        return "TicketRow{" +
                "plateId='" + plateId + '\'' +
                ", infractionId='" + infractionId + '\'' +
                ", agency='" + agency + '\'' +
                ", county='" + county + '\'' +
                ", amount=" + amount +
                ", id=" + id +
                ", issueDate=" + issueDate +
                '}';
    }
}
