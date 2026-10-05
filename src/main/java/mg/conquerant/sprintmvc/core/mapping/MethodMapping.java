package mg.conquerant.sprintmvc.core.mapping;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import jakarta.servlet.http.HttpServletRequest;
import mg.conquerant.sprintmvc.core.annotation.HTTPMethod;
import mg.conquerant.sprintmvc.core.context.ApplicationContainer;

public class MethodMapping {

    private Class<?> controllerClass;
    private Method actionMethod;
    private UrlInfo urlInfo;
    private boolean json;

    public Object execute(ApplicationContainer beanContainer, HttpServletRequest request)
            throws IllegalArgumentException {
        try {
            Object toExecute = beanContainer.getBean(controllerClass);
            ParameterResolver resolver = (ParameterResolver) beanContainer.getBean(ParameterResolver.class);
            return actionMethod.invoke(toExecute, resolver.getMethodArgs(this.actionMethod, request));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

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

    public UrlInfo getUrlInfo() {
        return urlInfo;
    }

    public void setUrlInfo(UrlInfo urlInfo) {
        this.urlInfo = urlInfo;
    }

    @Override
    public String toString() {
        String toShow = "";

        toShow += "Class : " + getControllerClass().getName() + " ";

        toShow += " - Url : " + urlInfo.getPath();
        toShow += " - Method name : " + getActionMethod().getName();
        toShow += " - Parameters : ";

        Parameter[] params = getActionMethod().getParameters();

        for (Parameter p : params) {
            toShow += "[name: " + p.getName() + ", type: " + p.getType().getSimpleName() + "]";
        }

        if (urlInfo.getMethod() == HTTPMethod.POST) {
            toShow += " - Method : POST ";
        } else
            toShow += " - Method : GET ";

        toShow += " - return type : " + getActionMethod().getReturnType().getName();

        return toShow;
    }

    public boolean isJson() {
        return json;
    }

    public void setJson(boolean json) {
        this.json = json;
    }
}
