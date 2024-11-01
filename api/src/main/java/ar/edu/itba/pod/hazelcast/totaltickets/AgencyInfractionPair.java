package ar.edu.itba.pod.hazelcast.totaltickets;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;
import java.util.Objects;

public class AgencyInfractionPair implements DataSerializable {

    private Integer agencyId;
    private String infractionId;

    public AgencyInfractionPair() {
    }

    public AgencyInfractionPair(Integer agencyId, String infraction) {
        this.agencyId = agencyId;
        this.infractionId = infraction;
    }

    public Integer getAgencyId() {
        return agencyId;
    }

    public String getInfractionId() {
        return infractionId;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(agencyId);
        out.writeUTF(infractionId);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        agencyId = in.readInt();
        infractionId = in.readUTF();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AgencyInfractionPair agencyInfractionPair && infractionId.equals(agencyInfractionPair.infractionId) && agencyId.equals(agencyInfractionPair.agencyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(agencyId, infractionId);
    }

}
