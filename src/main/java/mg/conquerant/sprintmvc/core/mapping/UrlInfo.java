package mg.conquerant.sprintmvc.core.mapping;

import java.util.Objects;

import mg.conquerant.sprintmvc.annotation.HTTPMethod;

public class UrlInfo {
    private String path;
    private HTTPMethod method;

    public UrlInfo(String path, HTTPMethod method) {
        this.path = path;
        this.method = method;
    }

    public void setMethod(String method) {
        this.method = HTTPMethod.valueOf(method);
    }

    public void setMethod(HTTPMethod method) {
        this.method = method;
    }

    public HTTPMethod getMethod() {
        return method;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, method);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof UrlInfo url))
            return false;

        return Objects.equals(path, url.path)
                && method == url.method;
    }

    @Override
    public String toString(){
        return path + " " + method.toString();
    }
}