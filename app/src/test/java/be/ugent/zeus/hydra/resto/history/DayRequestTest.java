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

import java.time.LocalDate;

import be.ugent.zeus.hydra.common.network.AbstractJsonRequestTest;
import be.ugent.zeus.hydra.common.network.JsonOkHttpRequest;
import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.resto.RestoChoice;
import be.ugent.zeus.hydra.resto.RestoMenu;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class DayRequestTest extends AbstractJsonRequestTest<RestoMenu> {

    @Override
    protected String getRelativePath() {
        return "resto/singleday.json";
    }

    @Override
    protected JsonOkHttpRequest<RestoMenu> getRequest() {
        DayRequest request = new DayRequest(context);
        request.setChoice(new RestoChoice("test", "test"));
        request.setDate(LocalDate.now());
        return request;
    }

    @Test
    public void testUrlFormat() {
        LocalDate now = LocalDate.now();
        DayRequest request = new DayRequest(context);
        request.setChoice(new RestoChoice("test", "test"));
        request.setDate(now);
        assertThat(request.getAPIUrl(), endsWith("test/" + now.getYear() + "/" + now.getMonthValue() + "/" + now.getDayOfMonth() + ".json"));
    }

    @Test(expected = IllegalStateException.class)
    public void testNonInitialised() {
        Request<RestoMenu> request = new DayRequest(context);
        request.execute();
    }
}