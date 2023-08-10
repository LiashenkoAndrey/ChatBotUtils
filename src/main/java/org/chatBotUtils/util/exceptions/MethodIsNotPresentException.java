package org.chatBotUtils.util.exceptions;

public class MethodIsNotPresentException extends CommandMethodExecutionException {

    public MethodIsNotPresentException(Throwable cause) {
        super(cause);
    }

    public MethodIsNotPresentException(String message) {
        super(message);
    }
}
