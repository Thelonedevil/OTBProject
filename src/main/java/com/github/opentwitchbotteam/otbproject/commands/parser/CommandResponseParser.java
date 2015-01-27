package com.github.opentwitchbotteam.otbproject.commands.parser;

public class CommandResponseParser {
    private static final String TERM_START = "[[";        // Not a regex
    private static final String TERM_END = "]]";          // Not a regex
    private static final String MODIFIER_DELIM = "\\.";   // regex
    private static final String EMBED_START = "\\{\\{";   // regex
    private static final String EMBED_END = "\\}\\}";     //regex


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
                temp = postProcessor(parseTerm(userNick, channel, count, args, rawResponse.substring(innerStartIndex + 2, innerEndIndex)));
            }
            catch (InvalidTermException e) {
                return rawResponse;
            }
            rawResponse = rawResponse.substring(0, innerStartIndex) + temp + rawResponse.substring(innerEndIndex + 2);
        }
    }

    private static String parseTerm(String userNick, String channel, int count, String[] args, String term) throws InvalidTermException {
        // [[user.modifier]]
        if (term.matches("^user(" + MODIFIER_DELIM + "\\w*)?$")) {
            return doModifier(userNick, term);
        }
        // [[channel.modifier]]
        else if (term.matches("^channel(" + MODIFIER_DELIM + "\\w*)?$")) {
            return doModifier(channel, term);
        }
        // [[count]] - ignores modifier (because no effect)
        else if (term.matches("^count(" + MODIFIER_DELIM + "\\w*)?$")) {
            return Integer.toString(count);
        }
        // [[quote.modifier]] - can have a modifier, but it's unclear why you want one
        else if (term.matches("^quote(" + MODIFIER_DELIM + "\\w*)?$")) {
            return "[Quotes are not yet implemented]"; // TODO fix when quotes implemented
        }
        // [[game.modifier]]
        else if (term.matches("^game(" + MODIFIER_DELIM + "\\w*)?$")) {
            return "a game"; // TODO fix when able to get game name from twitch
        }
        // [[numargs]] - ignores modifier (because no effect)
        else if (term.matches("^numargs(" + MODIFIER_DELIM + "\\w*)?$")) {
            return Integer.toString(args.length);
        }
        // [[args.modifier{{default}}]]
        else if (term.matches("^args(" + MODIFIER_DELIM + "\\w*)?(" + EMBED_START + ".*" + EMBED_END + ")?$")) {
            // If no args, parse default
            if (args.length == 0) {
                return getEmbeddedString(term);
            }
            return doModifier(String.join(" ", args), term);
        }
        // [[ifargs{{string}}]] - ignores modifier
        else if (term.matches("^ifargs(" + MODIFIER_DELIM + "\\w*)?(" + EMBED_START + ".*" + EMBED_END + ")?$")) {
            // If no args, return empty string
            if (args.length == 0) {
                return "";
            }
            // Gets conditionally printed string
            return getEmbeddedString(term);
        }
        // [[argN.modifier{{default}}]] - N is a natural number
        else if (term.matches("^arg\\p{Digit}+(" + MODIFIER_DELIM + "\\w*)?(" + EMBED_START + ".*" + EMBED_END + ")?$")) {
            // Gets arg number
            String argNumStr = term.replaceFirst("arg", "").split(EMBED_START, 2)[0].split(MODIFIER_DELIM, 2)[0];
            int argNum = Integer.parseInt(argNumStr);

            // If argNum is 0 (or max int overflow), invalid term
            if (argNum <= 0) {
                throw new InvalidTermException();
            }
            // If insufficient args, parse default
            if (args.length < argNum) {
                return getEmbeddedString(term);
            }
            return doModifier(args[argNum - 1], term);
        }
        // [[ifargN{{string}}]] - N is a natural number; ignores modifier
        else if (term.matches("^ifarg\\p{Digit}+(" + MODIFIER_DELIM + "\\w*)?(" + EMBED_START + ".*" + EMBED_END + ")?$")) {
            // Gets arg number
            String argNumStr = term.replaceFirst("ifarg", "").split(EMBED_START, 2)[0].split(MODIFIER_DELIM, 2)[0];
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
            return getEmbeddedString(term);
        }
        // [[foreach.modifier{{prepend}}{{append}}]]
        else if (term.matches("^foreach(" + MODIFIER_DELIM + "\\w*)?(" + EMBED_START + ".*" + EMBED_END + "){0,2}$")) {
            String prepend = getEmbeddedString(term);
            String append = getEmbeddedString(term, 2);
            String result = "";

            for (String arg : args) {
                result = result + prepend + doModifier(arg, term) + append;
            }
            return result;
        }

        else {
            throw new InvalidTermException();
        }
    }

    // Prevents terms from being passed into the parser as arguments to a command
    // (this could lead to infinite looping if, for example, [[args]] is passed in)
    // Regex hard-coded
    private static String postProcessor(String parsedTerm) {
        while (parsedTerm.contains(TERM_START)) {
            parsedTerm = parsedTerm.replaceAll("\\[\\[", "[ [");
        }
        while (parsedTerm.contains(TERM_END)) {
            parsedTerm = parsedTerm.replaceAll("\\]\\]", "] ]");
        }
        return parsedTerm;
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
        String[] temp = word.split(EMBED_START);

        // Check if modifier exists; return empty string if not
        temp = temp[0].split(MODIFIER_DELIM);
        if (temp.length == 1) {
            return "";
        }
        return temp[1];
    }

    // Check if embedded string exists; return empty string if not
    private static String getEmbeddedString(String term) {
        return getEmbeddedString(term, 1);
    }

    // Check if given index embedded string exists; return empty string if not
    private static String getEmbeddedString(String term, int index) {
        String[] temp = term.split(EMBED_START, 2);
        if (temp.length == 1) {
            return "";
        }

        for (int i = 1; i < index; i++) {
            temp = temp[1].split(EMBED_START, 2);
            if (temp.length == 1) {
                return "";
            }
        }

        // Handle empty embedded string
        if (temp[1].matches("^"+EMBED_END)) {
            return "";
        }
        return temp[1].split(EMBED_END)[0];
    }
}
