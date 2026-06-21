package mg.conquerant.sprintmvc.core.scan;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mg.conquerant.sprintmvc.annotation.UrlMapping;
import mg.conquerant.sprintmvc.core.mapping.MethodMapping;

public class MappingBuilder {

    private MappingBuilder() {
    }

    public static Map<String, MethodMapping> scanAndBuildMapping(List<Class<?>> controllerList) {
        Map<String, MethodMapping> methodMap = new HashMap<>();
        for (Class<?> controller : controllerList) {
            Method[] methods = controller.getDeclaredMethods();
            for (Method toAnalyze : methods) {
                if (toAnalyze.isAnnotationPresent(UrlMapping.class)) {
                    UrlMapping urlMapping = toAnalyze.getAnnotation(UrlMapping.class);
                    String path = urlMapping.path();
                    methodMap.put(path, buildMapping(toAnalyze, controller, path));
                }
            }
        }
        return methodMap;
    }

    public static MethodMapping buildMapping(Method method, Class<?> controller, String path) {
        MethodMapping methodMap = new MethodMapping();
        methodMap.setControllerClass(controller);
        methodMap.setPath(path);
        methodMap.setActionMethod(method);
        return methodMap;
    }
}
