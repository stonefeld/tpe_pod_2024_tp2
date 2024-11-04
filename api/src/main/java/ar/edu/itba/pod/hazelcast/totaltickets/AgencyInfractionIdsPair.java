package ar.edu.itba.pod.hazelcast.totaltickets;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;
import java.util.Objects;

public class AgencyInfractionIdsPair implements DataSerializable {

    private Integer agencyId;
    private String infractionId;

    public AgencyInfractionIdsPair() {
    }

    public AgencyInfractionIdsPair(Integer agencyId, String infractionId) {
        this.agencyId = agencyId;
        this.infractionId = infractionId;
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
        return obj instanceof AgencyInfractionIdsPair agencyInfractionIdsPair && infractionId.equals(agencyInfractionIdsPair.infractionId) && agencyId.equals(agencyInfractionIdsPair.agencyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(agencyId, infractionId);
    }

}
