package mg.conquerant.sprintmvc.core.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import mg.conquerant.sprintmvc.core.annotation.Controller;
import mg.conquerant.sprintmvc.core.context.ApplicationContainer;
import mg.conquerant.sprintmvc.core.context.DefaultApplicationContainer;
import mg.conquerant.sprintmvc.core.context.SpringApplicationContainer;
import mg.conquerant.sprintmvc.core.mapping.MethodMapping;
import mg.conquerant.sprintmvc.core.mapping.UrlInfo;
import mg.conquerant.sprintmvc.core.scan.MappingBuilder;
import mg.conquerant.sprintmvc.core.web.view.ViewResolver;
import mg.conquerant.sprintmvc.utils.ClasspathAnalyzer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


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

        ClasspathAnalyzer clp = new ClasspathAnalyzer();
        Set<Class<?>> classList = new HashSet<>();
        Map<UrlInfo, MethodMapping> routesMapping = new HashMap<>();

        for (String pkg : packageList) {
            classList.addAll(clp.classList(pkg, Controller.class));
        }

        String suffix = appContext.getInitParameter("suffix");
        String prefix = appContext.getInitParameter("prefix");
        ViewResolver viewResolver = new ViewResolver();
        viewResolver.setPrefix(prefix);
        viewResolver.setSuffix(suffix);

        List<Class<?>> uniqueClassList = new ArrayList<>(classList);
        MappingBuilder.buildRoutesMapping(routesMapping, uniqueClassList);

        ApplicationContext springContext = WebApplicationContextUtils.getWebApplicationContext(appContext);
        ApplicationContainer appContainer;

        if (springContext != null) {
            appContainer = new SpringApplicationContainer(springContext);
        } else {
            appContainer = new DefaultApplicationContainer();
        }

        ObjectWriter writer = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writer()
                .withDefaultPrettyPrinter();
        

        appContext.setAttribute("objectWriter", writer);
        appContext.setAttribute("routesMapping", routesMapping);
        appContext.setAttribute("viewResolver", viewResolver);
        appContext.setAttribute("beanContainer", appContainer);
    }
}
