package mg.conquerant.sprintmvc.core.web.view;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ViewResolver {

    private String prefix;
    private String suffix;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public void render(ModelAndView modelAndView, HttpServletRequest request, HttpServletResponse response) {
        String target = buildTargetView(modelAndView.getView());
        prepareRequest(modelAndView, request);
        System.out.println("FORWARD TO = " + target);

        RequestDispatcher dispatcher = request.getRequestDispatcher(target);
        try {
            System.out.println("BEFORE FORWARD");
            System.out.println("COMMITTED = " + response.isCommitted());
            System.out.println("DISPATCHER = " + dispatcher);
            dispatcher.forward(request, response);
            System.out.println("AFTER FORWARD");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String buildTargetView(String view) {
        StringBuilder pathBuilder = new StringBuilder(prefix);
        pathBuilder.append(view);
        pathBuilder.append(suffix);
        return pathBuilder.toString();
    }

    private void prepareRequest(ModelAndView modelAndView, HttpServletRequest request) {
        modelAndView.getAttribute().forEach((key, value) -> {
            request.setAttribute(key, value);
        });
    }
}
