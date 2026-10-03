package mg.conquerant.sprintmvc.core.mapping;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import jakarta.servlet.http.HttpServletRequest;
import mg.conquerant.sprintmvc.core.annotation.HTTPMethod;
import mg.conquerant.sprintmvc.core.annotation.RequestParam;
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
            return actionMethod.invoke(toExecute, getMethodArgs(request));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Object[] getMethodArgs(HttpServletRequest request) throws IllegalArgumentException {
        Parameter[] parameters = actionMethod.getParameters();
        Object[] arguments = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            arguments[i] = resolveParameter(parameters[i], request);
        }
        return arguments;
    }

    private Object resolveParameter(Parameter param, HttpServletRequest request) throws IllegalArgumentException {
        RequestParam definedName = param.getAnnotation(RequestParam.class);
        String name = definedName != null ? definedName.value() : param.getName();

        String value = request.getParameter(name);
        if (value == null) {
            throw new IllegalArgumentException("This parameter is not defined : " + name);
        }
        return parseParameter(value, param.getType());
    }

    private Object parseParameter(String value, Class<?> goal) throws IllegalArgumentException {
        if (goal == String.class) {
            return value;
        } else if (goal == Integer.class || int.class == goal) {
            return Integer.valueOf(value);
        } else if (goal == Double.class || double.class == goal) {
            return Double.valueOf(value);
        } else if (goal == Long.class || long.class == goal) {
            return Long.valueOf(value);
        } else if (goal == Boolean.class || boolean.class == goal) {
            return Boolean.valueOf(value);
        } else if (goal == char.class) {
            if (value.length() != 1) {
                throw new IllegalArgumentException(
                        "Cannot convert '" + value + "' to char");
            }
            return value.charAt(0);
        } else
            throw new IllegalArgumentException(
                    "We don't actually support this parameter type :') : " + goal.getName());
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
