package com.github.opentwitchbotteam.otbproject.commands.parser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ParserTest {
    private static final String TS = "[[";  // Term Start
    private static final String TE = "]]";  // Term End
    private static final String MD = ".";   // Modifier Delimiter
    private static final String DS = "{{";  // Default Start
    private static final String DE = "}}";  // Default End

    private static final String USER = "NthPortal";
    private static final int COUNT = 1;

    @Test
    // Tests [[user]] term, as well as general term parsing
    public void userTest() {
        String user = "nthPortal";
        String[] args = new String[2];
        args[0] = "foo";
        args[1] = "bar";

        // Basic Test
        String parsed = CommandResponseParser.parse(user, COUNT, args, TS+"user"+TE+" says hi.");
        assertEquals("nthPortal says hi.", parsed);

        // Term at end of string
        parsed = CommandResponseParser.parse(user, COUNT, args, "Go to twitch.tv/"+TS+"user"+TE);
        assertEquals("Go to twitch.tv/nthPortal", parsed);

        // Term at beginning of string
        parsed = CommandResponseParser.parse(user, COUNT, args, "Hi "+TS +"user"+TE+"!");
        assertEquals("Hi nthPortal!", parsed);

        // Term in middle of string
        parsed = CommandResponseParser.parse(user, COUNT, args, "start-"+TS +"user"+TE +"-end");
        assertEquals("start-nthPortal-end", parsed);

        // Multiple times in one word
        parsed = CommandResponseParser.parse(user, COUNT, args, TS+"user"+TE+TS +"user"+TE);
        assertEquals("nthPortalnthPortal", parsed);
    }

    @Test
    public void argsTest() {
        // Empty args
        String[] args = new String[0];
        String parsed = CommandResponseParser.parse(USER, COUNT, args, "Hi "+TS+"args"+TE+".");
        assertEquals("Hi .", parsed);

        parsed = CommandResponseParser.parse(USER, COUNT, args, "Hi "+TS+"args"+DS+"person"+DE+TE+".");
        assertEquals("Hi person.", parsed);

        parsed = CommandResponseParser.parse(USER, COUNT, args, "Hi "+TS+"args"+DS+TS+"user"+TE+DE+TE+".");
        assertEquals("Hi NthPortal.", parsed);
    }
}
