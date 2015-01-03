package com.github.opentwitchbotteam.otbproject.commands.parser;

import java.util.ArrayList;

public class ResponseParserUtil {
    public static String firstCap(String string, boolean forceLower) {
        if (string.length() == 0) {
            return string;
        }
        if (string.length() == 1) {
            return string.toUpperCase();
        }

        if (forceLower) {
            return (string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase());
        }
        return (string.substring(0,1).toUpperCase() + string.substring(1));
    }

    public static String wordCap(String string, boolean forceLower) {
        String[] strArray = string.split(" ");
        ArrayList<String> newStrArray = new ArrayList<String>();

        for (String word : strArray) {
            newStrArray.add(firstCap(word, forceLower));
        }
        return String.join(" ", newStrArray);
    }
}
