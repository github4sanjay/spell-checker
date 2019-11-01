package com.example.demo.util;

import java.time.*;
import java.util.Date;

public class DateUtil {

    public static Period getPeriod(Date lentDate ){
        Instant instant = Instant.ofEpochMilli(lentDate.getTime());
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return Period.between(localDateTime.toLocalDate(), LocalDate.now());
    }
}
