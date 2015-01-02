package com.github.OpenTwitchBotTeam.OpenTwitchBotProject.commands.parser;

import java.util.ArrayList;

public class ResponseParserUtil {
    public static String firstCap(String string) {
        if (string.length() == 0) {
            return string;
        }
        if (string.length() == 1) {
            return string.toUpperCase();
        }
        return (string.substring(0,1).toUpperCase() + string.substring(1).toLowerCase());
    }

    public static String wordCap(String string) {
        String[] strArray = string.split(" ");
        ArrayList<String> newStrArray = new ArrayList<String>();

        for (String word : strArray) {
            newStrArray.add(firstCap(word));
            //newString = newString + firstCap(word);
        }
        return String.join(" ", newStrArray);

        //return newString;
    }
}
