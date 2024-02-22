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

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import be.ugent.zeus.hydra.wpi.tap.product.Product;

/**
 * The cart is a collection of cart products and a map of normal products.
 * <p>
 * The cart products are what is in the cart, and is what should be saved to
 * the parcel when saving the user's cart. The product map is to make adding
 * stuff faster, so it is not necessarily needed.
 * <p>
 * Because of how the diff algorithms work in the RecyclerView, the cart is
 * partially read-only: this list of orders is read-only, and will result in
 * a new cart you'll need to save.
 *
 * @author Niko Strijbol
 */
public class Cart {
    private static final String TAG = "Cart";

    private final List<CartProduct> orders;
    private final Map<Integer, Product> productIdToProduct;
    private final Map<String, Integer> barcodeToProductId;
    private final OffsetDateTime lastEdited;

    private Cart(List<CartProduct> orders, Map<Integer, Product> productIdToProduct, Map<String, Integer> barcodeToProductId) {
        this(orders, productIdToProduct, barcodeToProductId, OffsetDateTime.now());
    }

    private Cart(List<CartProduct> orders, Map<Integer, Product> productIdToProduct, Map<String, Integer> barcodeToProductId, OffsetDateTime lastEdited) {
        this.orders = orders;
        this.productIdToProduct = productIdToProduct;
        this.barcodeToProductId = barcodeToProductId;
        this.lastEdited = lastEdited;
    }

    /**
     * Create a new cart based on an existing one.
     *
     * @param existingCart       The existing cart.
     * @param productIdToProduct Map of product ID's to products.
     * @param barcodeToProductId Map of barcodes to product ID's.
     */
    public Cart(StorageCart existingCart, Map<Integer, Product> productIdToProduct, Map<String, Integer> barcodeToProductId) {
        this(fromExisting(existingCart, productIdToProduct), productIdToProduct, barcodeToProductId, existingCart.lastEdited());
    }

    private static List<CartProduct> fromExisting(StorageCart cart, Map<Integer, Product> productIdToProduct) {
        List<CartProduct> orders = new ArrayList<>();
        for (ProductIdAmount productIdAmount : cart.products()) {
            Product product = productIdToProduct.get(productIdAmount.productId());
            if (product == null) {
                // Skip this product, as it no longer exists.
                continue;
            }
            orders.add(new CartProduct(productIdAmount.amount(), product));
        }
        return orders;
    }

    /**
     * Get a smaller class suitable for saving. It contains all data
     * that cannot be found on the network.
     */
    public StorageCart forStorage() {
        return new StorageCart(orders.stream().map(cp -> new ProductIdAmount(cp.product().id(), cp.amount())).collect(Collectors.toList()), lastEdited);
    }

    public Map<String, List<Map<String, Object>>> forJson() {
        List<Map<String, Object>> attributes = new ArrayList<>();
        for (CartProduct cartProduct : this.getOrders()) {
            Map<String, Object> data = new HashMap<>();
            data.put("product_id", cartProduct.product().id());
            data.put("count", cartProduct.amount());
            attributes.add(data);
        }
        Map<String, List<Map<String, Object>>> total = new HashMap<>();
        total.put("order_items_attributes", attributes);
        return total;
    }

    /**
     * @return Unmodifiable Map of product ID's to products.
     */
    public Map<Integer, Product> getProductIdToProduct() {
        return Collections.unmodifiableMap(productIdToProduct);
    }

    /**
     * Get the product corresponding to a given barcode.
     *
     * @param barcode The barcode to search.
     * @return The found product or null.
     */
    @Nullable
    public Product getProductFor(@NonNull String barcode) {
        Integer productId = barcodeToProductId.get(barcode);
        if (productId == null) {
            return null;
        }
        return productIdToProduct.get(productId);
    }

    /**
     * Get the index in the order list of a specific product.
     */
    private OptionalInt getPosition(Product product) {
        // Find the position of an existing cart product if available.
        return IntStream.range(0, orders.size())
                .filter(i -> orders.get(i).product().id() == product.id())
                .findFirst();
    }

    /**
     * Add a product or increment its count if already present.
     *
     * @param product The product to add.
     * @return A new cart that has the added product.
     */
    @NonNull
    public Cart addProduct(@NonNull Product product) {
        OptionalInt index = this.getPosition(product);
        if (index.isPresent()) {
            return increment(orders.get(index.getAsInt()));
        } else {
            CartProduct newProduct = new CartProduct(1, product);
            List<CartProduct> replacementList = new ArrayList<>(orders);
            replacementList.add(newProduct);
            return new Cart(replacementList, productIdToProduct, barcodeToProductId);
        }
    }

    /**
     * Maybe add the product to the cart.
     */
    @NonNull
    public Cart maybeAddInitialProduct(int productId) {
        Product favourite = productIdToProduct.get(productId);
        if (favourite == null) {
            Log.i(TAG, "maybeAddProduct: not adding initial product, " + productId + " is invalid.");
            return this;
        }
        // Only add the product if it is not yet in the cart.
        // Since the cart is saved between activities, this prevents
        // adding the same product again, while you might not want that.
        if (this.getPosition(favourite).isPresent()) {
            Log.i(TAG, "maybeAddProduct: not adding same product twice product.");
            return this;
        }
        return addProduct(favourite);
    }

    public Cart increment(CartProduct product) {
        int index = orders.indexOf(product);
        CartProduct replacement = product.incrementAmount();
        List<CartProduct> replacementList = new ArrayList<>(orders);
        replacementList.set(index, replacement);
        return new Cart(replacementList, productIdToProduct, barcodeToProductId);
    }

    public Cart remove(CartProduct product) {
        List<CartProduct> replacementList = new ArrayList<>(orders);
        replacementList.remove(product);
        return new Cart(replacementList, productIdToProduct, barcodeToProductId);
    }

    public Cart decrement(CartProduct product) {
        if (product.amount() == 1) {
            return remove(product);
        } else {
            int index = orders.indexOf(product);
            CartProduct replacement = product.decrementAmount();
            List<CartProduct> replacementList = new ArrayList<>(orders);
            replacementList.set(index, replacement);
            return new Cart(replacementList, productIdToProduct, barcodeToProductId);
        }
    }

    public BigDecimal getTotalPrice() {
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartProduct product : getOrders()) {
            totalAmount = totalAmount.add(product.totalPrice());
        }
        return totalAmount;
    }

    public int getTotalProducts() {
        int totalProducts = 0;
        for (CartProduct product : getOrders()) {
            totalProducts += product.amount();
        }
        return totalProducts;
    }

    public Cart clear() {
        return new Cart(new ArrayList<>(), productIdToProduct, barcodeToProductId);
    }

    public List<CartProduct> getOrders() {
        return Collections.unmodifiableList(orders);
    }
}
