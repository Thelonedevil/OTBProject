package com.github.opentwitchbotteam.otbproject.commands.parser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ParserTest {
    private static final String TS = "[[";  // Term Start
    private static final String TE = "]]";  // Term End
    private static final String MD = ".";   // Modifier Delimiter
    private static final String DS = "{{";  // Default Start
    private static final String DE = "}}";  // Default End

    private static final String USER = "nthportal";
    private static final int COUNT = 1;
    private static final String CHANNEL = "the_lone_devil";

    @Test
    // Term-independent tests
    public void generalTest() {
        String[] args = new String[2];
        args[0] = "foo";
        args[1] = "bar";

        // InvalidTerm
        String parsed = CommandResponseParser.parse(USER, CHANNEL, COUNT, args, TS+"foo"+TE);
        assertEquals(TS+"foo"+TE, parsed);
    }

    @Test
    // Tests [[user]] term, as well as some general term parsing
    public void userTest() {
        String user = "nthPortal";
        String[] args = new String[2];
        args[0] = "foo";
        args[1] = "bar";

        // Basic Test
        String parsed = CommandResponseParser.parse(user, CHANNEL, COUNT, args, TS+"user"+TE+" says hi.");
        assertEquals("nthPortal says hi.", parsed);

        // Term at end of string
        parsed = CommandResponseParser.parse(user, CHANNEL, COUNT, args, "Go to twitch.tv/"+TS+"user"+TE);
        assertEquals("Go to twitch.tv/nthPortal", parsed);

        // Term at beginning of string
        parsed = CommandResponseParser.parse(user, CHANNEL, COUNT, args, "Hi "+TS +"user"+TE+"!");
        assertEquals("Hi nthPortal!", parsed);

        // Term in middle of string
        parsed = CommandResponseParser.parse(user, CHANNEL, COUNT, args, "start-"+TS +"user"+TE +"-end");
        assertEquals("start-nthPortal-end", parsed);

        // Multiple times in one word
        parsed = CommandResponseParser.parse(user, CHANNEL, COUNT, args, TS+"user"+TE+TS +"user"+TE);
        assertEquals("nthPortalnthPortal", parsed);

        // Space in term
        parsed = CommandResponseParser.parse(user, CHANNEL, COUNT, args, TS+"user "+TE+TS +"user"+TE);
        assertNotEquals("nthPortalnthPortal", parsed);

        parsed = CommandResponseParser.parse(user, CHANNEL, COUNT, args, TS+"user"+TE+TS +"user "+TE);
        assertNotEquals("nthPortalnthPortal", parsed);
    }

    @Test
    // Test [[count]] term
    public void countTest() {
        String[] args = new String[2];
        args[0] = "foo";
        args[1] = "bar";

        // count == 0
        int count = 0;
        String parsed = CommandResponseParser.parse(USER, CHANNEL, count, args, "This command has been run "+TS+"count"+TE+" times.");
        assertEquals("This command has been run 0 times.", parsed);

        // large count
        count = 30000;
        parsed = CommandResponseParser.parse(USER, CHANNEL, count, args, "This command has been run "+TS+"count"+TE+" times.");
        assertEquals("This command has been run 30000 times.", parsed);

        // negative count (shouldn't ever happen, but testing for it anyway)
        count = -30000;
        parsed = CommandResponseParser.parse(USER, CHANNEL, count, args, "This command has been run "+TS+"count"+TE+" times.");
        assertEquals("This command has been run -30000 times.", parsed);
    }

    @Test
    // Test [[args{{default}}]] term, as well as defaults
    public void argsTest() {
        // Empty args
        String[] args = new String[0];
        String parsed = CommandResponseParser.parse(USER, CHANNEL, COUNT, args, "Hi "+TS+"args"+TE+".");
        assertEquals("Hi .", parsed);

        parsed = CommandResponseParser.parse(USER, CHANNEL, COUNT, args, "Hi "+TS+"args"+DS+"person"+DE+TE+".");
        assertEquals("Hi person.", parsed);

        parsed = CommandResponseParser.parse(USER, CHANNEL, COUNT, args, "Hi "+TS+"args"+DS+TS+"user"+TE+DE+TE+".");
        assertEquals("Hi NthPortal.", parsed);

        // Defaults (can't have a space)
        parsed = CommandResponseParser.parse(USER, CHANNEL, COUNT, args, "Hi "+TS+"args"+DS+"lovely person"+DE+TE+".");
        assertNotEquals("Hi lovely person.", parsed);

        parsed = CommandResponseParser.parse(USER, CHANNEL, COUNT, args, "Hi "+TS+"args"+DS+"person "+DE+TE+".");
        assertNotEquals("Hi person .", parsed);

        // 1 arg
        args = new String[1];
        args[0] = "Justin";

        parsed = CommandResponseParser.parse(USER, CHANNEL, COUNT, args, "Hi "+TS+"args"+TE+".");
        assertEquals("Hi Justin.", parsed);

        parsed = CommandResponseParser.parse(USER, CHANNEL, COUNT, args, "Hi "+TS+"args"+DS+"person"+DE+TE+".");
        assertEquals("Hi Justin.", parsed);

        parsed = CommandResponseParser.parse(USER, CHANNEL, COUNT, args, "Hi "+TS+"args"+DS+TS+"user"+TE+DE+TE+".");
        assertEquals("Hi Justin.", parsed);

        // 2 args
        args = new String[2];
        args[0] = "awesome";
        args[1] = "people";

        parsed = CommandResponseParser.parse(USER, CHANNEL, COUNT, args, "Hi "+TS+"args"+TE+".");
        assertEquals("Hi awesome people.", parsed);

        parsed = CommandResponseParser.parse(USER, CHANNEL, COUNT, args, "Hi "+TS+"args"+DS+"person"+DE+TE+".");
        assertEquals("Hi awesome people.", parsed);

        parsed = CommandResponseParser.parse(USER, CHANNEL, COUNT, args, "Hi "+TS+"args"+DS+TS+"user"+TE+DE+TE+".");
        assertEquals("Hi awesome people.", parsed);
    }

    @Test
    // Test [[argN{{default}}]] term (N is a natural number)
    public void argTest() {
        // Empty args
        String[] args = new String[0];

        // TODO 2 digit arg
    }

    @Test
    // Test modifiers ([[args]] used for completeness)
    public void modifierTest() {

    }
}
