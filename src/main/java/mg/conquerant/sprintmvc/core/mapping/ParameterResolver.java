package mg.conquerant.sprintmvc.core.mapping;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mg.conquerant.sprintmvc.core.annotation.RequestParam;

public class ParameterResolver {

    public Object[] getMethodArgs(Method method, HttpServletRequest request, HttpServletResponse response) {
        Parameter[] parameters = method.getParameters();
        Object[] arguments = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            arguments[i] = resolveParameter(parameters[i], request, response);
        }

        return arguments;
    }

    private Object resolveParameter(Parameter param, HttpServletRequest request, HttpServletResponse response) {
        if (HttpServletRequest.class.isAssignableFrom(param.getType())) {
            return request;
        } else if (HttpServletResponse.class.isAssignableFrom(param.getType())) {
            return response;
        } else if (HttpSession.class.isAssignableFrom(param.getType())) {
            return request.getSession();
        }
        RequestParam definedName = param.getAnnotation(RequestParam.class);
        String name = null;
        if (definedName != null && !definedName.value().trim().isEmpty()) {
            name = definedName.value();
        } else {
            name = param.getName();
        }

        String[] values = request.getParameterValues(name);

        if (values == null || values.length == 0
                || (values.length == 1 && values[0].trim().isEmpty() && param.getType() != String.class)) {
            if (param.getType().isArray()) {
                return Array.newInstance(param.getType().componentType(), 0);
            }
            if (param.getType().isPrimitive()) {
                // throw new IllegalArgumentException("The mandatory parameter is missing : " +
                // name); a
                return getDefaultValue(param.getType());
            }
            return null;
        }

        return parseParameter(values, param.getType());
    }

    private Object parseParameter(String[] values, Class<?> goal) {
        if (goal.isArray()) {
            Class<?> componentType = goal.componentType();

            String[] effectiveValues = (values.length == 1 && values[0].contains(","))
                    ? values[0].split("\\s*,\\s*")
                    : values;

            Object toReturn = Array.newInstance(componentType, effectiveValues.length);
            for (int i = 0; i < effectiveValues.length; i++) {
                Array.set(toReturn, i, parseParameter(effectiveValues[i], componentType));
            }
            return toReturn;
        }
        return parseParameter(values[0], goal);
    }

    private Object getDefaultValue(Class<?> type) {
        if (type == boolean.class)
            return false;
        if (type == int.class)
            return 0;
        if (type == long.class)
            return 0L;
        if (type == double.class)
            return 0.0d;
        if (type == float.class)
            return 0.0f;
        if (type == char.class)
            return '\0';
        if (type == short.class)
            return (short) 0;
        if (type == byte.class)
            return (byte) 0;
        return null;
    }

    private Object parseParameter(String value, Class<?> goal) {
        String trimmed = value.trim();

        if (goal == String.class) {
            return value;
        } else if (goal == LocalDate.class) {
            return LocalDate.parse(trimmed);
        } else if (goal == LocalDateTime.class) {
            return LocalDateTime.parse(trimmed);
        } else if (goal == java.util.Date.class) {
            try {
                return new java.text.SimpleDateFormat("yyyy-MM-dd").parse(trimmed);
            } catch (java.text.ParseException e) {
                throw new IllegalArgumentException("Date format invalid (attendu yyyy-MM-dd) : " + trimmed, e);
            }
        } else if (goal == Integer.class || int.class == goal) {
            return Integer.valueOf(trimmed);
        } else if (goal == Double.class || double.class == goal) {
            return Double.valueOf(trimmed);
        } else if (goal == Float.class || float.class == goal) {
            return Float.valueOf(trimmed);
        } else if (goal == Long.class || long.class == goal) {
            return Long.valueOf(trimmed);
        } else if (goal == Short.class || short.class == goal) {
            return Short.valueOf(trimmed);
        } else if (goal == Byte.class || byte.class == goal) {
            return Byte.valueOf(trimmed);
        } else if (goal == BigDecimal.class) {
            return new BigDecimal(trimmed);
        } else if (goal == Boolean.class || boolean.class == goal) {
            return "true".equalsIgnoreCase(trimmed) || "on".equalsIgnoreCase(trimmed) || "1".equals(trimmed);
        } else if (goal == char.class || goal == Character.class) {
            if (trimmed.length() != 1) {
                throw new IllegalArgumentException("Impossible to convert : '" + value + "' into a char");
            }
            return trimmed.charAt(0);
        } else {
            throw new IllegalArgumentException("Unsupported type : " + goal.getName());
        }
    }
}