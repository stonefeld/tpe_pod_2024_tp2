package ar.edu.itba.pod.hazelcast.ytdcollection;

public record YTDCollectionResult(String agency, int year, int month, int amount) {

    @Override
    public String toString() {
        return agency + ";" + year + ";" + month + ";" + amount;
    }

}
