package be.ugent.zeus.hydra.caching;

import java8.util.Objects;

import java.io.Serializable;

/**
 * @author Niko Strijbol
 */
public class TestObject implements Serializable {

    static final String TEST_FILE_KEY = "testFile";

    private String data = "TestObjectData";

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestObject that = (TestObject) o;
        return Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }
}