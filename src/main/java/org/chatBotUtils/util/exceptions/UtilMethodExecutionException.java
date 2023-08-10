package org.chatBotUtils.util.exceptions;

public class UtilMethodExecutionException extends MethodExecutionException {
    public UtilMethodExecutionException(Throwable cause) {
        super(cause);
    }

    public UtilMethodExecutionException(String message) {
        super(message);
    }
}
