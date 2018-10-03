package tech.coinbub.daemon.testutils.matchers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

// From https://github.com/sandromancuso/bean-property-matcher/blob/master/src/main/java/org/craftedsw/beanpropertymatcher/matcher/BeanMatcher.java
public class BeanMatcher<T> extends BaseMatcher<T> {

    private final boolean ignoreNullFields;
    private final boolean testAllFields;
    private final List<BeanPropertyMatcher<?>> propertyMatchers;
    private final Description expectedDescription = new StringDescription();
    private final Description mismatchDescription = new StringDescription();

    @Factory
    public static <T> BeanMatcher<T> has(final BeanPropertyMatcher<?>... propertyMatchers) {
        return new BeanMatcher<>(false, false, propertyMatchers);
    }

    @Factory
    public static <T> BeanMatcher<T> hasAll(final BeanPropertyMatcher<?>... propertyMatchers) {
        return new BeanMatcher<>(false, true, propertyMatchers);
    }

    @Factory
    public static <T> BeanMatcher<T> hasAllIgnoringNull(final BeanPropertyMatcher<?>... propertyMatchers) {
        return new BeanMatcher<>(true, true, propertyMatchers);
    }

    protected BeanMatcher(final boolean ignoreNullFields,
            final boolean testAllFields,
            final BeanPropertyMatcher<?>... propertyMatchers) {
        this.ignoreNullFields = ignoreNullFields;
        this.testAllFields = testAllFields;
        this.propertyMatchers = Arrays.asList(propertyMatchers);
    }

    @Override
    public boolean matches(final Object actual) {
        final List<BeanPropertyMatcher<?>> matchers = new ArrayList<>(propertyMatchers);
        boolean matches = true;

        // Verify that actual has _all_ the fields expected
        for (BeanPropertyMatcher matcher : propertyMatchers) {
            final String fieldName = matcher.getPropertyName();
            try {
                actual.getClass().getDeclaredField(fieldName);
            } catch (NoSuchFieldException ex) {
                matches = false;
                appendSeparators();
                matchers.remove(matcher);
                expectedDescription.appendDescriptionOf(matcher);
                mismatchDescription.appendText("property \"" + fieldName + "\" did not exist");
            } catch (SecurityException ex) {
                throw new RuntimeException(ex);
            }
        }

        if (testAllFields) {
            // Now verify that all the known fields on the actual have been tested for
            final Set<String> knownFields = propertyMatchers.stream()
                    .map((m) -> (m.getPropertyName()))
                    .collect(Collectors.toSet());
            for (Field field : actual.getClass().getDeclaredFields()) {
                if (!knownFields.contains(field.getName()) && !field.isSynthetic()) {
                    Object value;
                    try {
                        value = BeanPropertyMatcher.getFieldValue(actual, field);
                    } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException ex) {
                        value = "<error>";
                    }
                    // Allow nulls
                    if (value == null && ignoreNullFields) {
                        continue;
                    }

                    matches = false;
                    appendSeparators();
                    expectedDescription.appendText("to not have property \"" + field.getName() + "\"");
                    mismatchDescription.appendText("property \"" + field.getName() + "\" was ").appendValue(value);
                }
            }
        }

        for (BeanPropertyMatcher<?> matcher : matchers) {
            if (!matcher.matches(actual)) {
                matches = false;
                appendDescriptions(actual, matcher);
            }
        }
        return matches;
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText(expectedDescription.toString());
    }

    @Override
    public void describeMismatch(final Object item, final Description description) {
        description.appendText(mismatchDescription.toString());
    }

    private void appendDescriptions(final Object item, final Matcher<?> matcher) {
        matcher.describeTo(expectedDescription);
        matcher.describeMismatch(item, mismatchDescription);
    }

    private void appendSeparators() {
        if (expectedDescription.toString().length() > 0) {
            expectedDescription.appendText(",\n        ");
        }
        if (mismatchDescription.toString().length() > 0) {
            mismatchDescription.appendText(",\n        ");
        }
    }

}
