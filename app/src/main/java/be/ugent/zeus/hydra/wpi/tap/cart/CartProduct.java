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

package be.ugent.zeus.hydra.wpi.tap.cart;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Objects;

import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.wpi.tap.product.Product;

/**
 * Represents a product (or more than one) in the user's cart.
 * 
 * Various details about the product are also saved, but this is to reduce
 * the amount of network requests we need to make.
 * 
 * @author Niko Strijbol
 */
public class CartProduct {
    private final int amount;
    private final int productId;
    private final String name;
    private final int price;
    private final String thumbnail;
    
    public CartProduct(Product product, int amount) {
        this(amount, product.getId(), product.getName(), product.getPrice(), product.getImageUrl());
    }

    private CartProduct(int amount, int productId, String name, int price, String thumbnail) {
        this.amount = amount;
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }

    public int getProductId() {
        return productId;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    /**
     * @return A new product cart with the amount incremented by 1.
     */
    public CartProduct increment() {
        return new CartProduct(this.amount + 1, this.productId, this.name, this.price, this.thumbnail);
    }

    /**
     * @return A new product cart with the amount decremented by 1.
     */
    public CartProduct decrement() {
        return new CartProduct(this.amount - 1, this.productId, this.name, this.price, this.thumbnail);
    }

    public BigDecimal getPriceDecimal() {
        return new BigDecimal(price).movePointLeft(2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartProduct that = (CartProduct) o;
        return amount == that.amount && productId == that.productId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, productId);
    }
}
