package be.ugent.zeus.hydra.news;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @author Niko Strijbol
 */
public final class NewsStream {

    private String generator;
    private String id;
    private String language;
    private String link;
    private String logo;
    private String title;
    private OffsetDateTime updated;

    private List<NewsArticle> entries;

    @SuppressWarnings("unused")
    public NewsStream() {
        // Moshi constructor
    }

    public List<NewsArticle> getEntries() {
        return entries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewsStream that = (NewsStream) o;
        return Objects.equals(generator, that.generator)
                && id.equals(that.id)
                && Objects.equals(language, that.language)
                && Objects.equals(link, that.link)
                && Objects.equals(logo, that.logo)
                && Objects.equals(title, that.title)
                && updated.equals(that.updated)
                && entries.equals(that.entries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(generator, id, language, link, logo, title, updated, entries);
    }
}
