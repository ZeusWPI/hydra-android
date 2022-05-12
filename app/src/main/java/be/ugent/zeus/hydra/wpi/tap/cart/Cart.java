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

import android.util.Pair;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import be.ugent.zeus.hydra.wpi.tap.product.Product;

/**
 * The cart is a collection of cart products and a map of normal products.
 * 
 * The cart products are what is in the cart, and is what should be saved to
 * the parcel when saving the user's cart. The product map is to make adding
 * stuff faster, so it is not necessarily needed.
 * 
 * @author Niko Strijbol
 */
public class Cart {
    
    private List<CartProduct> orders;
    private Map<Integer, Product> productMap;
    private OffsetDateTime lastEdited;
    
    public CartStorage asStorage() {
        return new CartStorage(orders.stream().map(cp -> new Pair<>(cp.getProductId(), cp.getAmount())).collect(Collectors.toList()), lastEdited);
    }

    public void setLastEdited(OffsetDateTime lastEdited) {
        this.lastEdited = lastEdited;
    }

    public void setOrders(List<CartProduct> orders) {
        this.orders = orders;
    }

    public OffsetDateTime getLastEdited() {
        return lastEdited;
    }

    public List<CartProduct> getOrders() {
        return orders;
    }

    public void setProductMap(Map<Integer, Product> productMap) {
        this.productMap = productMap;
    }
}
