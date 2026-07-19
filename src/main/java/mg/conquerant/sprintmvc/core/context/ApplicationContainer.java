package mg.conquerant.sprintmvc.core.context;

public interface ApplicationContainer {
    Object getBean(Class<?> toGet);
}
