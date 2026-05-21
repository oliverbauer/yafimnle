package io.github.yafimnle.exception;

import java.io.IOException;

public class IllegalArgsException extends RuntimeException {
    public IllegalArgsException(String message) {
        super(message);
    }

    public IllegalArgsException(IOException e) {
        super(e);
    }
}
