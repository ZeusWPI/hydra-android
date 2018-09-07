package be.ugent.zeus.hydra.sko.lineup;


import android.support.annotation.NonNull;

import java9.util.Objects;

/**
 * Contains an Artist or a Title, but not both, nor neither.
 *
 * @author Niko Strijbol
 */
class ArtistOrTitle {

    private final Artist artist;
    private final String title;

    ArtistOrTitle(@NonNull Artist artist) {
        this.artist = artist;
        this.title = null;
    }

    ArtistOrTitle(@NonNull String title) {
        this.artist = null;
        this.title = title;
    }

    public boolean isArtist() {
        return artist != null;
    }

    public boolean isTitle() {
        return title != null;
    }

    /**
     * @return The artist.
     *
     * @throws NullPointerException If the artist is {@code null}.
     */
    @NonNull
    public Artist getArtist() throws NullPointerException {
        //noinspection ConstantConditions - because we use the backport
        return Objects.requireNonNull(artist);
    }

    /**
     * @return The title.
     *
     * @throws NullPointerException If the title is {@code null}.
     */
    @NonNull
    public String getTitle() throws NullPointerException {
        //noinspection ConstantConditions - because we use the backport
        return Objects.requireNonNull(title);
    }
}
