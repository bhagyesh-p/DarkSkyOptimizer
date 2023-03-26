package GetData;

import java.util.*;

public class PreProcessDay {
    // (hour) time Index, data
    HashMap<String, PreProcessTimeData> daysData;
    PreProcessDay(){
        daysData = new HashMap<>();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Map<String, PreProcessTimeData> sortedMap = new TreeMap<>(daysData);
        for (Map.Entry<String, PreProcessTimeData> entry : sortedMap.entrySet()) {
            String key = entry.getKey();
            PreProcessTimeData value = entry.getValue();
            sb.append(String.format("%-10s\t%s\n", key, value));
        }
        return sb.toString();
    }

    public List<String> getSeqDateKeys(){
        List<String> seqKeys = new LinkedList<>();
        Map<String, PreProcessTimeData> sortedMap = new TreeMap<>(daysData);
        for (Map.Entry<String, PreProcessTimeData> entry : sortedMap.entrySet()) {
            String key = entry.getKey();
            seqKeys.add(key);
        }
        return seqKeys;
    }

    public HashMap<String, PreProcessTimeData> getDaysData() {
        return daysData;
    }
}
