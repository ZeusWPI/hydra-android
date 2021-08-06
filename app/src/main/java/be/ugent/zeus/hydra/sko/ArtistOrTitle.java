/*
 * Copyright (c) 2021 The Hydra authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package be.ugent.zeus.hydra.sko;

import androidx.annotation.NonNull;

import java.util.Objects;

/**
 * Contains an artist or a title, but not both and not neither.
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
     * @throws NullPointerException If the artist is {@code null}.
     */
    @NonNull
    public Artist getArtist() throws NullPointerException {
        //noinspection ConstantConditions - because we use the backport
        return Objects.requireNonNull(artist);
    }

    /**
     * @return The title.
     * @throws NullPointerException If the title is {@code null}.
     */
    @NonNull
    public String getTitle() throws NullPointerException {
        //noinspection ConstantConditions - because we use the backport
        return Objects.requireNonNull(title);
    }
}
