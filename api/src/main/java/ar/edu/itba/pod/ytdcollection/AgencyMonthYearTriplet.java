package ar.edu.itba.pod.ytdcollection;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.util.Objects;

public class AgencyMonthYearTriplet implements DataSerializable {
    private String agency;
    private int month, year;

    public AgencyMonthYearTriplet() {
    }

    public AgencyMonthYearTriplet(String agency, int month, int year) {
        this.agency = agency;
        this.month = month;
        this.year = year;
    }

    public String getAgency() {
        return agency;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws java.io.IOException {
        out.writeUTF(agency);
        out.writeInt(month);
        out.writeInt(year);
    }

    @Override
    public void readData(ObjectDataInput in) throws java.io.IOException {
        agency = in.readUTF();
        month = in.readInt();
        year = in.readInt();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AgencyMonthYearTriplet agencyMonthYearTriplet && month == agencyMonthYearTriplet.month && year == agencyMonthYearTriplet.year && agency.equals(agencyMonthYearTriplet.agency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(agency, month, year);
    }
}
