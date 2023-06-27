package net.geidea.paymentsdk.internal.serialization;

import net.geidea.paymentsdk.model.ExpiryDate;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SerializationJavaTest {

    @Test
    public void fromJson() {
        //language=JSON
        ExpiryDate actual = ExpiryDate.fromJson("{\"month\":11,\"year\":22}");
        assertEquals(new ExpiryDate(11, 22), actual);
    }

    @Test
    public void toJson() {
        ExpiryDate ed = new ExpiryDate(11, 22);
        //language=JSON
        assertEquals("{\"month\":11,\"year\":22}", ed.toJson(false));
    }

    @Test
    public void toJson_pretty() {
        ExpiryDate ed = new ExpiryDate(11, 22);
        //language=JSON
        String expected =
                "{\n" +
                "    \"month\": 11,\n" +
                "    \"year\": 22\n" +
                "}";
        assertEquals(expected, ed.toJson(true));
    }
}
