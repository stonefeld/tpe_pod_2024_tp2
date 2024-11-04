package ar.edu.itba.pod.hazelcast.totaltickets;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.KeyPredicate;

public class TotalTicketsKeyPredicate implements KeyPredicate<AgencyInfractionNamesPair> {

    @Override
    public boolean evaluate(AgencyInfractionNamesPair s) {
        IMap<String, Integer> agenciesMap = Hazelcast.getHazelcastInstanceByName("g2-tpe2").getMap("g2-agencies");
        IMap<String, Integer> infractionsMap = Hazelcast.getHazelcastInstanceByName("g2-tpe2").getMap("g2-infractions");
        return agenciesMap.containsKey(s.getAgency()) && infractionsMap.containsKey(s.getInfractionId());
    }

}
