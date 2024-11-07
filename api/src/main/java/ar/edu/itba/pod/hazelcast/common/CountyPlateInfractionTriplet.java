package ar.edu.itba.pod.hazelcast.common;

import com.hazelcast.nio.serialization.DataSerializable;

import java.util.Objects;

public class CountyPlateInfractionTriplet implements DataSerializable {

    private int id;
    private String county, plateId, infractionId;

    public CountyPlateInfractionTriplet() {
    }

    public CountyPlateInfractionTriplet(int id, String county, String plateId, String infractionId) {
        this.id = id;
        this.county = county;
        this.plateId = plateId;
        this.infractionId = infractionId;
    }

    public int getId() {
        return id;
    }

    public String getCounty() {
        return county;
    }

    public String getPlateId() {
        return plateId;
    }

    public String getInfractionId() {
        return infractionId;
    }

    @Override
    public void writeData(com.hazelcast.nio.ObjectDataOutput out) throws java.io.IOException {
        out.writeInt(id);
        out.writeUTF(county);
        out.writeUTF(plateId);
        out.writeUTF(infractionId);
    }

    @Override
    public void readData(com.hazelcast.nio.ObjectDataInput in) throws java.io.IOException {
        id = in.readInt();
        county = in.readUTF();
        plateId = in.readUTF();
        infractionId = in.readUTF();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CountyPlateInfractionTriplet that)) return false;
        return id == that.id && Objects.equals(county, that.county) && Objects.equals(plateId, that.plateId) && Objects.equals(infractionId, that.infractionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, county, plateId, infractionId);
    }
}
