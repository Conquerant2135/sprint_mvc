package mg.conquerant.sprintmvc.core.web.view;

import java.util.HashMap;
import java.util.Map;

public class ModelAndView {

    private String view;
    private Map<String, Object> attribute;

    public ModelAndView() {
        attribute = new HashMap<>();
    }

    public ModelAndView(String view) {
        this.view = view;
        attribute = new HashMap<>();
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public Map<String, Object> getAttribute() {
        return attribute;
    }

    public void setAttribute(String key , Object value) {
        this.attribute.put(key, value);
    }

}
