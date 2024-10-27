package ar.edu.itba.pod.ytdcollection;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;
import java.util.Objects;

public class AgencyMonthYearTriplet implements DataSerializable {

    private int agencyId, month, year;

    public AgencyMonthYearTriplet() {
    }

    public AgencyMonthYearTriplet(int agencyId, int month, int year) {
        this.agencyId = agencyId;
        this.month = month;
        this.year = year;
    }

    public int getAgencyId() {
        return agencyId;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(agencyId);
        out.writeInt(month);
        out.writeInt(year);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        agencyId = in.readInt();
        month = in.readInt();
        year = in.readInt();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AgencyMonthYearTriplet agencyMonthYearTriplet && month == agencyMonthYearTriplet.month && year == agencyMonthYearTriplet.year && agencyId == agencyMonthYearTriplet.agencyId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(agencyId, month, year);
    }

}
