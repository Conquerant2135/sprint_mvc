package mg.conquerant.sprintmvc.core.web;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.conquerant.sprintmvc.core.mapping.MethodMapping;
import mg.conquerant.sprintmvc.core.mapping.UrlInfo;
import mg.conquerant.sprintmvc.core.web.view.ModelAndView;
import mg.conquerant.sprintmvc.core.web.view.ViewResolver;

public class FrontControllerServlet extends HttpServlet {

    private List<Class<?>> classList;
    private Map<UrlInfo, MethodMapping> routesMapping;
    private ViewResolver viewResolver;

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
        routesMapping = (Map<UrlInfo, MethodMapping>) getServletContext().getAttribute("routesMapping");
        String suffix = getInitParameter("suffix");
        String prefix = getInitParameter("prefix");
        viewResolver = new ViewResolver();
        viewResolver.setPrefix(prefix);
        viewResolver.setSuffix(suffix);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String contextPath = request.getContextPath();
        String targetResource = request.getRequestURI().substring(contextPath.length());

        UrlInfo urlInfo = new UrlInfo(targetResource, null);
        urlInfo.setMethod(request.getMethod());

        if (routesMapping.containsKey(urlInfo)) {

            Object res = routesMapping.get(urlInfo).execute();

            if (res instanceof ModelAndView mv) {
                viewResolver.render(mv, request, response);
            }
        }
    }
}