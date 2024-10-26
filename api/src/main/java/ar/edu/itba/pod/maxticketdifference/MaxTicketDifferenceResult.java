package ar.edu.itba.pod.maxticketdifference;

public record MaxTicketDifferenceResult(String infractionId, int max, int min, int difference) {

    @Override
    public String toString() {
        return infractionId + ";" + min + ";" + max + ";" + difference;
    }
}
