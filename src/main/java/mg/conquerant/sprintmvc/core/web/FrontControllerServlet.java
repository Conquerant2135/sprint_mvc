package mg.conquerant.sprintmvc.core.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.conquerant.sprintmvc.annotation.Controller;
import mg.conquerant.sprintmvc.core.mapping.MethodMapping;
import mg.conquerant.sprintmvc.core.scan.MappingBuilder;
import mg.conquerant.sprintmvc.utils.ClasspathAnalyzer;

public class FrontControllerServlet extends HttpServlet {

    private List<Class<?>> classList;
    private Map<String, MethodMapping> routesMapping;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public void init() throws ServletException {
        String blockPackage = getServletConfig().getInitParameter("package_list");
        String separator = getServletConfig().getInitParameter("list_separator");
        String[] packageList = blockPackage.split(separator);
        Set<Class<?>> withAnnotation = new HashSet<>();
        ClasspathAnalyzer clp = new ClasspathAnalyzer();

        for (String pkg : packageList) {
            classList = clp.classList(pkg);
            for (Class<?> cls : classList) {
                if (cls.isAnnotationPresent(Controller.class)) {
                    withAnnotation.add(cls);
                }
            }
        }

        classList = new ArrayList<>(withAnnotation);
        routesMapping = MappingBuilder.scanAndBuildMapping(classList);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        out.println("<p> The method : " + request.getMethod() + "</p>");
        out.println("<p> The called URL : " + request.getRequestURL() + "</p>");
        out.println("<p> The called URI : " + request.getRequestURI() + "</p>");
        out.println("<h1> Class list : </h1>");
        out.println("<ul>");

        for (Class<?> cls : classList) {
            out.println("<li>" + cls.getName() + "</li>");
        }
        out.println("</ul>");
        String contextPath = request.getContextPath();
        String targetResource = request.getRequestURI().substring(contextPath.length());

        out.print("<h2>");
        if (routesMapping.containsKey(targetResource)) {
            out.print(" We have this resource but we also have : ");
        } else {
            out.print("We dont have the requested ressource but instead we have :");
        }
        out.println("</h2>");
        out.println("<ul>");
        for (MethodMapping methodMapping : routesMapping.values()) {
            out.println("<li> " + methodMapping + " </li>");
        }
        out.println("</ul>");
    }
}