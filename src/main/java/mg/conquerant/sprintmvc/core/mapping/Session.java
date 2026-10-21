package mg.conquerant.sprintmvc.core.mapping;

import java.util.Map;
import java.util.HashMap;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import jakarta.servlet.http.HttpSession;

public class Session {

    private final String sessionId;
    private final long creationTime;
    private final long lastAccessedTime;
    private final Map<String, Object> attributes = new ConcurrentHashMap<>();
    
    public static Session buildSessionInstance(HttpSession session)
    {
        Map<String,Object> attributes = new HashMap<>();
        Enumeration<String> attributesNames = session.getAttributeNames();
        while(attributesNames.hasMoreElements()){
            String attributeName = attributesNames.nextElement();
            Object toPut = session.getAttribute(attributeName);
            attributes.put(attributeName,toPut);
        }
        return new Session(session.getId() , session.getCreationTime() , session.getLastAccessedTime() , attributes);
    }

    public Session(String sessionId, long creationTime, long lastAccessedTime,
            Map<String, Object> initialAttributes) {
        this.sessionId = sessionId;
        this.creationTime = creationTime;
        this.lastAccessedTime = lastAccessedTime;
        this.attributes.putAll(initialAttributes);
    }

    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }
    
    public String getSessionId() {
        return sessionId;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public long getLastAccessedTime() {
        return lastAccessedTime;
    }
}