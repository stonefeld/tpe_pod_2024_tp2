package ar.edu.itba.pod.hazelcast.ytdcollection;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.KeyPredicate;

public class YTDCollectionKeyPredicate implements KeyPredicate<String> {

    @Override
    public boolean evaluate(String s) {
        IMap<String, Integer> agenciesMap = Hazelcast.getHazelcastInstanceByName("g2-tpe2").getMap("g2-agencies");
        return agenciesMap.containsKey(s);
    }

}
