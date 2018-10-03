package tech.coinbub.daemon.testutils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class Util {
    private Util() {}

    public static Map<String, String> headers(final String username, final String password) {
        final String cred = Base64.getEncoder().encodeToString((username + ":" + password)
                .getBytes(StandardCharsets.UTF_8));
        final Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic " + cred);
        headers.put("Content-Type", "application/json");
        return headers;
    }

    /**
     * Retrieve the nested field indicated by the provided path.
     * 
     * Example path: transaction[0].details[1].address
     * 
     * @param path The path to resolve
     * @param obj The object to examine
     * @return The resulting object, or null
     */
    public static Object resolve(final String path, final Object obj) {
        if (path == null || path.isEmpty()) {
            return obj;
        }

        // Get first part of path
        String part = path;
        String remaining = null;
        if (path.contains(".")) {
            part = path.substring(0, path.indexOf("."));
            remaining = path.substring(path.indexOf(".") + 1);
        }

        // Check for array index
        String index = null;
        if (part.endsWith("]")) {
            index = part.substring(part.indexOf("[") + 1, part.indexOf("]"));
            part = part.substring(0, part.indexOf("["));
        }
        
        // Get the field
        final Object field = getField(part, obj);

        // If this is an array (or map) index
        if (index != null) {
            if (remaining == null || remaining.isEmpty()) {
                remaining = index;
            } else {
                remaining = index + "." + remaining;
            }
        }
        
        if (remaining == null) {
            return field;
        }

        return resolve(remaining, field);
    }

    /**
     * Retrieve the value of the given field from the given object. Swallows up checked exceptions in favour of an
     * illegal argument exception.
     * @param field The field to retrieve
     * @param obj The object to examine
     * @return The object contained in the field
     */
    public static Object getField(final Field field, final Object obj) {
        final boolean isAccessible = field.isAccessible();
        try {
            field.setAccessible(true);
            return field.get(obj);
        } catch (IllegalAccessException | IllegalArgumentException ex) {
            throw new IllegalArgumentException("Could not get property " + field.getName() + " for " + obj.getClass(), ex);
        } finally {
            field.setAccessible(isAccessible);
        }
    }

    /**
     * Retrieve the value of the given field from the given object. Swallows up checked exceptions in favour of an
     * illegal argument exception.
     * @param field The field to retrieve
     * @param obj The object to examine
     * @return The object contained in the field
     */
    public static Object getField(final String field, final Object obj) {
        // Deal with special cases of collections and maps
        if (obj instanceof List) {
            if (isInteger(field)) {
                return ((List) obj).get(Integer.parseInt(field));
            }
            throw new IllegalArgumentException("Index must be numeric");
        }
        if (obj instanceof Collection) {
            if (isInteger(field)) {
                return new ArrayList<>((Collection) obj).get(Integer.parseInt(field));
            }
            throw new IllegalArgumentException("Index must be numeric");
        }
        if (obj instanceof Map) {
            if (isInteger(field)) {
                return new ArrayList<>(((Map) obj).values()).get(Integer.parseInt(field));
            }
            return ((Map) obj).get(field);
        }
        
        try {
            return getField(obj.getClass().getDeclaredField(field), obj);
        } catch (NoSuchFieldException ex) {
            throw new IllegalArgumentException("Field " + field + " does not exist on object of type " + obj.getClass(), ex);
        } catch (SecurityException ex) {
            throw new IllegalArgumentException("Unable to access field " + field + " on object of type " + obj.getClass(), ex);
        }
    }

    public static boolean isInteger(final String str) {
        try {
            Integer.parseInt(str);
        } catch (NullPointerException | NumberFormatException ex) {
            return false;
        }
        return true;
    }

    /**
     * Recursively search for the given annotation type upon the given element. Searches all annotations to see if they
     * themselves are also annotated.
     * @param <A> The type of annotation to seek and return
     * @param element The element to search
     * @param type The annotation type to search for
     * @return The first annotation found
     */
    public static <A extends Annotation> A getAnnotation(final AnnotatedElement element, final Class<A> type) {
        return getAnnotation(element, type, new HashSet<>());
    }
    private static <A extends Annotation> A getAnnotation(final AnnotatedElement element, final Class<A> type, final Set cache) {
        if (cache.contains(element)) {
            return null;
        }
        cache.add(element);

        final Annotation[] annotations = element.getAnnotations();
        for (Annotation annotation : annotations) {
            if (type.isAssignableFrom(annotation.annotationType())) {
                return (A) annotation;
            }

            final A result = getAnnotation(annotation.annotationType(), type, cache);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
}
