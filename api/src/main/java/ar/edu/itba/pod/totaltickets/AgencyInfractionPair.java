package ar.edu.itba.pod.totaltickets;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

public class AgencyInfractionPair implements DataSerializable {
    private String agency;
    private String infractionId;

    public AgencyInfractionPair() {
    }

    public AgencyInfractionPair(String agency, String infraction) {
        this.agency = agency;
        this.infractionId = infraction;
    }

    public String getAgency() {
        return agency;
    }

    public String getInfractionId() {
        return infractionId;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws java.io.IOException {
        out.writeUTF(agency);
        out.writeUTF(infractionId);
    }

    @Override
    public void readData(ObjectDataInput in) throws java.io.IOException {
        agency = in.readUTF();
        infractionId = in.readUTF();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AgencyInfractionPair agencyInfractionPair && infractionId.equals(agencyInfractionPair.infractionId) && agency.equals(agencyInfractionPair.agency);
    }

    @Override
    public int hashCode() {
        return agency.hashCode() + infractionId.hashCode();
    }
}
