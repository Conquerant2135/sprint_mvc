package mg.conquerant.sprintmvc.core.web;

import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.Collections;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.conquerant.sprintmvc.core.config.Config;
import mg.conquerant.sprintmvc.core.context.ApplicationContainer;
import mg.conquerant.sprintmvc.core.mapping.MethodMapping;
import mg.conquerant.sprintmvc.core.mapping.UrlInfo;
import mg.conquerant.sprintmvc.core.web.view.ModelAndView;
import mg.conquerant.sprintmvc.core.web.view.ViewResolver;
import mg.conquerant.sprintmvc.utils.JsonConverter;
import mg.conquerant.sprintmvc.core.security.User;

@MultipartConfig
public class FrontControllerServlet extends HttpServlet {

    private transient Map<UrlInfo, MethodMapping> routesMapping;
    private transient ViewResolver viewResolver;
    private transient ApplicationContainer beanContainer;
    private transient JsonConverter converter;
    private transient String sessionVerification;
    private transient String profileKey;
    private transient final String sessionAttribute = Config.getProperty("sprintmvc.session");

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
        sessionVerification = (String) getServletContext().getAttribute("sessionVerification");
        profileKey = (String) getServletContext().getAttribute("profileKey");
    }

    private void sync(HttpSession session) {
        try {
            Session source = (Session) session.getAttribute(sessionAttribute);

            System.out.println("READING THE SESSION VARIABLE");
            if(source == null) return;

            List<String> attributeNames = Collections.list(session.getAttributeNames());

            System.out.println("SYNC STATE");

            for(String attribute : attributeNames){
                if(!source.getAttributes().containsKey(attribute) && !attribute.equals(sessionAttribute)){
                        session.removeAttribute(attribute);
                }
            }

            source.getAttributes().forEach((key , value) -> {
                System.out.println(key);
                session.setAttribute(key,value);
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String contextPath = request.getContextPath();
        String targetResource = request.getRequestURI().substring(contextPath.length());

        UrlInfo urlInfo = new UrlInfo(targetResource, null);
        urlInfo.setMethod(request.getMethod());

        try {

            if (routesMapping.containsKey(urlInfo)) {

                MethodMapping toTest = routesMapping.get(urlInfo);

                if (toTest.isAuthNeeded()) {
                    HttpSession session = request.getSession();
                    Session source = (Session) session.getAttribute(sessionAttribute);
                    Session rawSession = (Session) session.getAttribute(sessionAttribute);
                    System.out.println("--- DEBUG SESSION ---");
                    System.out.println("Objet brut en session : " + rawSession);
                    if (rawSession != null) {
                        System.out.println("Classe de l'objet : " + rawSession.getClass().getName());
                        System.out.println("ClassLoader de l'objet : " + rawSession.getClass().getClassLoader());
                        System.out.println("ClassLoader du Servlet : " + this.getClass().getClassLoader());
                    }
                    if (session.getAttribute(sessionAttribute) == null
                            || request.getSession().getAttribute(sessionVerification) == null) {
                        // System.out.println(request.getSession().getAttribute(sessionVerification));
                        // System.out.println(request.getSession().getAttribute(sessionAttribute));
                        // System.out.println(session.getId());
                        response.setContentType("application/json");
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setCharacterEncoding("UTF-8");
                        response.getOutputStream().print(
                                """
                                        {
                                            "message" : "You must be logged in to access this resource",
                                            "error" : "Error 401"
                                        }
                                        """);
                        throw new RuntimeException("User must be logged to access the resource");
                    }
                }

                if (toTest.isRestricted()) {
                    User toView = (User) request.getSession().getAttribute(profileKey);
                    if (toView == null || !toTest.isPermitted(toView.getRole())) {
                        System.out.println(toView.getRole());
                        response.setContentType("application/json");
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setCharacterEncoding("UTF-8");
                        response.getOutputStream().print(
                                """
                                        {
                                            "message" : "Role not authorized",
                                            "error" : "Error 403"
                                        }
                                        """);
                        throw new RuntimeException("Role not authorized");
                    }
                }

                Object res = toTest.execute(beanContainer, request, response);
                sync(request.getSession());
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
