package mg.conquerant.sprintmvc.core.mapping;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mg.conquerant.sprintmvc.core.annotation.RequestBody;
import mg.conquerant.sprintmvc.core.annotation.RequestParam;
import mg.conquerant.sprintmvc.utils.JsonConverter;

public class ParameterResolver {

    public JsonConverter converter;

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

        if (param.isAnnotationPresent(RequestBody.class)) {
            return resolveJsonObject(param, request);
        }

        if (isScalarOrArray(param.getType())) {

            RequestParam definedName = param.getAnnotation(RequestParam.class);
            String name = (definedName != null && !definedName.value().trim().isEmpty())
                    ? definedName.value()
                    : param.getName();

            String[] values = request.getParameterValues(name);

            if (values == null || values.length == 0
                    || (values.length == 1 && values[0].trim().isEmpty() && param.getType() != String.class)) {
                if (param.getType().isArray()) {
                    return Array.newInstance(param.getType().componentType(), 0);
                }
                if (List.class.isAssignableFrom(param.getType())) {
                    return new ArrayList<>();
                }
                if (param.getType().isPrimitive()) {
                    return getDefaultValue(param.getType());
                }
                return null;
            }

            return parseParameter(values, param.getParameterizedType());
        }
        return resolveObject(param.getType(), request, response);
    }

    private Object resolveJsonObject(Parameter param, HttpServletRequest request) {
        try {
            return converter.toObject(request.getInputStream(), param.getParameterizedType());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Object resolveObject(Class<?> toReturn, HttpServletRequest request, HttpServletResponse response) {
        try {
            Object toReturnInstance = toReturn.getDeclaredConstructor().newInstance();
            for (Field field : toReturn.getDeclaredFields()) {
                Class<?> fieldType = field.getType();
                String fieldName = field.getName();
                Object valueToInject = null;
                if (HttpServletRequest.class.isAssignableFrom(fieldType)) {
                    valueToInject = request;
                } else if (HttpServletResponse.class.isAssignableFrom(fieldType)) {
                    valueToInject = response;
                } else if (HttpSession.class.isAssignableFrom(fieldType)) {
                    valueToInject = request.getSession();
                } else {
                    String[] values = request.getParameterValues(fieldName);
                    if (values != null && values.length > 0 && !(values.length == 1 && values[0].trim().isEmpty())) {
                        valueToInject = parseParameter(values, field.getGenericType());
                    } else if (fieldType.isPrimitive()) {
                        valueToInject = getDefaultValue(fieldType);
                    }
                }
                if (valueToInject != null) {
                    invokeSetter(toReturn, toReturnInstance, fieldName, fieldType, valueToInject);
                }
            }
            return toReturnInstance;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void invokeSetter(Class<?> targetClass, Object instance, String fieldName, Class<?> fieldType,
            Object value) {
        String setterName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        try {
            Method setter = targetClass.getMethod(setterName, fieldType);
            setter.invoke(instance, value);
        } catch (Exception e) {
            try {
                Field field = targetClass.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(instance, value);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private Object parseParameter(String[] values, Type targetType) {
        if (targetType instanceof ParameterizedType paramType
                && List.class.isAssignableFrom((Class<?>) paramType.getRawType())) {

            Type rawGenericType = paramType.getActualTypeArguments()[0];
            Class<?> genericClass = (rawGenericType instanceof Class<?>)
                    ? (Class<?>) rawGenericType
                    : String.class;

            String[] effectiveValues = (values.length == 1 && values[0].contains(","))
                    ? values[0].split("\\s*,\\s*")
                    : values;

            List<Object> list = new ArrayList<>();
            for (String val : effectiveValues) {
                list.add(parseParameter(val, genericClass));
            }
            return list;
        } else if (targetType instanceof Class<?> rawClass && List.class.isAssignableFrom(rawClass)) {
            String[] effectiveValues = (values.length == 1 && values[0].contains(","))
                    ? values[0].split("\\s*,\\s*")
                    : values;

            List<Object> list = new ArrayList<>();
            for (String val : effectiveValues) {
                list.add(parseParameter(val, String.class));
            }
            return list;
        }

        if (targetType instanceof Class<?> goal && goal.isArray()) {
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

        if (targetType instanceof Class<?> goalClass) {
            return parseParameter(values[0], goalClass);
        }

        throw new IllegalArgumentException("Unsupported type: " + targetType);
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

    private boolean isScalarOrArray(Class<?> type) {
        if (List.class.isAssignableFrom(type)) {
            return true;
        }
        Class<?> componentType = type.isArray() ? type.componentType() : type;

        return componentType.isPrimitive()
                || componentType == String.class
                || componentType == Integer.class
                || componentType == Double.class
                || componentType == Float.class
                || componentType == Long.class
                || componentType == Short.class
                || componentType == Byte.class
                || componentType == Boolean.class
                || componentType == Character.class
                || componentType == BigDecimal.class
                || componentType == BigInteger.class
                || componentType == LocalDate.class
                || componentType == LocalDateTime.class
                || componentType == LocalTime.class
                || componentType == Date.class;
    }

    private Object parseParameter(String value, Class<?> goal) {
        String trimmed = value.trim();

        if (goal == String.class) {
            return value;
        } else if (goal == LocalDate.class) {
            return LocalDate.parse(trimmed);
        } else if (goal == LocalDateTime.class) {
            if (trimmed.length() == 16) {
                trimmed = trimmed + ":00";
            }
            return LocalDateTime.parse(trimmed);
        } else if (goal == Date.class) {
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