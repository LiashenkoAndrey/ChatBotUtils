import lombok.RequiredArgsConstructor;
import org.chatBotUtils.util.BeanService;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class BeanServiceImpl implements BeanService {

    private final ApplicationContext context;


    @Override
    public Object getBean(String name) {
        return context.getBean(name);
    }

    @Override
    public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) {
        return context.getBeansWithAnnotation(annotationType);
    }
}