package mg.conquerant.sprintmvc.core.context;

import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.Constructor;

public class DefaultApplicationContainer implements ApplicationContainer {

    private final Map<Class<?>, Object> beans = new HashMap<>();

    @Override
    public Object getBean(Class<?> toGet) {
        if (beans.containsKey(toGet)) {
            return beans.get(toGet);
        }
        try {
            Constructor<?> toPutConstr = toGet.getDeclaredConstructor();
            Object toPut = toPutConstr.newInstance();
            beans.put(toGet, toPut);
            return toPut;
        } catch (Exception e) {
            throw new RuntimeException("Can't get or create the bean : " + toGet.getName() , e);
        }
    }

    public void addBean(Class<?> toPutKey, Object toPut) {
        beans.put(toPutKey, toPut);
    }
}