package ar.edu.itba.pod.hazelcast.ytdcollection;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;
import java.util.Objects;

public class AgencyYearPair implements DataSerializable {

    private int agencyId, year;

    public AgencyYearPair() {
    }

    public AgencyYearPair(int agencyId, int year) {
        this.agencyId = agencyId;
        this.year = year;
    }

    public int getAgencyId() {
        return agencyId;
    }

    public int getYear() {
        return year;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(agencyId);
        out.writeInt(year);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        agencyId = in.readInt();
        year = in.readInt();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AgencyYearPair agencyYearPair && year == agencyYearPair.year && agencyId == agencyYearPair.agencyId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(agencyId, year);
    }

}
