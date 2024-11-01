package ar.edu.itba.pod.hazelcast.utils;

import com.hazelcast.core.IMap;

import java.util.Map;

public class AgenciesMapUtils {

    public static String getAgencyName(IMap<String, Integer> agenciesMap, Integer agencyId) {
        return agenciesMap.entrySet().stream()
                .filter(entry -> entry.getValue().equals(agencyId))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("Unknown");
    }

}
