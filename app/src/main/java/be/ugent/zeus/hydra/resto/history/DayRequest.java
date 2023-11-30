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

package be.ugent.zeus.hydra.resto.history;

import android.content.Context;
import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.util.Locale;

import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonOkHttpRequest;
import be.ugent.zeus.hydra.resto.RestoChoice;
import be.ugent.zeus.hydra.resto.RestoMenu;


/**
 * Request the menu for a single day.
 * <p>
 * If not properly initialised before use (call {@link #setChoice(RestoChoice)} and {@link #setDate(LocalDate)}, it
 * will throw {@link IllegalStateException}s when used.
 *
 * @author Niko Strijbol
 */
public class DayRequest extends JsonOkHttpRequest<RestoMenu> {

    private static final String OVERVIEW_URL = Endpoints.ZEUS_V2 + "resto/menu/%s/%d/%d/%d.json";

    private LocalDate date;
    private RestoChoice choice;

    public DayRequest(Context context) {
        super(context, RestoMenu.class);
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setChoice(RestoChoice choice) {
        this.choice = choice;
    }

    public boolean isSetup() {
        return date != null && choice != null;
    }

    @NonNull
    @Override
    protected String apiUrl() {
        if (date == null) {
            throw new IllegalStateException("The date MUST be set before using the request.");
        }
        if (choice == null) {
            throw new IllegalStateException("The resto choice MUST be set before using the request.");
        }

        return String.format(Locale.ROOT, OVERVIEW_URL, choice.endpoint(), date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }
}