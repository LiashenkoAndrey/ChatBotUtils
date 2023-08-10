package org.chatBotUtils.util;


import org.chatBotUtils.util.exceptions.UpdateProcessingException;

public interface UpdateService<T> {

     void processUpdate(T update) throws UpdateProcessingException;
}
