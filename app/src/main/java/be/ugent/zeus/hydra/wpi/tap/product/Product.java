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

package be.ugent.zeus.hydra.wpi.tap.product;

import com.squareup.moshi.Json;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Objects;

import be.ugent.zeus.hydra.common.network.Endpoints;

/**
 * Product from Tab
 *
 * @author Niko Strijbol
 */
public class Product {

    private static final String IMAGE_URL = "system/products/avatars/%s/%s/%s/medium/%s";

    private int id;
    private String name;
    @Json(name = "price_cents")
    private int price;
    @Json(name = "avatar_file_name")
    private String avatarFileName;
    private String category;
    private int stock;
    private Integer calories;

    public Product() {
        // Moshi
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getAvatarFileName() {
        return avatarFileName;
    }

    public String getCategory() {
        return category;
    }

    public int getStock() {
        return stock;
    }

    public Integer getCalories() {
        return calories;
    }

    public BigDecimal getPriceDecimal() {
        return new BigDecimal(getPrice()).movePointLeft(2);
    }

    public String getImageUrl() {
        if (this.avatarFileName == null) {
            return null;
        }
        String paddedID = String.format(Locale.ROOT, "%09d", id);
        String first = paddedID.substring(0, 3);
        String second = paddedID.substring(3, 6);
        String third = paddedID.substring(6, 9);

        return Endpoints.TAP + String.format(Locale.ROOT, IMAGE_URL, first, second, third, this.avatarFileName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
