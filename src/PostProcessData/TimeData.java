package PostProcessData;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

// 1 hour data
public class TimeData {
    public Double cloud;
    public Double trans;
    public Double see;
    public Double alumMag;
    public Double moonAlum;
    public Double smoke;
    public Double wind;
    public Double humidity;
    public Double temp;

    @Override
    public String toString() {
        return String.format("Cloud:%5f, Trans:%5f, See:%5f, AlumMag:%5f, Moon Alum:%5f, Smoke:%5f, Wind:%5f, Humidity:%5f, Temp:%5f", cloud,trans,see, alumMag, moonAlum, smoke,wind,humidity,temp);
    }

    // Define weights for each factor
    private static final Map<String, Integer> weights = new HashMap<String, Integer>() {{
        put("cloud", 10);
        put("trans", 10);
        put("see", 10);
        put("alumMag", 2);
        put("moonAlum", 2);
        put("smoke", 10);
        put("wind", 2);
        put("humidity", 10);
        put("temp", 1);
    }};

    public double calculateHourData() {
        ObjectMapper mapObject = new ObjectMapper();
        Map<String, Double> conditions = mapObject.convertValue(this, Map.class);

    // Calculate the weighted scores for each factor
        double totalScore = 0;
        for (String factor : conditions.keySet()) {
            Double score = (Double) conditions.get(factor);
            int weight = weights.get(factor);
            totalScore += score * weight;
        }
        return totalScore;
    }
}
