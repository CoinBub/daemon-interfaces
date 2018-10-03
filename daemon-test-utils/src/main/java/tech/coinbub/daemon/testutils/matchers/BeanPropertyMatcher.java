package tech.coinbub.daemon.testutils.matchers;

import java.lang.reflect.Field;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class BeanPropertyMatcher<T> extends TypeSafeDiagnosingMatcher<T> {

    private final String matchingPropertyName;
    private final Matcher<?> valueMatcher;

    @Factory
    public static <T> BeanPropertyMatcher<T> property(final String propertyName, final Matcher<?> value) {
        return new BeanPropertyMatcher<>(propertyName, value);
    }

    public BeanPropertyMatcher(final String propertyName, final Matcher<?> valueMatcher) {
        this.matchingPropertyName = propertyName;
        this.valueMatcher = valueMatcher;
    }

    @Override
    public boolean matchesSafely(final T actual, final Description mismatchDescription) {
        try {
            final Object propertyValue = getFieldValue(actual, matchingPropertyName);
            boolean valueMatches = valueMatcher.matches(propertyValue);
            if (!valueMatches) {
                appendSeparator(mismatchDescription);
                mismatchDescription.appendText("property \"" + matchingPropertyName + "\" ");
                valueMatcher.describeMismatch(propertyValue, mismatchDescription);
            }
            return valueMatches;
        } catch (Exception ex) {
            return false;
        }
    }

    public String getPropertyName() {
        return matchingPropertyName;
    }

    private void appendSeparator(final Description description) {
        if (description.toString().length() > 0) {
            description.appendText(",\n ");
        }
    }

    @Override
    public void describeTo(final Description description) {
        appendSeparator(description);
        description.appendText("property \"" + matchingPropertyName + "\" ").appendDescriptionOf(valueMatcher);
    }

    //
    // Helpers
    //
    public static Object getFieldValue(final Object actual, final String name)
            throws NoSuchFieldException,
            IllegalArgumentException,
            IllegalAccessException {
        return getFieldValue(actual, actual.getClass().getDeclaredField(name));
    }

    public static Object getFieldValue(final Object actual, final Field field)
            throws NoSuchFieldException,
            IllegalArgumentException,
            IllegalAccessException {
        final boolean isAccessible = field.isAccessible();
        field.setAccessible(true);
        try {
            return field.get(actual);
        } finally {
            field.setAccessible(isAccessible);
        }
    }

}
