package be.ugent.zeus.hydra.data.network.requests.resto;

import android.os.Build;
import android.support.annotation.RequiresApi;

import be.ugent.zeus.hydra.data.models.resto.RestoMenu;
import be.ugent.zeus.hydra.repository.requests.RequestException;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static be.ugent.zeus.hydra.testing.Utils.generate;
import static org.junit.Assert.assertEquals;

/**
 * @author Niko Strijbol
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class CurrentMenuFilterTest {

    @Test
    public void testOne() throws RequestException {
        RestoMenu expected = generate(RestoMenu.class);
        CurrentMenuFilter filter = new CurrentMenuFilter();
        RestoMenu result = filter.apply(Collections.singletonList(expected));
        assertEquals(expected, result);
    }

    @Test
    public void testMultiple() throws RequestException {
        List<RestoMenu> expected = generate(RestoMenu.class, 10).collect(Collectors.toList());
        CurrentMenuFilter filter = new CurrentMenuFilter();
        RestoMenu result = filter.apply(expected);
        assertEquals(expected.get(0), result);
    }

    @Test(expected = RequestException.class)
    public void testNone() throws RequestException {
        CurrentMenuFilter filter = new CurrentMenuFilter();
        filter.apply(Collections.emptyList());
    }
}