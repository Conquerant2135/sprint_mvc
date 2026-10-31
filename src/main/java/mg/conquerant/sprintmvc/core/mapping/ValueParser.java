package mg.conquerant.sprintmvc.core.mapping;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;

import mg.conquerant.sprintmvc.utils.TypeUtils;


public class ValueParser {

    private ValueParser() {
    }

    private static String[] splitValues(String[] values) {
        return (values.length == 1 && values[0].contains(","))
                ? values[0].split("\\s*,\\s*")
                : values;
    }

    static Object parseParameter(String[] values, Type targetType) {
        if (targetType instanceof ParameterizedType paramType
                && Collection.class.isAssignableFrom((Class<?>) paramType.getRawType())) {

            Type rawGenericType = paramType.getActualTypeArguments()[0];
            Class<?> genericClass = (rawGenericType instanceof Class<?>)
                    ? (Class<?>) rawGenericType
                    : String.class;

            Collection<Object> collection = TypeUtils.newCollection((Class<?>) paramType.getRawType());
            for (String val : splitValues(values)) {
                collection.add(parseParameter(val, genericClass));
            }
            return collection;
        } else if (targetType instanceof Class<?> rawClass && Collection.class.isAssignableFrom(rawClass)) {
            Collection<Object> collection = TypeUtils.newCollection(rawClass);
            for (String val : splitValues(values)) {
                collection.add(parseParameter(val, String.class));
            }
            return collection;
        }

        if (targetType instanceof Class<?> goal && goal.isArray()) {
            Class<?> componentType = goal.componentType();
            String[] effectiveValues = splitValues(values);

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

    static Object parseParameter(String value, Class<?> goal) {
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