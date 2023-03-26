package PostProcessData;

import java.util.*;

public class StarGazingRankingAlgorithm {
    private PriorityQueue<ScoreData> maxHeapDay1 = new PriorityQueue<ScoreData>(Comparator.reverseOrder());
    private PriorityQueue<ScoreData> maxHeapDay2 = new PriorityQueue<ScoreData>(Comparator.reverseOrder());
    private PriorityQueue<ScoreData> maxHeapDay3 = new PriorityQueue<ScoreData>(Comparator.reverseOrder());
    private PriorityQueue<ScoreData> maxHeapDayOverFill = new PriorityQueue<ScoreData>(Comparator.reverseOrder());
    private String currentLocation = "";
    HashMap<String, HashMap<String, Day>> locationsAndData;
    int windowSize;

    public StarGazingRankingAlgorithm(HashMap<String, HashMap<String, Day>> locationsAndData, int windowSize){
        this.locationsAndData =  locationsAndData;
        this.windowSize = windowSize;
        digestLocations();
    }
    public void printResults(){
        printHeap(maxHeapDay1, "Day 1");
        printHeap(maxHeapDay2, "Day 2");
        printHeap(maxHeapDay3, "Day 3");
        printHeap(maxHeapDayOverFill, "Day 4");
    }

    private void printHeap(PriorityQueue<ScoreData> heap, String name){
        System.out.println("Top: " + name);
        int i = 0;
        if (maxHeapDay1.isEmpty()){
            System.out.println(name + " Empty");
        }else{
            while (!heap.isEmpty()){
                if(i > 10){
                    return;
                }
                System.out.println(heap.poll());
                i++;
            }
        }
    }


    public void digestLocations() {
        for (Map.Entry<String, HashMap<String, Day>> entry : locationsAndData.entrySet()) {
            String key = entry.getKey();
            currentLocation = key;
            // get the acc score per hour
            HashMap<String, Day> value = entry.getValue();
            digestSingleLocation(value);
        }

    }

    public void digestSingleLocation(HashMap<String, Day> daysAtLocationData){
        Map<String, Day> sortedMap = new TreeMap<>(daysAtLocationData);

        for (Map.Entry<String, Day> entry : sortedMap.entrySet()) {
            // day number
            String key = entry.getKey();
            // that days data
            Day value = entry.getValue();

            ScoreData singleDayDigested = digestSingleDay(value);
            switch (key){
                case "0":
                    maxHeapDay1.add(singleDayDigested);
                    break;
                case "1":
                    maxHeapDay2.add(singleDayDigested);
                    break;
                case "2":
                    maxHeapDay3.add(singleDayDigested);
                    break;
                default:
                    maxHeapDayOverFill.add(singleDayDigested);
                    break;
            }
        }
    }



    public ScoreData digestSingleDay(Day currentDay){
        // change to an array
        ArrayList<Double> hourlyScores = new ArrayList<>();
        ArrayList<String> hours = new ArrayList<>();
        Map<String, TimeData> sortedMap = new TreeMap<>(currentDay.daysData);
        for (Map.Entry<String, TimeData> entry : sortedMap.entrySet()) {
            String key = entry.getKey();

            // get the acc score per hour
            TimeData value = entry.getValue();
            Double overAllScore = value.calculateHourData();
            hourlyScores.add(overAllScore);
            hours.add(key);
        }

        double[] scores = new double[hourlyScores.size()];
        for(int i = 0; i < hourlyScores.size(); i++){
            scores[i] = hourlyScores.get(i);
        }
        if(windowSize > scores.length){
            System.out.println("Window size too big");
            return new ScoreData(null, -1d, "");
        }
        Pair<Integer,Double> idxAndScore = maxSum(scores,windowSize);
        int startIdxForBestStartTime = idxAndScore.getFirst();
        double bestScoreGivenWindow = idxAndScore.getSecond();
        Pair<String,String> hourInterval = new Pair<>(hours.get(startIdxForBestStartTime),hours.get(startIdxForBestStartTime+windowSize-1));
        ScoreData scoreData = new ScoreData(hourInterval, bestScoreGivenWindow, currentLocation);
        return scoreData;
    }



    public Pair<Integer,Double> maxSum(double[] scores, int windowSize) {
        double totalSum = 0;
        double maxSum = scores[0];
        int maxIdx = 0;
        for (int i = 0; i < scores.length; i++) {
            if (i + windowSize - 1 < scores.length) {
                totalSum += scores[i + windowSize - 1];
                if(maxSum < totalSum) {
                     maxIdx = i;
                }
                maxSum = Math.max(maxSum, totalSum);
            }
        }

        return new Pair<>(maxIdx, maxSum);
    }

}
