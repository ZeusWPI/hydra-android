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

import java.math.BigDecimal;

import com.squareup.moshi.Json;

import static be.ugent.zeus.hydra.wpi.tap.TapUtils.createImageUrl;

/**
 * Product from Tab
 *
 * @author Niko Strijbol
 */
public record Product(
        int id,
        String name,
        @Json(name = "price_cents")
        int price,
        @Json(name = "avatar_file_name")
        String avatarFileName,
        String category,
        int stock,
        Integer calories
) {

    private static final String IMAGE_URL = "system/products/avatars/%s/%s/%s/medium/%s";

    public BigDecimal priceDecimal() {
        return new BigDecimal(price()).movePointLeft(2);
    }

    public String imageUrl() {
        return createImageUrl(this.id, IMAGE_URL, avatarFileName());
    }
}
