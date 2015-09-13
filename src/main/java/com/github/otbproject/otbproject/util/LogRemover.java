package com.github.otbproject.otbproject.util;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.fs.FSUtil;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

public class LogRemover {
    public static void removeOldLogs() {
        final Calendar calendar = Calendar.getInstance();
        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        calendar.setTime(new Date());
        final int month = calendar.get(Calendar.MONTH);
        final int year = calendar.get(Calendar.YEAR);

        final Pattern filePattern = Pattern.compile("(app|web)-\\d{4}-\\d{2}-\\d{2}-\\d+\\.log");
        final Pattern endPattern = Pattern.compile("-\\d+\\.log");

        long count = FSUtil.streamDirectory(new File(FSUtil.logsDir()))
                .filter(file -> filePattern.matcher(file.getName()).matches())
                .filter(file -> {
                    String dateStr = endPattern.matcher(file.getName().substring(4)).replaceFirst("");
                    try {
                        Date logDate = dateFormat.parse(dateStr);
                        calendar.setTime(logDate);
                        int logMonth = calendar.get(Calendar.MONTH);
                        int logYear = calendar.get(Calendar.YEAR);

                        // Remove if month is more than 1 month ago
                        if ((year > logYear)
                                && (12 * (year - logYear) + month - logMonth) > 1) {
                            return true;
                        } else if ((month - logMonth) > 1) {
                            return true;
                        }
                    } catch (ParseException e) { // Really shouldn't happen if it matched the Pattern
                        App.logger.catching(e);
                    }
                    return false;
                })
                .filter(file -> {
                    if (!file.delete()) {
                        App.logger.error("Failed to delete old log file: " + file.getName());
                        return false;
                    }
                    return true;
                })
                .count();

        if (count > 0) {
            App.logger.info("Removed " + count + " old log" + (count == 1 ? "" : "s"));
        }
    }
}
