package ar.edu.itba.pod;

import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

import java.util.StringTokenizer;

public class TokenizerMapper implements Mapper<String, String, String, Long> {
    private static final long ONE = 1L;

    @Override
    public void map(String key, String value, Context<String, Long> context) {
        StringTokenizer tokenizer = new StringTokenizer(value.toLowerCase());
        while (tokenizer.hasMoreTokens()) {
            context.emit(tokenizer.nextToken(), ONE);
        }
    }

}
