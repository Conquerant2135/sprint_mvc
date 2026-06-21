package mg.conquerant.sprintmvc.core.mapping;

import java.lang.reflect.Method;

public class MethodMapping {

    private Class<?> controllerClass;
    private Method actionMethod;
    private String path;
    
    public Class<?> getControllerClass() {
        return controllerClass;
    }
    public void setControllerClass(Class<?> controllerClass) {
        this.controllerClass = controllerClass;
    }
    public Method getActionMethod() {
        return actionMethod;
    }
    public void setActionMethod(Method actionMethod) {
        this.actionMethod = actionMethod;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }

}
