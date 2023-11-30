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

package be.ugent.zeus.hydra.resto.menu;

import java.util.List;
import java.util.Locale;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.network.AbstractJsonRequestTest;
import be.ugent.zeus.hydra.common.network.JsonOkHttpRequest;
import be.ugent.zeus.hydra.resto.RestoMenu;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static be.ugent.zeus.hydra.resto.menu.MenuRequest.OVERVIEW_URL;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class MenuRequestTest extends AbstractJsonRequestTest<List<RestoMenu>> {
    @Override
    protected String getRelativePath() {
        return "resto/menu_default.json";
    }

    @Override
    protected JsonOkHttpRequest<List<RestoMenu>> getRequest() {
        return new MenuRequest(context);
    }

    @Test
    public void testDefaultUrlIsUsed() {
        MenuRequest request = new MenuRequest(context);
        String resto = context.getString(R.string.value_resto_default_endpoint);
        String expected = String.format(Locale.ROOT, OVERVIEW_URL, resto);
        Assert.assertEquals(expected, request.apiUrl());
    }
}
