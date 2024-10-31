package ar.edu.itba.pod.maxticketdifference;

import com.hazelcast.mapreduce.KeyPredicate;

public class MaxTicketDifferenceKeyPredicate implements KeyPredicate<String> {

    private final String agency;

    public MaxTicketDifferenceKeyPredicate(String agency) {
        this.agency = agency;
    }

    @Override
    public boolean evaluate(String key) {
        return key != null && key.equals(agency);
    }

}
