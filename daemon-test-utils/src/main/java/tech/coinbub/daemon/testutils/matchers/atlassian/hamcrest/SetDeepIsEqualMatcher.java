package tech.coinbub.daemon.testutils.matchers.atlassian.hamcrest;

import com.google.common.base.Function;
import com.google.common.collect.Sets;
import org.hamcrest.Description;
import org.hamcrest.DiagnosingMatcher;
import org.hamcrest.Matcher;

import java.util.Set;

import static tech.coinbub.daemon.testutils.matchers.atlassian.hamcrest.Functions.cache;
import static com.google.common.collect.Iterables.size;
import static com.google.common.collect.Iterables.transform;

/**
 * {@link org.hamcrest.Matcher} for {@code Set}s.  It is composed of {@code Matcher}s for each element in the expected set.  The element
 * matchers are created from the {@code MatcherFactory} provided when the SetDeepIsEqualMatcher is created.
 * The element matchers are also lazily created and cached for future use.
 *
 * As you would expect, the order of the given elements does not need to match the order of the matchers that match them.
 *
 * Ideally, this would verify that there is a 'perfect matching' - each element is matched by a different matcher.  This turns
 * out to be somewhat complicated (though I think it could be in cubic time by the Hungarian Algorithm --
 * http://en.wikipedia.org/wiki/Hungarian_algorithm).  So, to make my life simpler, this matcher verifies just that:
 * 1. all elements are matched by at least one matcher
 * 2. all matchers match at least one element.
 *
 *
 *
 * @param <S> represents the type of things this matcher matches (ie, sets).  Needed to make the compiler happy.
 */
//TODO: there is probably too much copy/paste between this and ListDeepIsEqualMatcher and ArayDeepIsEqualMatcher
class SetDeepIsEqualMatcher<S> extends DiagnosingMatcher<S>
{
    private final int expectedSize;
    private final Iterable<Matcher<?>> matchers;

    public SetDeepIsEqualMatcher(Iterable<?> expected, MatcherFactory baseMatcherFactory, DisjointSet<Object> equiv)
    {
        this.expectedSize = size(expected);
        this.matchers = transform(expected, cache(toMatchers(baseMatcherFactory, equiv)));
    }

    private Function<Object, Matcher<?>> toMatchers(final MatcherFactory matcherFactory, final DisjointSet<Object> equiv)
    {
        return new Function<Object, Matcher<?>>()
        {
            public Matcher<?> apply(Object element)
            {
                return matcherFactory.newEqualMatcher(element, matcherFactory, equiv);
            }
        };
    }

    @Override
    protected boolean matches(Object actual, Description mismatchDescription)
    {
        if (actual == null)
        {
            mismatchDescription.appendText("is null");
            return false;
        }


        if (! (actual instanceof Set<?>))
        {
            mismatchDescription.appendText("not a Set, but a ")
                    .appendText(actual.getClass().getName());
            return false;
        }

        @SuppressWarnings("unchecked") //just checked this above
                Set<?> actualAsSet = (Set<?>) actual;
        if (expectedSize != actualAsSet.size())
        {
            mismatchDescription.appendText("size should be ")
                    .appendText(String.valueOf(expectedSize))
                    .appendText(", but is ")
                    .appendValue(actualAsSet.size());
            return false;
        }

        Set<Matcher<?>> unsatisfiedMatchers = Sets.newHashSet();
        Set<?> unmatchingElements = Sets.newHashSet(actualAsSet);

        lookForMatches(actualAsSet, unsatisfiedMatchers, unmatchingElements);

        if (!unsatisfiedMatchers.isEmpty())
        {
            describeUnsatisfiedMatchers(mismatchDescription, unsatisfiedMatchers);
            if (!unmatchingElements.isEmpty())
            {
                mismatchDescription.appendText(", and it ");
            }

        }
        if (!unmatchingElements.isEmpty())
        {
            describeUnmatchedElements(mismatchDescription, unmatchingElements);
        }
        return unsatisfiedMatchers.isEmpty() && unmatchingElements.isEmpty();
    }

    private void lookForMatches(Set<?> actualAsSet, Set<Matcher<?>> unsatisfiedMatchers, Set<?> unmatchingElements) {
        for (Matcher<?> matcher : matchers)
        {
            boolean mismatchFound = true;
            for (Object element : actualAsSet)
            {
                if (matcher.matches(element))
                {
                    mismatchFound = false;
                    unmatchingElements.remove(element);
                }
            }
            if (mismatchFound)
            {
                unsatisfiedMatchers.add(matcher);
            }
        }
    }

    private void describeUnmatchedElements(Description mismatchDescription, Set<?> unmatchingElements) {
        mismatchDescription.appendText("contains these unmatched elements: ");
        mismatchDescription.appendValueList("[", ", ", "]", unmatchingElements);
    }

    private void describeUnsatisfiedMatchers(Description mismatchDescription, Set<Matcher<?>> unsatisfiedMatchers) {

        mismatchDescription.appendText("does not match these: ");
        mismatchDescription.appendList("[", ", ", "]", unsatisfiedMatchers);
    }

    public void describeTo(Description desc)
    {
        desc.appendText("[");
        int index = 0;
        for (Matcher<?> matcher : matchers)
        {
            if (index > 0)
            {
                desc.appendText(", ");
            }
            desc.appendDescriptionOf(matcher);
            index++;
        }
        desc.appendText("]");
    }
}