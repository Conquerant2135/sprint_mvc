package mg.conquerant.sprintmvc.core.context;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.lang.reflect.Constructor;

public class DefaultApplicationContainer implements ApplicationContainer {

    private final Map<Class<?>, Object> beans = new ConcurrentHashMap<>();
    private final Set<Class<?>> inBuild = new HashSet<>();

    @Override
    public Object getBean(Class<?> toGet) {
        Object bean = beans.get(toGet);
        if (bean != null) {
            return bean;
        }
        synchronized(this){
            bean = beans.get(toGet);
            if( bean == null){
                bean = createInstance(toGet);
                addBean(toGet , bean);
            }
            return bean;
        }
    }

    private Object createInstance(Class<?> toGet){
        if(!inBuild.add(toGet)){
            throw new RuntimeException("Circular dependencies are not allowed , detected on " + toGet.getName());
        }
        try {
            Constructor<?> toPutConstr = findCorrectConstructor(toGet);
            toPutConstr.setAccessible(true);
            Class<?>[] types = toPutConstr.getParameterTypes();
            Object[] parameters = new Object[types.length];
            for(int i = 0 ; i < types.length ; i++)
            {
                parameters[i] = getBean(types[i]);
            }
            return toPutConstr.newInstance(parameters);
        } catch (Exception e) {
            throw new RuntimeException("Can't get or create the bean : " + toGet.getName() , e);
        } finally
        {
            inBuild.remove(toGet);
        }
    }

    private Constructor<?> findCorrectConstructor(Class<?> toGet) {
        Constructor<?>[] declaredConstructors = toGet.getDeclaredConstructors();
        if (declaredConstructors.length == 1) return declaredConstructors[0];

        Constructor<?> biggestConstructor = null;
        int greatest = -1;
        for (Constructor<?> c : declaredConstructors) {
            if (c.getParameterCount() > greatest) {
                greatest = c.getParameterCount();
                biggestConstructor = c;
            }
        }
        return biggestConstructor;
    }

    public void addBean(Class<?> toPutKey, Object toPut) {
        beans.put(toPutKey, toPut);
    }
}