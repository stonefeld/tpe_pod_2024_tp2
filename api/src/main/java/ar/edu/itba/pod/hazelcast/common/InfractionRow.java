package ar.edu.itba.pod.hazelcast.common;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;

public class InfractionRow implements DataSerializable {

    private String code, description;

    public InfractionRow() {
    }

    public InfractionRow(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(code);
        out.writeUTF(description);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        code = in.readUTF();
        description = in.readUTF();
    }
}
