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

    private static final String IMAGE_URL = "system/products/avatars/%s/%s/%s/medium/%s";
    
    private int amount;
    private int productId;
    private String name;
    private int price;
    private String thumbnail;
    
    public static CartProduct fromProduct(Product product, int amount) {
        CartProduct cartProduct = new CartProduct();
        cartProduct.price = product.getPrice();
        cartProduct.productId = product.getId();
        cartProduct.name = product.getName();
        cartProduct.thumbnail = product.getAvatarFileName();
        cartProduct.amount = amount;
        return cartProduct;
    }

    public String getThumbnailUrl() {
        if (this.thumbnail == null) {
            return null;
        }
        String paddedID = String.format(Locale.ROOT, "%09d", productId);
        String first = paddedID.substring(0, 3);
        String second = paddedID.substring(3, 6);
        String third = paddedID.substring(6, 9);

        return Endpoints.TAP + String.format(Locale.ROOT, IMAGE_URL, first, second, third, this.thumbnail);
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
