package mg.conquerant.sprintmvc.core.mapping;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import jakarta.servlet.http.HttpServletRequest;
import mg.conquerant.sprintmvc.core.annotation.RequestParam;

public class ParameterResolver {

    public Object[] getMethodArgs(
            Method method,
            HttpServletRequest request) {

        Parameter[] parameters = method.getParameters();
        Object[] arguments = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            arguments[i] = resolveParameter(
                    parameters[i],
                    request);
        }

        return arguments;
    }

    private Object resolveParameter(Parameter param, HttpServletRequest request) throws IllegalArgumentException {
        RequestParam definedName = param.getAnnotation(RequestParam.class);
        String name = definedName != null ? definedName.value() : param.getName();
        String[] values = request.getParameterMap().get(name);

        if (values == null) {
            throw new IllegalArgumentException("This parameter is not defined : " + name);
        }
        return parseParameter(values, param.getType());
    }

    private Object parseParameter(String[] values, Class<?> goal) {
        if (goal.isArray()) {
            Class<?> componentType = goal.componentType();
            Object toReturn = Array.newInstance(componentType, values.length);
            for (int i = 0; i < values.length; i++) {
                Array.set(
                        toReturn,
                        i,
                        parseParameter(values[i], componentType));
            }

            return toReturn;
        }
        return parseParameter(values[0], goal);
    }

    private Object parseParameter(String value, Class<?> goal) throws IllegalArgumentException {
        if (goal == String.class) {
            return value;
        } else if (goal == Integer.class || int.class == goal) {
            return Integer.valueOf(value);
        } else if (goal == Double.class || double.class == goal) {
            return Double.valueOf(value);
        } else if (goal == Float.class || float.class == goal) {
            return Float.valueOf(value);
        } else if (goal == Long.class || long.class == goal) {
            return Long.valueOf(value);
        } else if (goal == Boolean.class || boolean.class == goal) {
            return Boolean.valueOf(value);
        } else if (goal == char.class) {
            if (value.length() != 1) {
                throw new IllegalArgumentException(
                        "Cannot convert '" + value + "' to char");
            }
            return value.charAt(0);
        } else
            throw new IllegalArgumentException(
                    "We don't actually support this parameter type :') : " + goal.getName());
    }
}
