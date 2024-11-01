package ar.edu.itba.pod.hazelcast.totaltickets;

public record TotalTicketsResult(String agency, String infraction, long totalTickets) {

    @Override
    public String toString() {
        return infraction + ";" + agency + ";" + totalTickets;
    }

}
