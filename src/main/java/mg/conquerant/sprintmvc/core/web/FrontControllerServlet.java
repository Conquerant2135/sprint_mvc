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
import mg.conquerant.sprintmvc.utils.JsonConverter;

public class FrontControllerServlet extends HttpServlet {

    private transient Map<UrlInfo, MethodMapping> routesMapping;
    private transient ViewResolver viewResolver;
    private transient ApplicationContainer beanContainer;
    private transient JsonConverter converter;

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

    @SuppressWarnings("unchecked")
    @Override
    public void init() throws ServletException {
        routesMapping = (Map<UrlInfo, MethodMapping>) getServletContext().getAttribute("routesMapping");
        viewResolver = (ViewResolver) getServletContext().getAttribute("viewResolver");
        beanContainer = (ApplicationContainer) getServletContext().getAttribute("beanContainer");
        converter = (JsonConverter) getServletContext().getAttribute("converter");
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String contextPath = request.getContextPath();
        String targetResource = request.getRequestURI().substring(contextPath.length());

        UrlInfo urlInfo = new UrlInfo(targetResource, null);
        urlInfo.setMethod(request.getMethod());

        try {

            if (routesMapping.containsKey(urlInfo)) {

                Object res = routesMapping.get(urlInfo).execute(beanContainer, request);
                MethodMapping toTest = routesMapping.get(urlInfo);

                if (res instanceof ModelAndView mv) {
                    viewResolver.render(mv, request, response);
                } else if (toTest.isJson()) {
                    if (res instanceof String ressource) {
                        response.getOutputStream().print(ressource);
                    } else {
                        response.setContentType("application/json");
                        response.getOutputStream().print(
                                converter.toJson(res));
                    }
                } else if (res instanceof String ressource) {
                    viewResolver.render(new ModelAndView(ressource), request, response);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
