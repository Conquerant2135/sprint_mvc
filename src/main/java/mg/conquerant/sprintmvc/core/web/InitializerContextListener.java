package mg.conquerant.sprintmvc.core.web;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import mg.conquerant.sprintmvc.core.annotation.Controller;
import mg.conquerant.sprintmvc.core.mapping.MethodMapping;
import mg.conquerant.sprintmvc.core.mapping.UrlInfo;
import mg.conquerant.sprintmvc.core.scan.MappingBuilder;
import mg.conquerant.sprintmvc.utils.ClasspathAnalyzer;

public class InitializerContextListener implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext appContext = sce.getServletContext();
        String blockPackage = appContext.getInitParameter("package_list");
        String separator = appContext.getInitParameter("list_separator");
        String[] packageList = blockPackage.split(separator);
        Set<Class<?>> withAnnotation = new HashSet<>();
        ClasspathAnalyzer clp = new ClasspathAnalyzer();
        List<Class<?>> classList;
        Map<UrlInfo, MethodMapping> routesMapping;

        for (String pkg : packageList) {
            classList = clp.classList(pkg);
            for (Class<?> cls : classList) {
                if (cls.isAnnotationPresent(Controller.class)) {
                    withAnnotation.add(cls);
                }
            }
        }

        System.out.println("InitializerContextListener triggered");
        System.out.println("Packages: " + blockPackage);
        System.out.println("Controllers found: " + withAnnotation.size());

        classList = new ArrayList<>(withAnnotation);
        routesMapping = MappingBuilder.scanAndBuildMapping(classList);

        appContext.setAttribute("routesMapping", routesMapping);
        appContext.setAttribute("controllerList", classList);

    }
}
