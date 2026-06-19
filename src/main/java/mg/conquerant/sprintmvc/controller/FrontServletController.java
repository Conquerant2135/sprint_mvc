package mg.conquerant.sprintmvc.controller;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.ServletException;
import java.util.List;
import java.io.IOException;
import java.io.PrintWriter;
import mg.conquerant.sprintmvc.utils.ClasspathAnalyzer;

public class FrontServletController extends HttpServlet {

    private List<Class<?>> classList;

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
    public void init(){
        ClasspathAnalyzer clp = new ClasspathAnalyzer();
        classList = clp.classList();
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String method = request.getMethod();
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        out.println("<p> The method : " + method + "</p>");
        out.println("<p> The called url : " + request.getRequestURL() + "</p>");

        out.println("<h1> Class list : </h1>");
        out.println("<ul>");
        for(Class<?> cls : classList ){
            out.println("<li>" + cls.getName() + "</li>");
        }
        out.println("</ul>");
    }
}