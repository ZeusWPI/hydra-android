/*
 * Copyright (c) 2021 The Hydra authors
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

package be.ugent.zeus.hydra.resto.extrafood;

import android.app.Application;
import androidx.annotation.NonNull;

import java.util.List;

import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.ui.AdapterOutOfBoundsException;
import be.ugent.zeus.hydra.common.ui.RequestViewModel;

/**
 * @author Niko Strijbol
 */
public class ExtraFoodViewModel extends RequestViewModel<ExtraFood> {

    public ExtraFoodViewModel(Application application) {
        super(application);
    }

    /**
     * Gets the data depending on the position of the tab. Must agree with the {@link ExtraFoodPagerAdapter}.
     *
     * @param position The position. Must be [0-2].
     * @param data     The data.
     * @return The result.
     */
    static List<Food> getFor(int position, ExtraFood data) {
        return switch (position) {
            case 0 -> data.breakfast();
            case 1 -> data.desserts();
            case 2 -> data.drinks();
            default -> throw new AdapterOutOfBoundsException(position, 3);
        };
    }

    @NonNull
    @Override
    protected Request<ExtraFood> request() {
        return new ExtraFoodRequest(getApplication());
    }
}