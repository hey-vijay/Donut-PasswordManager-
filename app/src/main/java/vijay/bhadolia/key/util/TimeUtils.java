package vijay.bhadolia.key.util;

import android.util.Log;

import java.nio.file.WatchEvent;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TimeUtils {

    private static final String TAG = "TimeUtils";

    private  long SECOND_MILLIS = 1000;
    private  long MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private  long HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private  long DAY_MILLIS = 24 * HOUR_MILLIS;

    private static List<Long> unitDuration = Arrays.asList(
            TimeUnit.DAYS.toMillis(365),
            TimeUnit.DAYS.toMillis(30),
            TimeUnit.DAYS.toMillis(7),
            TimeUnit.DAYS.toMillis(1),
            TimeUnit.HOURS.toMillis(1),
            TimeUnit.MINUTES.toMillis(1),
            TimeUnit.SECONDS.toMillis(1)
    );

    private static List<String> durationName = Arrays.asList(
            "year", "month", "week", "day", "hr", "min", "sec", " ago ", "s", "now"
    );

    public static String getTimeDifference(Long oldTime) {
        Log.d(TAG, "TimeAgo: for $oldTime");
        List<String> timesString = durationName;
        Long time = oldTime;
        if (time < 1000000000000L) {
            time *= 1000;
        }
        Long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return "";
        }
        Long duration = now - time;
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < unitDuration.size(); i++) {
            Long current = unitDuration.get(i);
            Long temp = duration/current;
            String moreThanOne = "";
            if(temp > 1) {
                moreThanOne = timesString.get(8);       // "s"
            }
            if(temp > 0) {
                sb.append(temp)
                        .append(" ")
                        .append(timesString.get(i))
                        .append(moreThanOne)
                        .append(timesString.get(7));
                break;
            }
        }

        if(sb.toString().isEmpty()) {
            return timesString.get(9);  // "now"
        }

        return sb.toString();
    }
}
