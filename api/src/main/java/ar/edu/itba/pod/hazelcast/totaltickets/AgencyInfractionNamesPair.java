package ar.edu.itba.pod.hazelcast.totaltickets;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;
import java.util.Objects;

public class AgencyInfractionNamesPair implements DataSerializable {

    private String agency, infractionId;

    public AgencyInfractionNamesPair() {
    }

    public AgencyInfractionNamesPair(String agency, String infractionId) {
        this.agency = agency;
        this.infractionId = infractionId;
    }

    public String getAgency() {
        return agency;
    }

    public String getInfractionId() {
        return infractionId;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(agency);
        out.writeUTF(infractionId);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        agency = in.readUTF();
        infractionId = in.readUTF();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AgencyInfractionNamesPair agencyInfractionIdsPair && infractionId.equals(agencyInfractionIdsPair.infractionId) && agency.equals(agencyInfractionIdsPair.agency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(agency, infractionId);
    }

}
