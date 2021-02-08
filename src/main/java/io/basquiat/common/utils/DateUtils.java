package io.basquiat.common.utils;

import io.basquiat.common.model.DateFormatType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * LocalDateTime Utils
 * created by basquiat
 */
public class DateUtils {

    /**
     * LocalDateTime To String
     * @param localDateTime
     * @return String
     */
    public static String localDateTimeToDateString(LocalDateTime localDateTime, DateFormatType dateFormatType) {
        try {
            return localDateTime.format(DateTimeFormatter.ofPattern(dateFormatType.pattern));
        } catch (Exception e) {
            return "null";
        }
    }

}