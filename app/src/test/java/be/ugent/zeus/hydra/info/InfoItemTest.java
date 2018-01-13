package be.ugent.zeus.hydra.info;

import android.os.Parcel;
import be.ugent.zeus.hydra.common.MockParcel;
import be.ugent.zeus.hydra.common.ModelTest;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

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
    public void getType() throws Exception {
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
    public void equalAndHash() {
        InfoItem one = generate(InfoItem.class);
        one.setSubContent(new ArrayList<>());
        EqualsVerifier.forClass(InfoItem.class)
                .withPrefabValues(InfoItem.class, one, oneDeep)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }

    @Test
    public void parcelable() {
        Parcel parcel = MockParcel.writeToParcelable(oneDeep);
        InfoItem restored = InfoItem.CREATOR.createFromParcel(parcel);
        assertThat(restored, samePropertyValuesAs(oneDeep));
    }

    @Test
    public void serialize() {
        byte[] serialized = SerializationUtils.serialize(oneDeep);
        @SuppressWarnings("unchecked")
        InfoItem restored = SerializationUtils.deserialize(serialized);
        assertThat(restored, samePropertyValuesAs(oneDeep));
    }
}