package PostProcessData;

import java.util.*;

public class Day {
    // one full night
    // (hour) time Index, data
    HashMap<String, TimeData> daysData;
    Day(){
        daysData = new HashMap<>();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Map<String, TimeData> sortedMap = new TreeMap<>(daysData);
        for (Map.Entry<String, TimeData> entry : sortedMap.entrySet()) {
            String key = entry.getKey();
            TimeData value = entry.getValue();
            sb.append(String.format("%-10s\t%s\n", key, value));
        }
        return sb.toString();
    }

    public List<String> getSeqDateKeys(){
        List<String> seqKeys = new LinkedList<>();
        Map<String, TimeData> sortedMap = new TreeMap<>(daysData);
        for (Map.Entry<String, TimeData> entry : sortedMap.entrySet()) {
            String key = entry.getKey();
            seqKeys.add(key);
        }
        return seqKeys;
    }
}
