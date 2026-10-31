package mg.conquerant.sprintmvc.core.mapping;

import static mg.conquerant.sprintmvc.utils.TypeUtils.getDefaultValue;
import static mg.conquerant.sprintmvc.utils.TypeUtils.isScalarOrArray;
import static mg.conquerant.sprintmvc.utils.TypeUtils.listElementType;
import static mg.conquerant.sprintmvc.utils.TypeUtils.newCollection;
import static mg.conquerant.sprintmvc.utils.TypeUtils.rawClass;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Stack;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import mg.conquerant.sprintmvc.core.annotation.RequestBody;
import mg.conquerant.sprintmvc.core.annotation.RequestParam;
import mg.conquerant.sprintmvc.core.config.Config;
import mg.conquerant.sprintmvc.core.web.Session;
import mg.conquerant.sprintmvc.utils.JsonConverter;

public class ParameterResolver {

    public JsonConverter converter;
    private final CollectionProcessing collections = new CollectionProcessing();
    private final String sessionAttribute = Config.getProperty("sprintmvc.session");

    private Object resolveJsonObject(Parameter param, HttpServletRequest request) {
        try {
            return converter.toObject(request.getInputStream(), param.getParameterizedType());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

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
        } else if (Session.class.isAssignableFrom(param.getType())) {
            HttpSession session = request.getSession();
            Session source = (Session) session.getAttribute(sessionAttribute);
            if (source == null) {
                source = Session.buildSessionInstance(request.getSession());
                session.setAttribute(sessionAttribute, source);
                return source;
            }
            return source;
        }

        if (param.isAnnotationPresent(RequestBody.class)) {
            return resolveJsonObject(param, request);
        }

        Map<String, String[]> values = request.getParameterMap();
        Stack<String> key = new Stack<>();
        RequestParam definedName = param.getAnnotation(RequestParam.class);
        String name = (definedName != null && !definedName.value().trim().isEmpty())
                ? definedName.value()
                : param.getName();

        if (param.getType() == FileContainer.class) {
            try {
                return FileParts.toContainer(request.getPart(name));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (isScalarOrArray(param.getType()) || param.getType().isArray()
                || collections.hasKeysUnder(values.keySet(), name)) {

            key.push(name);
        }
        return resolve(values, key, param.getParameterizedType(), request, response);
    }

    private Object resolve(Map<String, String[]> values, Stack<String> key, Type goal,
            HttpServletRequest request, HttpServletResponse response) {

        Class<?> raw = rawClass(goal);
        boolean isCollection = Collection.class.isAssignableFrom(raw);

        if (isCollection || raw.isArray()) {
            Type elemType = isCollection ? listElementType(goal) : raw.componentType();

            if (!isScalarOrArray(rawClass(elemType))) {
                Collection<Object> items = raw.isArray() ? new ArrayList<>() : newCollection(raw);

                if (rawClass(elemType) == FileContainer.class) {
                    try {
                        CollectionProcessing cp = new CollectionProcessing();
                        for (Part part : request.getParts()) {
                            if (part.getName().equals(cp.buildKey(key)) && part.getSize() > 0) {
                                items.add(FileParts.toContainer(part));
                            }
                        }
                        return items;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                String prefix = collections.buildKey(key);
                for (int index : collections.countElements(values.keySet(), prefix)) {
                    key.push("[" + index + "]");
                    items.add(resolve(values, key, elemType, request, response));
                    key.pop();
                }

                if (raw.isArray()) {
                    Object array = Array.newInstance(raw.componentType(), items.size());
                    int i = 0;
                    for (Object item : items)
                        Array.set(array, i++, item);
                    return array;
                }
                return items;
            }
        }

        if (isScalarOrArray(raw)) {
            String[] vals = values.get(collections.buildKey(key));
            if (vals == null || vals.length == 0
                    || (vals.length == 1 && vals[0].trim().isEmpty() && raw != String.class)) {
                if (raw.isArray())
                    return Array.newInstance(raw.componentType(), 0);
                if (raw.isPrimitive())
                    return getDefaultValue(raw);
                if (Collection.class.isAssignableFrom(raw))
                    return newCollection(raw);
                return null;
            }
            return ValueParser.parseParameter(vals, goal);
        }

        if (!key.isEmpty() && !collections.hasKeysUnder(values.keySet(), collections.buildKey(key))) {
            return null;
        }

        return resolveObject(values, raw, key, request, response);
    }

    private Object resolveObject(Map<String, String[]> responses, Class<?> toReturn, Stack<String> key,
            HttpServletRequest request,
            HttpServletResponse response) {
        try {
            Object toReturnInstance = toReturn.getDeclaredConstructor().newInstance();
            for (Class<?> c = toReturn; c != null && c != Object.class; c = c.getSuperclass()) {
                for (Field field : c.getDeclaredFields()) {
                    if (Modifier.isStatic(field.getModifiers()))
                        continue;
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
                        key.push(fieldName);
                        valueToInject = resolve(responses, key, field.getGenericType(), request, response);
                        key.pop();
                    }
                    if (valueToInject != null) {
                        invokeSetter(c, toReturnInstance, fieldName, fieldType, valueToInject);
                    }
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
}