package mg.conquerant.sprintmvc.core.scan;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import mg.conquerant.sprintmvc.core.annotation.HTTPMethod;
import mg.conquerant.sprintmvc.core.annotation.UrlMapping;
import mg.conquerant.sprintmvc.core.exception.UrlRepetitionException;
import mg.conquerant.sprintmvc.core.mapping.MethodMapping;
import mg.conquerant.sprintmvc.core.mapping.UrlInfo;
import mg.conquerant.sprintmvc.core.annotation.ResponseBody;

public class MappingBuilder {

    private MappingBuilder() {
    }

    public static void buildRoutesMapping(Map<UrlInfo, MethodMapping> routesMapping ,  List<Class<?>> controllerList) {
        for (Class<?> controller : controllerList) {
            Method[] methods = controller.getDeclaredMethods();
            for (Method toAnalyze : methods) {
                if (toAnalyze.isAnnotationPresent(UrlMapping.class)) {
                    UrlMapping urlMapping = toAnalyze.getAnnotation(UrlMapping.class);
                    String path = urlMapping.path();
                    HTTPMethod urlMethod = urlMapping.method();
                    UrlInfo test = new UrlInfo(path,urlMethod);
            
                    if ( routesMapping.containsKey(test) ){
                        throw new UrlRepetitionException(toAnalyze,test);
                    }
                    routesMapping.put(test, buildMapping(toAnalyze, controller, path, urlMethod, toAnalyze.isAnnotationPresent(ResponseBody.class)));
                }
            }
        }
    }

    private static MethodMapping buildMapping(Method method, Class<?> controller, String path, HTTPMethod urlMethod, boolean isJson) {
        MethodMapping methodMap = new MethodMapping();
        methodMap.setControllerClass(controller);
        methodMap.setPath(path);
        methodMap.setActionMethod(method);
        methodMap.setUrlMethod(urlMethod);
        methodMap.setJson(isJson);
        return methodMap;
    }
}
