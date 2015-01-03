package com.github.opentwitchbotteam.otbproject.commands.parser;

import java.util.ArrayList;

public class CommandResponseParser {
    private static final String TERM_START = "\\[\\{";
    private static final String TERM_END = "\\}\\]";


    public static String parse(String userNick, int count, String[] args, String rawResponse) {
        String[] rawResponseArray = rawResponse.split(" ");
        ArrayList<String> formattedResponseArray = new ArrayList<String>();

        for (String word : rawResponseArray) {
            formattedResponseArray.add(parseWord(userNick, count, args, word));
        }
        return String.join(" ", formattedResponseArray);
    }

    private static String parseWord(String userNick, int count, String[] args, String word) {
        int innerStartIndex;
        int innerEndIndex;
        String temp;

        while (true) {
            // If no more terms, return
            innerStartIndex = word.lastIndexOf(TERM_START);
            if (innerStartIndex == -1) {
                return word;
            }
            // If unbalanced term delimiters, return
            innerEndIndex = word.indexOf(TERM_END, innerStartIndex);
            if (innerEndIndex == -1) {
                return word;
            }

            // Check if valid term, and replace if so
            try {
                temp = parseTerm(userNick, count, args, word.substring(innerStartIndex + 2, innerEndIndex));
            }
            catch (InvalidTermException e) {
                return word;
            }
            word = word.substring(0, innerStartIndex) + temp + word.substring(innerEndIndex + 2);
        }
    }

    private static String parseTerm(String userNick, int count, String[] args, String word) throws InvalidTermException {
        // [{user.modifier}]
        if (word.matches("^user(\\.\\p{Alpha}*)?$")) {
            return doModifier(userNick, word);
        }
        // [{count}] - ignores modifier (because no effect)
        else if (word.matches("^count(\\.\\p{Alpha}*)?$")) {
            return Integer.toString(count);
        }
        // [{quote.modifier}] - can have a modifier, but it's unclear why you want one
        else if (word.matches("^quote(\\.\\p{Alpha}*)?$")) {
            return "[Quotes are not yet implemented]"; // TODO fix when quotes implemented
        }
        // [{game.modifier}]
        else if (word.matches("^game(\\.\\p{Alpha}*)?$")) {
            return "a game"; // TODO fix when able to get game name from twitch
        }
        // [{args.modifier<<default>>}]
        else if (word.matches("^args(\\.\\p{Alpha}*)?(<<.*>>)?$")) {
            // If no args, parse default
            if (args.length == 0) {
                return getDefault(word);
            }
            return doModifier(String.join(" ", args), word);
        }
        // [{argN.modifier<<default>>}] - N is a natural number
        else if (word.matches("^arg\\p{Digit}+(\\.\\p{Alpha}*)?(<<.*>>)?$")) {
            // Gets arg number
            String argNumStr = word.replaceFirst("arg", "").split("<<", 2)[0].split("\\.", 2)[0];
            int argNum = Integer.parseInt(argNumStr);

            // If argNum is 0, invalid term
            if (argNum == 0) {
                throw new InvalidTermException();
            }
            // If insufficient args, parse default
            if (args.length < argNum) {
                return getDefault(word);
            }
            return doModifier(args[argNum], word);
        }

        else {
            throw new InvalidTermException();
        }
    }

    private static String doModifier(String toModify, String rawResponseWord) {
        String modifier = getModifier(rawResponseWord);

        if (modifier.equals(ModifierTypes.LOWER)) {
            return toModify.toLowerCase();
        }
        if (modifier.equals(ModifierTypes.UPPER)) {
            return toModify.toUpperCase();
        }
        if (modifier.equals(ModifierTypes.FIRST_CAP)) {
            return ResponseParserUtil.firstCap(toModify, true);
        }
        if (modifier.equals(ModifierTypes.WORD_CAP)) {
            return ResponseParserUtil.wordCap(toModify, true);
        }
        if (modifier.equals(ModifierTypes.FIRST_CAP_SOFT)) {
            return ResponseParserUtil.firstCap(toModify, false);
        }
        if (modifier.equals(ModifierTypes.WORD_CAP_SOFT)) {
            return ResponseParserUtil.wordCap(toModify, false);
        }

        // Else return unmodified word
        return toModify;
    }

    private static String getModifier(String word) {
        // Split away default arg, if exists
        String[] temp = word.split("<<");

        // Check if modifier exists; return empty string if not
        temp = temp[0].split("\\.");
        if (temp.length == 1) {
            return "";
        }
        return temp[1];
    }

    private static String getDefault(String word) {
        // Check if default exists; return empty string if not
        String[] temp = word.split("<<", 2);
        if (temp.length == 1) {
            return "";
        }

        return temp[1].substring(0, (temp[1].length() - 2));
    }
}
