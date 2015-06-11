package com.github.otbproject.otbproject.command.parser;

import com.github.otbproject.otbproject.script.ScriptProcessor;

public class TermLoader {
    private static final ScriptProcessor<ParserTerm> PROCESSOR = new ScriptProcessor<>(ParserTerm.class, null);
    private static final String METHOD_NAME = "getTerm";

    public static boolean loadTerm(String scriptName) {
        // TODO create actual path directory
        ParserTerm term = PROCESSOR.process(scriptName, scriptName, METHOD_NAME, null);
        return (term != null) && (term.value() != null) && (term.action() != null) && CommandResponseParser.registerTerm(term);
    }
}
