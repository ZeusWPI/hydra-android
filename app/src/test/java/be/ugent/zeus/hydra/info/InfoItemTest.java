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

package be.ugent.zeus.hydra.info;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.Collections;

import be.ugent.zeus.hydra.common.MockParcel;
import be.ugent.zeus.hydra.common.ModelTest;
import be.ugent.zeus.hydra.testing.Utils;
import org.junit.Before;
import org.junit.Test;

import static be.ugent.zeus.hydra.testing.Assert.assertThat;
import static be.ugent.zeus.hydra.testing.Assert.samePropertyValuesAs;
import static be.ugent.zeus.hydra.testing.Utils.generate;
import static org.junit.Assert.assertEquals;

/**
 * @author Niko Strijbol
 */
public class InfoItemTest extends ModelTest<InfoItem> {

    private InfoItem oneDeep;

    public InfoItemTest() {
        super(InfoItem.class);
    }

    @Before
    public void setUp() {
        // Prevent stack overflow due to too much recursion.
        // Set the sub contents to nothing.
        oneDeep = generate(InfoItem.class);
        InfoItem sub = generate(InfoItem.class);
        sub.setSubContent(new ArrayList<>());
        oneDeep.setSubContent(new ArrayList<>(Collections.singleton(sub)));
    }

    @Test
    public void getType() {
        InfoItem infoItem = generate(InfoItem.class, "urlAndroid", "html", "subContent");
        assertEquals(InfoType.EXTERNAL_LINK, infoItem.getType());
        InfoItem infoItem1 = generate(InfoItem.class, "url", "html", "subContent");
        assertEquals(InfoType.EXTERNAL_APP, infoItem1.getType());
        InfoItem infoItem2 = generate(InfoItem.class, "url", "urlAndroid", "subContent");
        assertEquals(InfoType.INTERNAL, infoItem2.getType());
        InfoItem infoItem3 = generate(InfoItem.class, "url", "urlAndroid", "html");
        assertEquals(InfoType.SUBLIST, infoItem3.getType());
    }

    @Test
    @Override
    public void equalsAndHash() {
        InfoItem one = generate(InfoItem.class);
        one.setSubContent(new ArrayList<>());
        Utils.defaultVerifier(InfoItem.class)
                .withPrefabValues(InfoItem.class, one, oneDeep)
                .verify();
    }

    @Test
    @Override
    public void parcelable() {
        Parcel parcel = MockParcel.writeToParcelable(oneDeep);
        InfoItem restored = InfoItem.CREATOR.createFromParcel(parcel);
        assertThat(restored, samePropertyValuesAs(oneDeep));
    }
}