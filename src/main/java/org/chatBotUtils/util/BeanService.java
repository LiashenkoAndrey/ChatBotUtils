package org.chatBotUtils.util;

import java.lang.annotation.Annotation;
import java.util.Map;

public interface BeanService {

    Object getBean(String name);

    Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType);
}
