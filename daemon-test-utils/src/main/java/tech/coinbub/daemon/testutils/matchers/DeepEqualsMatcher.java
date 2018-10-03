package tech.coinbub.daemon.testutils.matchers;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.DiagnosingMatcher;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import static org.hamcrest.core.IsEqual.equalTo;
import tech.coinbub.daemon.testutils.Util;

public class DeepEqualsMatcher<T> extends BaseMatcher<T> {
    private final T expected;
//    private final Set<String> fieldNames;
//    private final List<Matcher> matchers;
    private final String path;
    private final Description expectedDescription = new StringDescription();
    private final Description mismatchDescription = new StringDescription();

    public DeepEqualsMatcher(T expected) {
        this(expected, null);
    }
    protected DeepEqualsMatcher(final T expected,
            final String path) {
        if (expected == null) {
            throw new IllegalArgumentException(expected + " cannot be null");
        }

//        final List<Field> fields = getFields(expected.getClass());
        this.expected = expected;
//        this.fieldNames = fieldNames(fields);
//        this.matchers = propertyMatchersFor(expected, fields);
        this.path = path;
    }

    //
    // Matcher implementation
    //
    @Override
    public boolean matches(final Object actual) {
        final Object compareTo = Util.resolve(path, actual);
        System.out.println("========================");
        System.out.println("Expected: " + expected);
        System.out.println("Actual: " + compareTo);
        System.out.println("========================");
        
        // Ensure compatible types
        if (compareTo == null) {
            appendPath();
            expectedDescription.appendText("to not be null");
            mismatchDescription.appendText("is null");
            return false;
        }
        if (!expected.getClass().isAssignableFrom(compareTo.getClass())) {
            appendPath();
            expectedDescription.appendText("to be a " + expected.getClass().getCanonicalName());
            mismatchDescription.appendText("was " + compareTo.getClass().getSimpleName());
            return false;
        }

        if (compareTo instanceof Collection) {
            boolean result = true;
            int i = 0;
            for (Object o : (Collection) compareTo) {
                final String newPath = getPath(i + "");
                final Object newExpected = Util.resolve(newPath, expected);
                final Matcher matcher;
                if (isDirectlyComparable(o.getClass())
                        || (o.getClass().isArray() && isDirectlyComparable(o.getClass().getComponentType()))) {
                    matcher = new PropertyMatcher(newExpected, newPath);
                } else {
                    matcher = new DeepEqualsMatcher(newExpected, newPath);
                }
                if (!matcher.matches(Util.resolve(newPath, compareTo))) {
                    appendDescriptions(Util.resolve(getPath(path), compareTo), matcher);
                    result = false;
                }
            }
            return result;
        }
        if (compareTo instanceof Map) {
            boolean result = true;
            int i = 0;
            for (Object o : (Collection) compareTo) {
                final String newPath = getPath(i + "");
                final Object newExpected = Util.resolve(newPath, expected);
                final Matcher matcher;
                if (isDirectlyComparable(o.getClass())
                        || (o.getClass().isArray() && isDirectlyComparable(o.getClass().getComponentType()))) {
                    matcher = new PropertyMatcher(newExpected, newPath);
                } else {
                    matcher = new DeepEqualsMatcher(newExpected, newPath);
                }
                if (!matcher.matches(Util.resolve(newPath, compareTo))) {
                    appendDescriptions(Util.resolve(getPath(path), compareTo), matcher);
                    result = false;
                }
            }
            return result;
        }

        // Ensure neither side has extra properties
        final Set<String> extraProperties = diffProperties(compareTo, expected);
        if (!extraProperties.isEmpty()) {
            mismatchDescription.appendText("has extra properties called " + extraProperties);
            return false;
        }
        final Set<String> missingProperties = diffProperties(expected, compareTo);
        if (!missingProperties.isEmpty()) {
            mismatchDescription.appendText("is missing properties called " + missingProperties);
        }

        // All values match
        final List<Matcher> matchers = matchersFor(expected);
        boolean result = true;
        for (Matcher matcher : matchers) {
            if (!matcher.matches(Util.resolve(getPath(path), compareTo))) {
                appendDescriptions(Util.resolve(getPath(path), compareTo), matcher);
                result = false;
            }
        }
        return result;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(expectedDescription.toString());
    }

