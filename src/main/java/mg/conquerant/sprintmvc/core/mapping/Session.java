package mg.conquerant.sprintmvc.core.mapping;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Session {

    private final String sessionId;
    private final long creationTime;
    private final long lastAccessedTime;
    private final Map<String, Object> attributes = new ConcurrentHashMap<>();
    private final Set<String> keyToRemove = ConcurrentHashMap.newKeySet();

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
        keyToRemove.remove(name);
    }

    public void removeAttribute(String name) {
        attributes.remove(name);
        keyToRemove.add(name);
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public Set<String> getKeyToRemove() {
        return keyToRemove;
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