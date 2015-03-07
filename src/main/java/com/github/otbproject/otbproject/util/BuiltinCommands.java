package com.github.otbproject.otbproject.util;

public class BuiltinCommands {
    public static class General {
        public static final String INVALID_ARG = "~%general:invalid.arg";
        public static final String INVALID_FLAG = "~%general:invalid.flag";
        public static final String INSUFFICIENT_USER_LEVEL = "~%general:insufficient.user.level";
        public static final String INSUFFICIENT_ARGS = "~%general:insufficient.args";
    }
    public static class Command {
        public static class General {
            public static final String DOES_NOT_EXIST = "~%command.general:does.not.exist";
        }
        public static final String ADD_ALREADY_EXISTS = "~%command.add.already.exists";
        public static final String SET_SUCCESS = "~%command.set.success";
        public static final String SET_IS_ALIAS = "~%command.set.is.alias";
        public static final String REMOVE_SUCCESS = "~%command.remove.success";
    }
}
