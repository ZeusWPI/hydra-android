package be.ugent.zeus.hydra.data.network;

import org.junit.Assert;

/**
 * @author Niko Strijbol
 */
public abstract class ArrayJsonSpringRequestTest<R> extends JsonSpringRequestTest<R[]> {

    public ArrayJsonSpringRequestTest(Class<R[]> clazz) {
        super(clazz);
    }

    @Override
    protected void assertEquals(R[] expected, R[] actual) {
        Assert.assertArrayEquals(expected, actual);
    }
}
