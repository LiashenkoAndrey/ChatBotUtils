package org.chatBotUtils.util.impl;


import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.chatBotUtils.util.BeanService;
import org.chatBotUtils.util.MethodExecutor;
import org.chatBotUtils.util.annotations.CallBack;
import org.chatBotUtils.util.annotations.Command;
import org.chatBotUtils.util.annotations.Controller;
import org.chatBotUtils.util.exceptions.*;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class MethodExecutorImpl implements MethodExecutor {

    private static final Logger logger = LogManager.getLogger(MethodExecutorImpl.class);

    private Map<String, Method> callBacks;
    private Map<String, Method> commands;

    private Map<String, Method> methods;

    private final BeanService beanService;

    @PostConstruct
    private void prepare() {
        List<Method> methods = findAllMethods();
        this.methods = methods.stream()
                .collect(Collectors.toMap(Method::getName, Function.identity()));
        this.callBacks = findCallbacks(methods);
        this.commands = findCommands(methods);

    }

    @Override
    public void invokeCallBack(Update update)  {
        try {
            String data = update.getCallbackQuery().getData();
            if (hasParams(data)) {
                logger.debug("data = " + data);
                String methodName = data.substring(0, data.indexOf('?'));
                logger.debug(methodName);
                Method method = methods.get(methodName);

                invokeMethod(method, getParams(data));
            } else {
                Method method = methods.get(data);
                invokeMethod(method);
            }
        } catch (MethodExecutionException e) {
            logger.error(e.toString());
            throw new CallBackMethodExecutionException(e);
        }
    }

    public String[] getParams(String methodName) {
        logger.debug(methodName);
        String[] splittedData = methodName.split("\\?");
        logger.debug(Arrays.toString(splittedData));
        return splittedData[1].split("_");
    }


    @Override
    public void invokeCommand(Update update) {
        try {
            String commandName = update.getMessage().getText().replace("/", "");

            if (commands.containsKey(commandName)) {
                Method method = commands.get(commandName);

                invokeMethod(method);
            } else {
                throw new MethodIsNotPresentException("Method with name '" + commandName + "' is not present");
            }

        } catch (MethodExecutionException e) {
            logger.error(e.toString());
            throw new CommandMethodExecutionException(e);
        }
    }

    private void invokeMethod(Method method) {
        try {
            if (method == null) throw new IllegalArgumentException("method is null");
            String className = method.getDeclaringClass().getSimpleName();
            Object methodOwner = beanService.getBean(uncapitalize(className));
            method.invoke(methodOwner);
        } catch (InvocationTargetException | IllegalAccessException e) {
            logger.error(e.toString());
            throw new MethodExecutionException(e);
        }
    }


    public void invokeMethod(Method method, String[] params) {
        try {
            logger.debug("invokeMethod " + method.getName()  + "p:  " + params);
            Object methodOwner = getMethodOwner(method);

            method.invoke(methodOwner, params);
        } catch (InvocationTargetException | IllegalAccessException e) {
            logger.error(e.toString());
            throw new MethodExecutionException(e);
        }
    }

    private Object getMethodOwner(Method method) {
        if (method == null) throw new IllegalArgumentException("method is null");
        String className = method.getDeclaringClass().getSimpleName();
        return beanService.getBean(uncapitalize(className));
    }


    @Override
    public void invokeUtilMethod(String methodName) {
        try {
            if (methods.containsKey(methodName)) {
                invokeMethod(methods.get(methodName));

            } else throw new MethodIsNotPresentException("Method with name '" + methodName + "' is not exist");
        } catch (MethodExecutionException e) {
            logger.error(e.getMessage());
            throw new UtilMethodExecutionException(e);
        }
    }

    /**
     * Changes a first char of a string to lover case
     * @param str string
     * @return changed string
     */
    private String uncapitalize(String str) {
        String firstChar = String.valueOf(str.charAt(0));
        return str.replaceFirst(firstChar, firstChar.toLowerCase());
    }



    /**
     * Invokes callback method
     * @param methodName methodName
     */
    @Override
    public void invokeCallBack(String methodName)  {
        Method method = callBacks.get(methodName);
        invokeMethod(method);
    }

    @Override
    public void invokeCommand(String methodName)  {
        Method method = commands.get(methodName);
        invokeMethod(method);
    }



    /**
     * Parses callback data into method name and params.
     * Callback data has the next format: 'methodName?param1_param2_param3'
     * @param data callback data
     */
    public void invokeCallBackWithParams(String data) {
        try {
            String[] splittedData = data.split("\\?");
            String methodName = splittedData[0];

            Method method = callBacks.get(methodName);
            if (method != null) {
                String className = uncapitalize(parseClassNameFromClassToString(method));
                Object methodOwner = beanService.getBean(className);

                Object[] params = splittedData[1].split("_");
                method.invoke(methodOwner, params);
            }

        }  catch (InvocationTargetException | IllegalAccessException e) {
            logger.error(e.toString());
            throw new CallBackMethodExecutionException(e);
        }
    }



    public String parseClassNameFromClassToString(Method method) {
        String className = method.getDeclaringClass().getName();
        String[] arr = className.split("\\.");
        return arr[arr.length-1];
    }


    public Map<String, Method> findCallbacks(List<Method> methods) {

        return methods.stream()
                .filter((method -> method.getAnnotation(CallBack.class) != null))
                .collect(Collectors.toMap(method -> {
                    String val = method.getAnnotation(CallBack.class).value();
                    return val.equals("") ? method.getName() : val;
                }, Function.identity()));
    }

    public Map<String, Method> findCommands(List<Method> methods) {

        return methods.stream()
                .filter((method -> method.getAnnotation(Command.class) != null))
                .collect(Collectors.toMap(method -> {
                    String val = method.getAnnotation(Command.class).value();
                    return val.equals("") ? method.getName() : val;
                }, Function.identity()));
    }


    public List<Method> findAllMethods() {
        Map<String, Object> controllers = beanService.getBeansWithAnnotation(Controller.class);
        List<Method> list = new ArrayList<>();

        controllers.values().forEach(
                (obj) -> list.addAll(Arrays.stream(obj.getClass().getDeclaredMethods()).toList())
        );

        return list;
    }


    public boolean hasParams(String data) {
        return data.contains("?");
    }

}
