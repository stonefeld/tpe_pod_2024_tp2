package ar.edu.itba.pod.repeatedplates;

public record RepeatedPlatesResult(String county, double percentage) {

    @Override
    public String toString() {
        return county + ";" + percentage + "%";
    }
}