    @Override
    public void describeMismatch(Object item, Description description) {
        description.appendText(mismatchDescription.toString());
    }

    //
    // Helpers
    //
    /**
     * 
     * @param base
     * @param against
     * @return 
     */
    private Set<String> diffProperties(final Object base, final Object against) {
        final Set<String> againstFieldNames = fieldNames(getFields(against.getClass()));
        againstFieldNames.removeAll(fieldNames(getFields(base.getClass())));
        return againstFieldNames;
    }

    /**
     * 
     * @param <T>
     * @param expected
     * @param fields
     * @return 
     */
    private <T> List<Matcher> matchersFor(final Object expected) {
        final List<Field> fields = getFields(expected.getClass());
        final List<Matcher> result = new ArrayList<>(fields.size());
        for (Field field : fields) {
            final Class<?> cls = field.getType();
            final String newPath = getPath(field.getName());
            final Object newExpected = Util.resolve(newPath, expected);
            System.out.println(">>>>>>>>>>>>>>>");
            System.out.println("New Path: " + newPath);
            System.out.println("New Expected: " + newExpected);
            System.out.println(">>>>>>>>>>>>>>>");
            
            if (isDirectlyComparable(field.getType())
                    || (cls.isArray() && isDirectlyComparable(cls.getComponentType()))) {
                result.add(new PropertyMatcher(newExpected, newPath));
            } else {
                result.add(new DeepEqualsMatcher(newExpected, newPath));
            }
        }
        return result;
    }

    /**
     * 
     * @param item
     * @param matcher 
     */
    private void appendDescriptions(Object item, Matcher<?> matcher) {
        matcher.describeTo(expectedDescription);
        matcher.describeMismatch(item, mismatchDescription);
        expectedDescription.appendText(" \n");
        mismatchDescription.appendText(" \n");
    }

    /**
     * 
     */
    private void appendPath() {
        if (path == null) {
            return;
        }
        expectedDescription.appendText(path + " ");
        mismatchDescription.appendText(path + " ");
    }

    /**
     * 
     * @param field
     * @return 
     */
    private String getPath(final String field) {
        if (path == null || path.isEmpty()) {
            return field;
        }
        return path + "." + field;
    }
    
    //
    // Reflection stuffs
    //
    private static List<Field> getFields(final Class<?> cls) {
        final List<Field> fields = new ArrayList<>(Arrays.asList(cls.getDeclaredFields()));
        if (cls.getSuperclass() != Object.class) {
            fields.addAll(getFields(cls.getSuperclass()));
        }
        return fields.stream()
                .filter((f) -> (!f.isSynthetic()))
                .collect(Collectors.toList());
    }

    private static Set<String> fieldNames(final List<Field> fields) {
        Set<String> result = new HashSet<>();
        for (Field field : fields) {
            result.add(field.getName());
        }
        return result;
    }

    private static boolean isDirectlyComparable(final Class<?> cls) {
        return cls.isPrimitive()
                || cls == Short.class
                || cls == Integer.class
                || cls == Long.class
                || cls == Boolean.class
                || BigDecimal.class.isAssignableFrom(cls)
                || BigInteger.class.isAssignableFrom(cls)
                || CharSequence.class.isAssignableFrom(cls);
    }

    public static class PropertyMatcher extends DiagnosingMatcher<Object> {
        private final String field;
        private final Matcher<Object> matcher;

        public PropertyMatcher(final Object expected, final String field) {
            this.field = field;
            this.matcher = equalTo(expected);
        }

        @Override
        public boolean matches(final Object actual, final Description mismatch) {
            final Object actualValue = Util.resolve(field, actual);
            if (!matcher.matches(actualValue)) {
                mismatch.appendText(field + " ");
                matcher.describeMismatch(actualValue, mismatch);
                return false;
            }
            return true;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText(field + ": ").appendDescriptionOf(matcher);
        }
    }

    @Factory
    public static <T> Matcher<T> deepEquals(T expectedBean) {
        return new DeepEqualsMatcher<>(expectedBean);
    }

}
