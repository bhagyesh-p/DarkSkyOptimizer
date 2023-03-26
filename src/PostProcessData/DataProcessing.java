package PostProcessData;

import GetData.PreProcessDay;
import GetData.PreProcessTimeData;

import java.util.*;

public class DataProcessing {
    public HashMap<String, HashMap<String, List<Pair<String, String>>>> CountryAndStateData = new HashMap<>();
    public HashMap<String, HashMap<String, Day>> LocAndConData = new HashMap<>();


    public void convert(HashMap<String, HashMap<String, PreProcessDay>> oldData){
        for (Map.Entry<String, HashMap<String, PreProcessDay>> entry : oldData.entrySet()) {
            String oldLocationKey = entry.getKey();
            HashMap<String, PreProcessDay> oldD2DData = entry.getValue();

            HashMap<String, Day> newD2DData = convertD2D(oldD2DData);

            LocAndConData.put(oldLocationKey, newD2DData);
        }
    }

    private HashMap<String, Day> convertD2D(HashMap<String, PreProcessDay> oldD2DData) {
        HashMap<String, Day> newD2DData = new HashMap<>();
        for (Map.Entry<String, PreProcessDay> entry : oldD2DData.entrySet()) {
            String day = entry.getKey();
            PreProcessDay preProcessDay = entry.getValue();
            Day newDay = convertDay(preProcessDay);
            newD2DData.put(day, newDay);
        }

        return newD2DData;
    }

    private Day convertDay(PreProcessDay preProcessDay) {
        Day newDay = new Day();
        for (Map.Entry<String, PreProcessTimeData> entry : preProcessDay.getDaysData().entrySet()) {
            String timeKey = entry.getKey();
            PreProcessTimeData preProcessTimeData = entry.getValue();

            TimeData newTimeData = new TimeData();
            newTimeData.cloud = getCoveragePercentage(preProcessTimeData.getCloud());
            newTimeData.trans = getTransRank(preProcessTimeData.getTrans());
            newTimeData.see = getSeeRank(preProcessTimeData.getSee());
            newTimeData.alumMag = preProcessTimeData.getAlumMag();
            newTimeData.moonAlum = preProcessTimeData.getMoonAlum();
            newTimeData.smoke = getSmokeRank(preProcessTimeData.getSmoke());
            newTimeData.wind = getWindRank(preProcessTimeData.getWind());
            newTimeData.humidity = getHumRank(preProcessTimeData.getHumidity());
            newTimeData.temp = getTempRank(preProcessTimeData.getTemp());
            newDay.daysData.put(timeKey, newTimeData);
        }
        return newDay;
    }

