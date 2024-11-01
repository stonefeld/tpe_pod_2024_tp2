package ar.edu.itba.pod.hazelcast.maxticketdifference;

public record MaxTicketDifferenceResult(String infraction, int max, int min, int difference) {

    @Override
    public String toString() {
        return infraction + ";" + min + ";" + max + ";" + difference;
    }

}
