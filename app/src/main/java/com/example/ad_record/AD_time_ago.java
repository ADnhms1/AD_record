package com.example.ad_record;

import java.sql.Time;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class AD_time_ago {
    public String getTime(long duration) {
        Date now = new Date();

        // it subtracts the last modified time from the current time

        long seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - duration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - duration);
        long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - duration);
        long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - duration);

        if(seconds < 60)
        {
            return "just now";
        }
        else if(minutes == 1)
        {
            return "a minute ago";
        }
        else if(minutes > 1 && minutes < 60)
        {
            return minutes + "minute ago";
        }
        else if(hours == 1)
        {
            return "an hours ago";
        }
        else if(hours > 1 && hours < 24)
        {
            return hours + " hours ago";
        }
        else if(days == 1)
        {
            return "a day ago";
        }
        else
        {
            return days + " days ago";
        }

    }
}
