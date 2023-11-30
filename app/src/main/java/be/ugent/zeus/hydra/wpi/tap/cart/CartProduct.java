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

import be.ugent.zeus.hydra.wpi.tap.product.Product;

import java.math.BigDecimal;

/**
 * Represents a product (or more than one) in the user's cart.
 * <p>
 * Various details about the product are also saved, but this is to reduce
 * the number of network requests we need to make.
 *
 * @author Niko Strijbol
 */
public record CartProduct(
        int amount,
        Product product
) {
    /**
     * @return A new product cart with the amount incremented by 1.
     */
    public CartProduct incrementAmount() {
        return new CartProduct(this.amount + 1, product());
    }

    /**
     * @return A new product cart with the amount decremented by 1.
     */
    public CartProduct decrementAmount() {
        return new CartProduct(this.amount - 1, product());
    }
    
    public BigDecimal totalPrice() {
        return product().priceDecimal().multiply(BigDecimal.valueOf(amount()));
    }
}
