package mg.conquerant.sprintmvc.core.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.conquerant.sprintmvc.core.mapping.MethodMapping;
import mg.conquerant.sprintmvc.core.mapping.UrlInfo;

public class FrontControllerServlet extends HttpServlet {

    private List<Class<?>> classList;
    private Map<UrlInfo, MethodMapping> routesMapping;

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
        classList = (List<Class<?>>) getServletContext().getAttribute("controllerList");
        routesMapping =  (Map<UrlInfo, MethodMapping>) getServletContext().getAttribute("routesMapping");
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

        UrlInfo urlInfo = new UrlInfo(targetResource, null);
        urlInfo.setMethod(request.getMethod());
        out.print(urlInfo);
        out.print("<h2>");
        if (routesMapping.containsKey(urlInfo)) {
            out.print(" We have this resource but we also have : ");
            MethodMapping toTest = routesMapping.get(urlInfo);
            toTest.execute();
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