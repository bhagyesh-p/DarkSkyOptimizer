package PostProcessData;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScoreData implements Comparable<ScoreData> {
    Pair<String,String> hourInterval;
    Double score;
    String location;

    public ScoreData(Pair<String, String> hourInterval, Double score)  {
        this.hourInterval = hourInterval;
        this.score = score;
    }

    public ScoreData(Pair<String, String> hourInterval, Double score, String location)  {
        this.hourInterval = hourInterval;
        this.score = score;
        this.location = location;
    }

    @Override
    public int compareTo(ScoreData o) {
        if(o.score.equals(score)){
            return Integer.valueOf(o.hourInterval.getFirst()).compareTo(Integer.valueOf(hourInterval.getFirst()));
        }
        return Double.compare(o.score,score);
    }

    private ZonedDateTime ZuluToPST(String zuluTimeWithOffset) {
        System.out.println(zuluTimeWithOffset);
        // Extract offset from string using regular expression
        Pattern pattern = Pattern.compile("Z([+-]\\d{2}):?(\\d{2})?");
        Matcher matcher = pattern.matcher(zuluTimeWithOffset);
        int hoursOffset = 0;
        int minutesOffset = 0;
        if (matcher.find()) {
            hoursOffset = Integer.parseInt(matcher.group(1));
            if (matcher.group(2) != null) {
                minutesOffset = Integer.parseInt(matcher.group(2));
            }
        }

        // Parse Zulu time to LocalDateTime object
        LocalDateTime localDateTime = LocalDateTime.parse(zuluTimeWithOffset.substring(0, 19), DateTimeFormatter.ISO_DATE_TIME);

        // Convert to ZonedDateTime in Zulu timezone
        ZonedDateTime zuluDateTime = ZonedDateTime.of(localDateTime, ZoneId.of("Z"));

        // Apply offset to ZonedDateTime
        zuluDateTime = zuluDateTime.plusHours(hoursOffset).plusMinutes(minutesOffset);

        // Convert to Pacific Standard Timezone
        ZonedDateTime pstDateTime = zuluDateTime.withZoneSameInstant(ZoneId.of("America/Los_Angeles"));

        // Format output in desired format

//        System.out.println("PST time: " + pstTime);
        return pstDateTime;
    }

    @Override
    public String toString() {
        return "ScoreData{" +
                "hourInterval=" + hourInterval.getFirst() + " to " + hourInterval.getSecond() +
                ", score=" + score +
                ", location='" + location + '\'' +
                '}';
    }
}
