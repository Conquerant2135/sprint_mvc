package mg.conquerant.sprintmvc.core.mapping;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

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

    @Override
    public String toString() {
        String toShow = "";

        toShow += "Url : " + getPath();
        toShow += " - Method name : " + getActionMethod().getName();
        toShow += " - Parameters : ";

        Parameter[] params = getActionMethod().getParameters();

        for (Parameter p : params) {
            toShow += "[name: " + p.getName() + ", type: " + p.getType().getSimpleName() + "]";
        }

        toShow += " - Return type : " + getActionMethod().getReturnType().getName();

        return toShow;
    }

}
