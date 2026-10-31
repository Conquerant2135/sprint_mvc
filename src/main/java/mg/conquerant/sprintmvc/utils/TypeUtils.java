package mg.conquerant.sprintmvc.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.TreeSet;

public class TypeUtils {
    private TypeUtils() {
    }

    public static Class<?> rawClass(Type t) {
        if (t instanceof ParameterizedType pt)
            return (Class<?>) pt.getRawType();
        return (Class<?>) t;
    }

    public static Type listElementType(Type t) {
        if (t instanceof ParameterizedType pt)
            return pt.getActualTypeArguments()[0];
        return String.class;
    }

    public static Collection<Object> newCollection(Class<?> raw) {
        if (raw.isAssignableFrom(ArrayList.class))
            return new ArrayList<>();
        if (raw.isAssignableFrom(LinkedHashSet.class))
            return new LinkedHashSet<>();
        if (raw.isAssignableFrom(TreeSet.class))
            return new TreeSet<>();
        if (raw.isAssignableFrom(LinkedList.class))
            return new LinkedList<>();
        throw new IllegalArgumentException("We don't support your collection type , change yours : " + raw.getName());
    }

    public static Object getDefaultValue(Class<?> type) {
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

    public static boolean isScalarOrArray(Class<?> type) {
        if (Collection.class.isAssignableFrom(type)) {
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
}