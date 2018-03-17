package be.ugent.zeus.hydra.common.network;

import android.os.Build;
import android.support.annotation.RequiresApi;

import org.junit.Assert;

/**
 * @author Niko Strijbol
 */
@RequiresApi(api = Build.VERSION_CODES.KITKAT)
@Deprecated
public abstract class ArrayJsonSpringRequestTest<R> extends AbstractJsonSpringRequestTest<R[]> {

    public ArrayJsonSpringRequestTest(Class<R[]> clazz) {
        super(clazz);
    }

    @Override
    protected void assertEquals(R[] expected, R[] actual) {
        Assert.assertArrayEquals(expected, actual);
    }
}
