/*
 * Copyright (c) 2022 Niko Strijbol
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

package be.ugent.zeus.hydra.wpi.tap.user;

import androidx.annotation.Nullable;

import com.squareup.moshi.Json;

import static be.ugent.zeus.hydra.wpi.tap.TapUtils.createImageUrl;

/**
 * Represents the Tap user.
 *
 * @author Niko Strijbol
 */
public record TapUser(
        int id,
        @Json(name = "avatar_file_name")
        String avatarFileName,
        @Json(name = "orders_count")
        int orderCount,
        String name,
        @Json(name = "dagschotel_id")
        Integer favourite) {

    private static final String IMAGE_URL = "system/users/avatars/%s/%s/%s/medium/%s";

    @Nullable
    public String profileImageUrl() {
        return createImageUrl(id, IMAGE_URL, avatarFileName());
    }
}
