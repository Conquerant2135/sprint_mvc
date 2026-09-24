package mg.conquerant.sprintmvc.core.mapping;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import mg.conquerant.sprintmvc.core.annotation.HTTPMethod;
import mg.conquerant.sprintmvc.core.context.ApplicationContainer;

public class MethodMapping {

    private Class<?> controllerClass;
    private Method actionMethod;
    private String path;
    private HTTPMethod urlMethod;
    private UrlInfo urlInfo;
    private boolean json;

    

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

    public Object execute(ApplicationContainer beanContainer){
        try {
            Object toExecute = beanContainer.getBean(controllerClass);
            return actionMethod.invoke(toExecute);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public boolean isJson() {
        return json;
    }

    public void setJson(boolean json) {
        this.json = json;
    }
}

