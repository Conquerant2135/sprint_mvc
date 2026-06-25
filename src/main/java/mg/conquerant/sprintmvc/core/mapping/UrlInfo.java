package mg.conquerant.sprintmvc.core.mapping;

import mg.conquerant.sprintmvc.annotation.HTTPMethod;

public class UrlInfo {
    private String path;
    private HTTPMethod method;

    public UrlInfo(String path , HTTPMethod method){
        this.path = path;
        this.method = method;
    }

    public void setMethod(String method){
        if (method.equals("POST")) {
            this.method = HTTPMethod.POST;
        } else this.method = HTTPMethod.GET;
    }

    public void setMethod(HTTPMethod method){
        this.method = method;
    }

    public HTTPMethod getMethod(){
        return method;
    }

    public void setPath(String path){
        this.path = path;
    }

    public String getPath(){
        return path;
    }

    @Override
    public boolean equals(Object obj){
        if ( this == obj ) return true;

        if (obj == null || getClass() != obj.getClass()) return false;

        UrlInfo url = (UrlInfo) obj;

        return url.getPath().equals(getPath()) && getMethod() == url.getMethod(); 
    }
}