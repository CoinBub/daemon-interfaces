package tech.coinbub.daemon.testutils.matchers;

import static org.hamcrest.core.IsNot.not;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Factory;
import org.hamcrest.BaseMatcher;

/**
 * IS the field null or not defined on the object?
 */
public class IsNullOrUndefinedMatcher<T> extends BaseMatcher<T> {
    @Override
    public boolean matches(final Object actual) {
        return actual == null;
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("null");
    }

    @Factory
    public static Matcher<Object> nullOrUndefined() {
        return new IsNullOrUndefinedMatcher<>();
    }

    @Factory
    public static Matcher<Object> notNullOrUndefined() {
        return not(nullOrUndefined());
    }
}

