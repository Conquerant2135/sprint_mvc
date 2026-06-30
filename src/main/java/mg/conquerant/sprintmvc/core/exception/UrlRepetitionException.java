package mg.conquerant.sprintmvc.core.exception;


import mg.conquerant.sprintmvc.core.mapping.UrlInfo;
import java.lang.reflect.Method;

public class UrlRepetitionException extends RuntimeException {
    public UrlRepetitionException(Method method , UrlInfo urlInfo){
        super(" Repetition of the url 2 times on the method : " + method.getName() + " - With the same HTTP method : " + urlInfo.getMethod().toString() );
    }
}
