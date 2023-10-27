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

package be.ugent.zeus.hydra.wpi.tap.order;

import androidx.annotation.Nullable;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import be.ugent.zeus.hydra.wpi.tap.product.Product;
import com.squareup.moshi.Json;

/**
 * Represents an order on Tap.
 * 
 * @author Niko Strijbol
 */
public class Order {
    private int id;
    @Json(name = "price_cents")
    private int price;
    @Json(name = "created_at")
    private OffsetDateTime createdAt;
    @Json(name = "deletable_until")
    private OffsetDateTime deletableUntil;
    @Nullable
    @Json(name = "transaction_id")
    private Integer transactionId;
    
    private List<Product> products;

    public int getId() {
        return id;
    }

    @Nullable
    public Integer getTransactionId() {
        return transactionId;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public List<Product> getProducts() {
        return products;
    }
    
    public BigDecimal getTotal() {
        return new BigDecimal(this.price).movePointLeft(2);
    }

    public OffsetDateTime getDeletableUntil() {
        return deletableUntil;
    }
}
