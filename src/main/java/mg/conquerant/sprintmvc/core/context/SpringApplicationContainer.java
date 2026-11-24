package mg.conquerant.sprintmvc.core.context;

import org.springframework.context.ApplicationContext;

public class SpringApplicationContainer implements ApplicationContainer {

    private final ApplicationContext context;

    public SpringApplicationContainer(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public Object getBean(Class<?> clazz) {
        if (context.getBeanNamesForType(clazz).length != 0) {
            return context.getBean(clazz);
        }
        return context.getAutowireCapableBeanFactory().createBean(clazz);
    }
}