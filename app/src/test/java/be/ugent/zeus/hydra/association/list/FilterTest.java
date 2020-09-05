package be.ugent.zeus.hydra.association.list;

import android.net.Uri;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import be.ugent.zeus.hydra.common.network.Endpoints;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class FilterTest {
    
    @Test
    public void urlGeneration() {
        OffsetDateTime now = OffsetDateTime.now();
        Filter.Live filter = new Filter.Live();
        filter.setWhitelist(Arrays.asList("test", "hallo"));
        filter.setTerm("search");
        filter.setAfter(now);
        filter.setBefore(now);

        Uri.Builder builder = Uri.parse("http://example.com").buildUpon();
        filter.filter.appendParams(builder);
        Uri uri = builder.build();
        
        assertEquals("search", uri.getQueryParameter("search_string"));
        Set<String> expected = Stream.of("test", "hallo").collect(Collectors.toSet());
        Set<String> actual = new HashSet<>(uri.getQueryParameters("association[]"));
        assertEquals(expected, actual);
        assertEquals(now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME), uri.getQueryParameter("start_time"));
        assertEquals(now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME), uri.getQueryParameter("end_time"));
    }

}