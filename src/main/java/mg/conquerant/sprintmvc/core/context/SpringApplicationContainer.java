package mg.conquerant.sprintmvc.core.context;

import org.springframework.context.ApplicationContext;

public class SpringApplicationContainer implements ApplicationContainer {

    private final ApplicationContext context;

    public SpringApplicationContainer(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public Object getBean(Class<?> clazz) {
        return context.getBean(clazz);
    }
}