    public static double getCoveragePercentage(String input) {
        if(input == null){
            return -1d;
        }
        if(input.contains("Clear")){
            return 0f;
        }
        if(input.contains("Overcast")){
            return 1f;
        }
        String[] parts = input.split(":");
        String coverageString = parts[2].split("%")[0].trim();
        return Double.parseDouble(coverageString) / 100.0;
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static Double getTransRank(String input) {
        if(input == null){
            return -1d;
        }
        String[] parts = input.split(":");
        String description = parts[0].trim();
        int i  = 0;
        while (isNumeric(description)){
            if(i>parts.length){
                return 0d;
            }
            description = parts[i].trim();
            i++;
        }

        if (description.contains("Too cloudy to forecast")) {
            return 0d;
        } else if (description.contains("Poor")) {
            return 1d;
        } else if (description.contains("Below Average")) {
            return 2d;
        } else if (description.contains("Average")) {
            return 3d;
        } else if (description.contains("Above average")) {
            return 4d;
        } else if (description.contains("Transparent")) {
            return 5d;
        }
        throw new IllegalArgumentException("Invalid description: " + description);
    }

    public static Double getSeeRank(String input) {
        String[] parts = input.split(":");
        String description = parts[0].trim();
        int i  = 0;
        while (isNumeric(description)){
            if(i>parts.length){
                return 0d;
            }
            description = parts[i].trim();
            i++;
        }

        if (description.contains("Too cloudy to forecast")) {
            return 0d;
        } else if (description.contains("Bad")) {
            return 1d;
        } else if (description.contains("Poor")) {
            return 2d;
        } else if (description.contains("Average")) {
            return 3d;
        } else if (description.contains("Good")) {
            return 4d;
        } else if (description.contains("Excellent")) {
            return 5d;
        }
        throw new IllegalArgumentException("Invalid description: " + description);
    }


    public static Double getSmokeRank(String input) {
        if(input == null){
            return -1d;
        }
        String[] parts = input.split(":");
        String description = parts[0].trim();
        int i  = 0;
        while (isNumeric(description)){
            if(i>parts.length){
                return 0d;
            }
            description = parts[i].trim();
            i++;
        }

        if (description.contains("No Smoke")) {
            return 11d;
        } else if (description.contains("2ug/m^3")) {
            return 10d;
        } else if (description.contains("5ug/m^3")) {
            return 9d;
        } else if (description.contains("10ug/m^3")) {
            return 8d;
        } else if (description.contains("20ug/m^3")) {
            return 7d;
        } else if (description.contains("40ug/m^3")) {
            return 6d;
        } else if (description.contains("60ug/m^3")) {
            return 5d;
        } else if (description.contains("80ug/m^3")) {
            return 4d;
        } else if (description.contains("100ug/m^3")) {
            return 3d;
        } else if (description.contains("200ug/m^3")) {
            return 2d;
        } else if (description.contains("500ug/m^3")) {
            return 1d;
        }
        throw new IllegalArgumentException("Invalid description: " + description);
    }

    public static double getWindRank(String input) {
        if(input == null){
            return -1d;
        }
        String[] parts = input.split(":");
        String description = parts[0].trim();
        int i  = 0;
        while (isNumeric(description)){
            if(i>parts.length){
                return 0d;
            }
            description = parts[i].trim();
            i++;
        }
        if (description.contains(">45 mph")) {
            return 0.0;
        } else if (description.contains("29 to 45 mph")) {
            return 1.0;
        } else if (description.contains("17 to 28 mph")) {
            return 2.0;
        } else if (description.contains("12 to 16 mph")) {
            return 3.0;
        } else if (description.contains("6 to 11 mph")) {
            return 4.0;
        } else if (description.contains("0 to 5 mph")) {
            return 5.0;
        }
        throw new IllegalArgumentException("Invalid description: " + description);
    }

    public static double getHumRank(String input) {
        if(input == null){
            return -1d;
        }
        Map<String, Integer> rankMap = new HashMap<>(){{
            put("<25%", 16);
            put("25% to 30%", 15);
            put("30% to 35%", 14);
            put("35% to 40%", 13);
            put("40% to 45%", 12);
            put("45% to 50%", 11);
            put("50% to 55%", 10);
            put("55% to 60%", 9);
            put("60% to 65%", 8);
            put("65% to 70%", 7);
            put("70% to 75%", 6);
            put("75% to 80%", 5);
            put("80% to 85%", 4);
            put("85% to 90%", 3);
            put("90% to 95%", 2);
            put("95% to 100%", 1);
        }};

        // extract the percentage value from the input string
        int startIndex = input.indexOf(":") + 2; // add 2 to exclude the space
        int endIndex = input.indexOf("%");
        int percentage = 0;
        try{
            percentage = Integer.parseInt(input.substring(startIndex, endIndex));
        }catch (Exception e){
            for (Map.Entry<String, Integer> entry : rankMap.entrySet()) {
                String key = entry.getKey();
                Integer value = entry.getValue();
                if(input.contains(key)) {
                    return value;
                }
            }
            // no match found
            return 0;
        }

        // loop through the rankMap and find the matching range for the given percentage value
        for (String range : rankMap.keySet()) {
            String[] boundaries = range.split(" to ");
            int lowerBound = Integer.parseInt(boundaries[0].replaceAll("[^\\d.]", ""));
            int upperBound = Integer.parseInt(boundaries[1].replaceAll("[^\\d.]", ""));
            if (percentage >= lowerBound && percentage <= upperBound) {
                return rankMap.get(range);
            }
        }
        return 0.0; // return 0.0 if no matching range is found
    }

    public static double getTempRank(String input) {
        if(input == null){
            return -1d;
        }
        Map<String, Integer> rankMap = new LinkedHashMap<String, Integer>() {{
            put(">113F", 0);
            put("104F to 113F", 1);
            put("95F to 104F", 2);
            put("86F to 95F", 3);
            put("77F to 86F", 4);
            put("68F to 77F", 5);
            put("59F to 68F", 6);
            put("50F to 59F", 7);
            put("41F to 50F", 8);
            put("32F to 41F", 9);
            put("23F to 32F", 10);
            put("14F to 23F", 11);
            put("5F to 14F", 12);
            put("-3F to 5F", 13);
            put("-12F to -3F", 14);
            put("-21F to -12F", 15);
            put("-30F to -21F", 16);
            put("-40F to -31F", 17);
            put("< -40F", 18);
        }};

        String[] parts = input.split(": ");
        String tempRange = parts[1].split(" \\(")[0];

        for (Map.Entry<String, Integer> entry : rankMap.entrySet()) {
            String key = entry.getKey();
            String[] rangeParts = key.split(" to ");
            int lowerBound = 0;
            int upperBound = 0;
            try{
                upperBound = rangeParts[1].equals(">113F") ? 999 : Integer.parseInt(rangeParts[1].replace("F", ""));
                lowerBound = Integer.parseInt(rangeParts[0].replace("F", ""));
            }catch (Exception e){
                if(input.contains(key)) {
                    return entry.getValue();
                }
                // no match found
                return 0;
            }
            if (tempRange.contains("F") && tempRange.contains("to")) {
                int temp = Integer.parseInt(tempRange.split(" to ")[0].replace("F", ""));
                if (temp >= lowerBound && temp <= upperBound) {
                    return entry.getValue();
                }
            }
        }

        return -1;
    }


}
