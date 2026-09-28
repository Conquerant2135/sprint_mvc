package mg.conquerant.sprintmvc.core.web;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.conquerant.sprintmvc.core.context.ApplicationContainer;
import mg.conquerant.sprintmvc.core.mapping.MethodMapping;
import mg.conquerant.sprintmvc.core.mapping.UrlInfo;
import mg.conquerant.sprintmvc.core.web.view.ModelAndView;
import mg.conquerant.sprintmvc.core.web.view.ViewResolver;

public class FrontControllerServlet extends HttpServlet {

    private Map<UrlInfo, MethodMapping> routesMapping;
    private ViewResolver viewResolver;
    private ApplicationContainer beanContainer;

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
        routesMapping = (Map<UrlInfo, MethodMapping>) getServletContext().getAttribute("routesMapping");
        viewResolver = (ViewResolver) getServletContext().getAttribute("viewResolver");
        beanContainer = (ApplicationContainer) getServletContext().getAttribute("beanContainer");
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String contextPath = request.getContextPath();
        String targetResource = request.getRequestURI().substring(contextPath.length());

        UrlInfo urlInfo = new UrlInfo(targetResource, null);
        urlInfo.setMethod(request.getMethod());

        if (routesMapping.containsKey(urlInfo)) {

            Object res = routesMapping.get(urlInfo).execute(beanContainer);
            MethodMapping toTest = routesMapping.get(urlInfo);
            if (res instanceof ModelAndView mv) {
                viewResolver.render(mv, request, response);
            } else if (toTest.isJson()) {
                response.setContentType("application/json");
                response.getOutputStream().println(ow.writeValueAsString(toTest.execute(beanContainer)));
            } else if (res instanceof String ressource) {
                viewResolver.render(new ModelAndView(ressource), request, response);
            }
        }
    }
}
