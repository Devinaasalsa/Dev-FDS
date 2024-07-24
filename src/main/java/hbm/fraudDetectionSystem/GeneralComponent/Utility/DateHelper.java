package hbm.fraudDetectionSystem.GeneralComponent.Utility;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class DateHelper {
    public static String convertTimestamp(String timestamp) {
        List<DateTimeFormatter> formatters = new ArrayList<>();
        formatters.add(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS"));
        formatters.add(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"));
        formatters.add(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDateTime.parse(timestamp, formatter).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
            } catch (DateTimeParseException e) {
                // Continue trying other formats
            }
        }

        return timestamp;
//        String formattedTimestamp;
//
//        if (timestamp.length() == 22) { // Check if the timestamp has two digits for milliseconds
//            LocalDateTime dateTime = LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS"));
//            formattedTimestamp = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
//        } else if (timestamp.length() == 21) {
//            LocalDateTime dateTime = LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"));
//            formattedTimestamp = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
//        } else {
//            formattedTimestamp = timestamp; // Return the original timestamp as is
//        }
//
//        return formattedTimestamp;
    }

    public static String convertTimestamp(String timestamp, String format) {
        try {
            return LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern(format)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        } catch (DateTimeParseException e) {
            // Continue trying other formats
        }

        return timestamp;
    }

    public static String getDay(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        LocalDate date = LocalDateTime.parse(dateString, formatter).toLocalDate();
        return String.valueOf(date.getDayOfWeek().getValue());
    }

    // Method to get the hour from a time string
    public static String getHour(String timeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        LocalTime time = LocalDateTime.parse(timeString, formatter).toLocalTime();
        return String.valueOf(time.getHour());
    }

    // Method to get seconds from a time string
    public static String getSec(String timeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        LocalTime time = LocalDateTime.parse(timeString, formatter).toLocalTime();
        return String.valueOf(time.getSecond());
    }
}
