package tech.coinbub.daemon.testutils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import org.junit.jupiter.api.Test;


public class UtilTest {
    @Test
    public void resolveWorksWithSimpleObjects() {
        final TestClass underTest = new TestClass();
        underTest.obj = "Test";

        assertThat(Util.resolve("obj", underTest), is(equalTo("Test")));
    }
    @Test
    public void resolveWorksWithMaps() {
        final Map<String, String> underTest = new LinkedHashMap<>();
        underTest.put("test1", "Test1");
        underTest.put("test2", "Test2");

        assertThat(Util.resolve("test1", underTest), is(equalTo("Test1")));
        assertThat(Util.resolve("test2", underTest), is(equalTo("Test2")));
        assertThat(Util.resolve("0", underTest), is(equalTo("Test1")));
        assertThat(Util.resolve("1", underTest), is(equalTo("Test2")));
    }
    @Test
    public void resolveWorksWithLists() {
        final List<String> underTest = new ArrayList<>();
        underTest.add("Test1");
        underTest.add("Test2");

        assertThat(Util.resolve("0", underTest), is(equalTo("Test1")));
        assertThat(Util.resolve("1", underTest), is(equalTo("Test2")));
    }
    @Test
    public void resolveWorksWithCollections() {
        final Collection<String> underTest = new LinkedHashSet<>();
        underTest.add("Test1");
        underTest.add("Test2");

        assertThat(Util.resolve("0", underTest), is(equalTo("Test1")));
        assertThat(Util.resolve("1", underTest), is(equalTo("Test2")));
    }
    @Test
    public void resolveWorksWithNestedObjects() {
        final TestClass underTest = new TestClass();
        underTest.obj = new TestClass();
        ((TestClass) underTest.obj).obj = "Test";

        assertThat(Util.resolve("obj.obj", underTest), is(equalTo("Test")));
    }
    @Test
    public void resolveWorksWithNestedLists() {
        final TestClass inner = new TestClass();
        inner.obj = "Test2";

        final TestClass underTest = new TestClass();
        underTest.obj = new ArrayList<>();
        ((List) underTest.obj).add("Test1");
        ((List) underTest.obj).add(inner);

        assertThat(Util.resolve("obj[1].obj", underTest), is(equalTo("Test2")));
    }
    @Test
    public void getAnnotationFindsNestedAnnotation() {
        final Tested result = Util.getAnnotation(TestClass.class, Tested.class);
        assertThat(result, is(notNullValue()));
    }

    @TestSub
    public static class TestClass {
        public Test a;
        public Test b;
        public Object obj;
    }
    
    @Target({ ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Tested {}

    @Target({ ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Tested
    public @interface TestSub {}
}
