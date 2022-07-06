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

import java.util.Locale;
import java.util.Objects;

import be.ugent.zeus.hydra.common.network.Endpoints;

/**
 * Represents the Tap user.
 * 
 * @author Niko Strijbol
 */
public class TapUser {
    
    private static final String IMAGE_URL = "system/users/avatars/%s/%s/%s/medium/%s";
    
    private int id;
    @Json(name = "avatar_file_name")
    private String avatarFileName;
    @Json(name = "orders_count")
    private int orderCount;
    private String name;
    @Json(name = "dagschotel_id")
    private Integer favourite;
    
    public TapUser() {
        // Needed for Moshi
    }

    public int getId() {
        return id;
    }
    
    public String getAvatarFileName() {
        return avatarFileName;
    }
    
    public int getOrderCount() {
        return orderCount;
    }
    
    public String getName() {
        return name;
    }
    
    public String getProfileImageUrl() {
        if (this.avatarFileName == null) {
            return null;
        }
        String paddedID = String.format(Locale.ROOT, "%09d", id);
        String first = paddedID.substring(0, 3);
        String second = paddedID.substring(3, 6);
        String third = paddedID.substring(6, 9);

        return Endpoints.TAP + String.format(Locale.ROOT, IMAGE_URL, first, second, third, this.avatarFileName);
    }

    /**
     * @return The ID of the dagschotel product or null if there is no dagschotel.
     */
    @Nullable
    public Integer getFavourite() {
        return favourite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TapUser tapUser = (TapUser) o;
        return id == tapUser.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
