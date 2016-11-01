/*
 * Copyright 2016 Allan Pichardo
 * Copyright 2016 Niko Strijbol
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.ugent.zeus.hydra.urgent.track;

import android.support.annotation.Nullable;

import java.io.IOException;

/**
 * A track to play.
 *
 * @author Allan Pichardo
 * @author Niko Strijbol
 */
public interface Track {

    /**
     * @return The id of this track.
     */
    int getId();

    /**
     * @return The name of the artist.
     */
    String getArtist();

    /**
     * @return The title of the track.
     */
    String getTitle();

    /**
     * @return The URL of the track.
     */
    void getUrl(UrlConsumer consumer);

    /**
     * @return The URL of the album track. If null, the defaults will be used.
     */
    String getArtworkUrl();

    interface UrlConsumer {
        void receive(@Nullable String url) throws IOException;
    }
}
