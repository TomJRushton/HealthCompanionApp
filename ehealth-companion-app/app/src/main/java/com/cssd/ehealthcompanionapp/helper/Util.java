package com.cssd.ehealthcompanionapp.helper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.TimeZone;

public class Util {
    private Util(){}

    public static String TAG(Object o) {
        return o.getClass().getSimpleName();
    }

    public static long getStartOfDay(long timestamp){
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp),
                TimeZone.getDefault().toZoneId()).toLocalDate().atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
    }
}
