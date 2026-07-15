package mg.conquerant.sprintmvc.core.mapping;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import org.springframework.context.ApplicationContext;

import mg.conquerant.sprintmvc.core.annotation.HTTPMethod;

public class MethodMapping {

    private Class<?> controllerClass;
    private Method actionMethod;
    private String path;
    private HTTPMethod urlMethod;
    private UrlInfo urlInfo;

    public HTTPMethod getUrlMethod(){
        return urlMethod;
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

    public void setUrlMethod(HTTPMethod urlMethod){
        this.urlMethod = urlMethod;
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

        toShow += "Class : " + getControllerClass().getName() + " "; 

        toShow += " - Url : " + getPath();
        toShow += " - Method name : " + getActionMethod().getName();
        toShow += " - Parameters : ";

        Parameter[] params = getActionMethod().getParameters();

        for (Parameter p : params) {
            toShow += "[name: " + p.getName() + ", type: " + p.getType().getSimpleName() + "]";
        }

        if ( getUrlMethod() == HTTPMethod.POST ){
            toShow += " - Method : POST ";
        } else toShow += " - Method : GET ";

        toShow += " - return type : " + getActionMethod().getReturnType().getName();

        return toShow;
    }

    public UrlInfo getUrlInfo() {
        return urlInfo;
    }

    public void setUrlInfo(UrlInfo urlInfo) {
        this.urlInfo = urlInfo;
    }

    public Object execute(ApplicationContext app){
        try {
            Object toExecute = controllerClass.getDeclaredConstructor().newInstance();
            return actionMethod.invoke(toExecute , app);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}

