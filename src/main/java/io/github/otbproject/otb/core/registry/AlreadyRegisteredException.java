package io.github.otbproject.otb.core.registry;

public class AlreadyRegisteredException extends IllegalStateException {
    AlreadyRegisteredException() {
        super();
    }

    AlreadyRegisteredException(String message) {
        super(message);
    }
}
