package be.ugent.zeus.hydra.testing;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

import be.ugent.zeus.hydra.common.MockParcel;
import be.ugent.zeus.hydra.testing.matcher.ShallowButFullEqual;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.ComparisonFailure;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZonedDateTime;

import static org.junit.Assert.assertEquals;

/**
 * Custom assert rules for unit tests.
 *
 * @author Niko Strijbol
 */
@SuppressLint("NewApi")
public class Assert {

    /**
     * Assert that a class implements {@link Parcelable} correctly. This will check every field of the class.
     *
     * @param clazz The class of the object to test.
     * @param <T> The type of the object to test.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Parcelable> void assertParcelable(Class<T> clazz) {
        Objects.requireNonNull(clazz);
        T original = Utils.generate(clazz);
        Parcel parcel = MockParcel.writeToParcelable(original);
        try {
            Parcelable.Creator<T> creator = (Parcelable.Creator<T>) FieldUtils.readDeclaredStaticField(clazz, "CREATOR");
            T other = creator.createFromParcel(parcel);
            assertThat(other, samePropertyValuesAs(original));
        } catch (IllegalAccessException e) {
            throw new AssertionError("Class does not have a CREATOR field.", e);
        } catch (ClassCastException e) {
            throw new AssertionError("Class does not have a correct CREATOR field, or it is used wrong.", e);
        }
    }

    /**
     * Matcher that take special care of {@link ZonedDateTime}s.
     */
    public static <T> Matcher<T> samePropertyValuesAs(T instance) {
        return ShallowButFullEqual.sameFieldsAs(instance)
                .withMatcher(ZonedDateTime.class, o -> new TypeSafeDiagnosingMatcher<ZonedDateTime>() {
                    @Override
                    protected boolean matchesSafely(ZonedDateTime item, Description mismatchDescription) {
                        if (!o.isEqual(item)) {
                            mismatchDescription.appendText(" was ").appendValue(item);
                            return false;
                        }
                        return true;
                    }

                    @Override
                    public void describeTo(Description description) {
                        description.appendValue(instance);
                    }
                })
                .withMatcher(OffsetDateTime.class, o -> new TypeSafeDiagnosingMatcher<OffsetDateTime>() {
                    @Override
                    protected boolean matchesSafely(OffsetDateTime item, Description mismatchDescription) {
                        if (!o.isEqual(item)) {
                            mismatchDescription.appendText(" was ").appendValue(item);
                            return false;
                        }
                        return true;
                    }

                    @Override
                    public void describeTo(Description description) {
                        description.appendValue(instance);
                    }
                });
    }

    public static <T> void assertThat(T expected, Matcher<? extends T> matcher) {
        if (!matcher.matches(expected)) {
            Description actual = new StringDescription();
            matcher.describeMismatch(expected, actual);
            Description expectedDescription = new StringDescription();
            matcher.describeTo(expectedDescription);
            throw new ComparisonFailure("", expectedDescription.toString(), actual.toString());
        }
    }

    /**
     * Assert that two collections contain the same elements. This does not account for ordering in the collections.
     *
     * @param expected The expected collection.
     * @param actual The actual collection.
     */
    public static <T> void assertCollectionEquals(Iterable<T> expected, Iterable<T> actual) {
        Collection<Object> actualSet = new HashSet<>();
        Collection<Object> notEqual = new HashSet<>();
        CollectionUtils.addAll(actualSet, actual.iterator());
        CollectionUtils.addAll(notEqual, expected.iterator());
        assertEquals(notEqual, actualSet);
    }
}