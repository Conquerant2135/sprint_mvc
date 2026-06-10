package mg.conquerant.sprintmvc.controller;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.ServletException;

import java.io.IOException;
import java.io.PrintWriter;

public class FrontControllerServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String method = request.getMethod();
        PrintWriter out = response.getWriter();
        out.println("The method : " + method);
        out.println("The called url : " + request.getRequestURI());
    }
}