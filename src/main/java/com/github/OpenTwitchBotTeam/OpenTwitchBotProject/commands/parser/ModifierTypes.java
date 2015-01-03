package com.github.OpenTwitchBotTeam.OpenTwitchBotProject.commands.parser;

public class ModifierTypes {
    /*
     * NOTE: Be careful modifying these values. In certain instances,
     * they are tested for using hard-coded regexps, which check for alphabetical
     * characters, and will fail if other characters are used.
     */

    public static final String LOWER = "lower";
    public static final String UPPER = "upper";
    public static final String FIRST_CAP = "first_cap";
    public static final String WORD_CAP = "word_cap";
    public static final String FIRST_CAP_SOFT = "first_cap_soft";
    public static final String WORD_CAP_SOFT = "word_cap_soft";
}
