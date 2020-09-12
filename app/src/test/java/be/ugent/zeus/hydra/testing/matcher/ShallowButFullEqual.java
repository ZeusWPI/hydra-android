package be.ugent.zeus.hydra.testing.matcher;

import java.lang.reflect.Field;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.hamcrest.core.IsEqual;

/**
 * An improved version of SamePropertyValues from Hamcrest, but with support for custom matchers for custom types.
 * <p>
 * The intended use case is matching {@link ZonedDateTime}, which don't use the "equals" method to see
 * if they are logically the same object (they use {@link ZonedDateTime#isEqual(ChronoZonedDateTime)}).
 *
 * @author Niko Strijbol
 */
public class ShallowButFullEqual<T> extends TypeSafeDiagnosingMatcher<T> {

    private final T expectedBean;
    private final Fields<T> fields;
    private final Map<Class<?>, Function<Object, Matcher<Object>>> matcherMap;

    private ShallowButFullEqual(T expectedBean) {
        //noinspection unchecked
        this.fields = new Fields<>((Class<T>) expectedBean.getClass());
        this.matcherMap = new HashMap<>();
        this.expectedBean = expectedBean;
    }

    /**
     * Creates a matcher that matches when the examined object has values for all of
     * its JavaBean properties that are equal to the corresponding values of the
     * specified bean.
     * <p>
     * For example:
     * <pre>assertThat(myBean, sameFieldsAs(myExpectedBean))</pre>
     *
     * @param expectedBean the bean against which examined beans are compared
     */
    public static <T> ShallowButFullEqual<T> sameFieldsAs(T expectedBean) {
        return new ShallowButFullEqual<>(expectedBean);
    }

    @Override
    public boolean matchesSafely(T bean, Description mismatch) {
        return isCompatibleType(bean, mismatch)
                && hasMatchingValues(bean, mismatch);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(ToStringBuilder.reflectionToString(expectedBean, ToStringStyle.MULTI_LINE_STYLE));
    }

    private boolean isCompatibleType(T item, Description mismatchDescription) {

        if (!expectedBean.getClass().equals(item.getClass())) {
            mismatchDescription.appendText("is incompatible type: " + item.getClass().getSimpleName());
            return false;
        }

        return true;
    }

    private boolean hasMatchingValues(T item, Description mismatchDescription) {

        List<Field> fields = this.fields.getFields();

        try {
            for (Field field : fields) {

                Object expected = FieldUtils.readField(field, expectedBean, true);
                Object actual = FieldUtils.readField(field, item, true);

                if (expected == null || actual == null) {
                    if (expected != null || actual != null) {
                        mismatchDescription.appendText(ToStringBuilder.reflectionToString(item, ToStringStyle.MULTI_LINE_STYLE));
                        return false;
                    }
                } else {
                    Matcher<?> matcher = matcherMap.getOrDefault(expected.getClass(), IsEqual::new).apply(expected);
                    if (!matcher.matches(actual)) {
                        mismatchDescription.appendText(ToStringBuilder.reflectionToString(item, ToStringStyle.MULTI_LINE_STYLE));
                        return false;
                    }
                }
            }

            return true;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            mismatchDescription.appendText(" error occurred while accessing field.");
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public <V> ShallowButFullEqual<T> withMatcher(Class<V> clazz, Function<V, Matcher<V>> matcher) {
        matcherMap.put(clazz, (Function<Object, Matcher<Object>>) (Function<?, ?>) matcher);
        return this;
    }

    @SuppressWarnings("unused")
    public ShallowButFullEqual<T> ignoring(String... fields) {
        this.fields.ignoreFields(fields);
        return this;
    }
}
