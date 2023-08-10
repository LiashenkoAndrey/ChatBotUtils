package org.chatBotUtils.util.exceptions;


public class MethodExecutionException extends UpdateProcessingException {

    public MethodExecutionException(Throwable cause) {
        super(cause);
    }

    public MethodExecutionException(String message) {
        super(message);
    }
}
