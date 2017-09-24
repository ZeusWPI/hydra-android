package be.ugent.zeus.hydra.data.network.requests.urgent;

import be.ugent.zeus.hydra.data.models.UrgentInfo;
import java8.util.Optional;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.threeten.bp.Instant;
import org.threeten.bp.temporal.ChronoUnit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static be.ugent.zeus.hydra.data.network.Endpoints.URGENT_BASE_URL;
import static be.ugent.zeus.hydra.data.network.Endpoints.URGENT_CONFIG_URL;

/**
 * Get the information from the website.
 *
 * @author Niko Strijbol
 */
public class ProgrammeRepository {

    private static final String SELECTOR = "#header-text > a:nth-child(3)";

    public Optional<UrgentInfo> getCurrent() {
        try {
            // Get the title
            String title = getTitle();
            // Get the expiration date
            Instant instant = Instant.now()
                    .plus(1, ChronoUnit.HOURS)
                    .truncatedTo(ChronoUnit.HOURS);

            return Optional.of(new UrgentInfo(title, instant, getUrl().toString()));

        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private String getTitle() throws IOException {
        Document doc = Jsoup.connect(URGENT_BASE_URL).get();
        Element element = doc.select(SELECTOR).first();
        return element.text();
    }

    private URL getUrl() throws IOException {
        InputStream is = null;
        try {
            URL url = new URL(URGENT_CONFIG_URL);
            is = url.openStream();
            return new URL(from(is).trim());
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private String from(InputStream inputStream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }

        // Use StandardCharsets.UTF_8 once possible
        return result.toString("UTF-8");
    }
}
