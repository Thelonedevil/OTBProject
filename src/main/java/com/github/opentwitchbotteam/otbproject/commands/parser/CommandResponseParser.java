package com.github.opentwitchbotteam.otbproject.commands.parser;

public class CommandResponseParser {
    private static final String TERM_START = "[[";        // Not a regex
    private static final String TERM_END = "]]";          // Not a regex
    private static final String MODIFIER_DELIM = "\\.";   // regex
    private static final String DEFAULT_START = "\\{\\{"; // regex
    private static final String DEFAULT_END = "\\}\\}";   //regex


    public static String parse(String userNick, String channel, int count, String[] args, String rawResponse) {
        int innerStartIndex;
        int innerEndIndex;
        String temp;

        while (true) {
            // If no more terms, return
            innerStartIndex = rawResponse.lastIndexOf(TERM_START);
            if (innerStartIndex == -1) {
                return rawResponse;
            }
            // If unbalanced term delimiters, return
            innerEndIndex = rawResponse.indexOf(TERM_END, innerStartIndex);
            if (innerEndIndex == -1) {
                return rawResponse;
            }

            // Check if valid term, and replace if so
            try {
                temp = parseTerm(userNick, channel, count, args, rawResponse.substring(innerStartIndex + 2, innerEndIndex));
            }
            catch (InvalidTermException e) {
                return rawResponse;
            }
            rawResponse = rawResponse.substring(0, innerStartIndex) + temp + rawResponse.substring(innerEndIndex + 2);
        }
    }

    private static String parseTerm(String userNick, String channel, int count, String[] args, String term) throws InvalidTermException {
        // [[user.modifier]]
        if (term.matches("^user(" + MODIFIER_DELIM + "\\p{Alpha}*)?$")) {
            return doModifier(userNick, term);
        }
        // [[channel.modifier]]
        if (term.matches("^channel(" + MODIFIER_DELIM + "\\p{Alpha}*)?$")) {
            return doModifier(channel, term);
        }
        // [[count]] - ignores modifier (because no effect)
        else if (term.matches("^count(" + MODIFIER_DELIM + "\\p{Alpha}*)?$")) {
            return Integer.toString(count);
        }
        // [[quote.modifier]] - can have a modifier, but it's unclear why you want one
        else if (term.matches("^quote(" + MODIFIER_DELIM + "\\p{Alpha}*)?$")) {
            return "[Quotes are not yet implemented]"; // TODO fix when quotes implemented
        }
        // [[game.modifier]]
        else if (term.matches("^game(" + MODIFIER_DELIM + "\\p{Alpha}*)?$")) {
            return "a game"; // TODO fix when able to get game name from twitch
        }
        // [[args.modifier{{default}}]]
        else if (term.matches("^args(" + MODIFIER_DELIM + "\\p{Alpha}*)?(" + DEFAULT_START + ".*" + DEFAULT_END + ")?$")) {
            // If no args, parse default
            if (args.length == 0) {
                return getDefault(term);
            }
            return doModifier(String.join(" ", args), term);
        }
        // [[ifargs{{string}}]] - ignores modifier
        else if (term.matches("^ifargs(" + MODIFIER_DELIM + "\\p{Alpha}*)?(" + DEFAULT_START + ".*" + DEFAULT_END + ")?$")) {
            // If no args, return empty string
            if (args.length == 0) {
                return "";
            }
            // Gets conditionally printed string
            return getDefault(term);
        }
        // [[argN.modifier{{default}}]] - N is a natural number
        else if (term.matches("^arg\\p{Digit}+(" + MODIFIER_DELIM + "\\p{Alpha}*)?(" + DEFAULT_START + ".*" + DEFAULT_END + ")?$")) {
            // Gets arg number
            String argNumStr = term.replaceFirst("arg", "").split(DEFAULT_START, 2)[0].split(MODIFIER_DELIM, 2)[0];
            int argNum = Integer.parseInt(argNumStr);

            // If argNum is 0 (or max int overflow), invalid term
            if (argNum <= 0) {
                throw new InvalidTermException();
            }
            // If insufficient args, parse default
            if (args.length < argNum) {
                return getDefault(term);
            }
            return doModifier(args[argNum], term);
        }
        // [[ifargN{{string}}]] - N is a natural number; ignores modifier
        else if (term.matches("^ifarg\\p{Digit}+(" + MODIFIER_DELIM + "\\p{Alpha}*)?(" + DEFAULT_START + ".*" + DEFAULT_END + ")?$")) {
            // Gets arg number
            String argNumStr = term.replaceFirst("ifarg", "").split(DEFAULT_START, 2)[0].split(MODIFIER_DELIM, 2)[0];
            int argNum = Integer.parseInt(argNumStr);

            // If argNum is 0 (or max int overflow)-, invalid term
            if (argNum <= 0) {
                throw new InvalidTermException();
            }
            // If insufficient args, return empty string
            if (args.length < argNum) {
                return "";
            }
            // Gets conditionally printed string
            return getDefault(term);
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
        String[] temp = word.split(DEFAULT_START);

        // Check if modifier exists; return empty string if not
        temp = temp[0].split(MODIFIER_DELIM);
        if (temp.length == 1) {
            return "";
        }
        return temp[1];
    }

    private static String getDefault(String word) {
        // Check if default exists; return empty string if not
        String[] temp = word.split(DEFAULT_START, 2);
        if (temp.length == 1) {
            return "";
        }

        return temp[1].substring(0, (temp[1].length() - 2));
    }
}
