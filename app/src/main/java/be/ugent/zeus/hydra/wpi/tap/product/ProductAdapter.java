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

import android.view.ViewGroup;
import androidx.annotation.NonNull;

import java.util.Locale;
import java.util.function.Consumer;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.SearchableAdapter;
import be.ugent.zeus.hydra.common.utils.ViewUtils;

/**
 * @author Niko Strijbol
 */
public class ProductAdapter extends SearchableAdapter<Product, ProductViewHolder> {
    
    private final Consumer<Product> onClickListener;

    public ProductAdapter() {
        this(null);
    }
    
    public ProductAdapter(Consumer<Product> onClickListener) {
        super(p -> p.getName().toLowerCase(Locale.getDefault()));
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup p, int viewType) {
        return new ProductViewHolder(ViewUtils.inflate(p, R.layout.item_product), onClickListener);
    }
}
