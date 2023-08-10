package org.chatBotUtils.util;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface MethodExecutor {

    void invokeCommand(Update update);

    void invokeCallBack(Update update) ;

    void invokeCallBack(String name) ;

    void invokeCommand(String name);


    void invokeUtilMethod(String methodName);
}
