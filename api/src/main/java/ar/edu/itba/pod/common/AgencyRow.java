package ar.edu.itba.pod.common;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;

public class AgencyRow implements DataSerializable {

    private String agency;

    public AgencyRow() {
    }

    public AgencyRow(String agency) {
        this.agency = agency;
    }

    public String getAgency() {
        return agency;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(agency);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        agency = in.readUTF();
    }
}
