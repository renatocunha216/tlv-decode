package br.com.rbcti.tlv;

import static br.com.rbcti.tlv.ByteUtil.decodeHex;
import static br.com.rbcti.tlv.ByteUtil.encodeHex;
import static org.testng.Assert.assertEquals;

import java.util.Arrays;

import org.testng.annotations.Test;

/**
 * Unit tests for the ByteUtil class.
 *
 * @author Renato Cunha
 *
 */
public class ByteUtilTest {

    private static byte[] SAMPLE1_BYTES = new byte[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private static byte[] SAMPLE2_BYTES = new byte[] {(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff};
    private static byte[] SAMPLE3_BYTES = new byte[] {(byte) 0xA5, (byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x5A};

    private static String SAMPLE1_STRING = "00000000000000000000";
    private static String SAMPLE2_STRING = "ffffffffffffffffffff";
    private static String SAMPLE3_STRING = "a500010203040506075a";

    @Test
    public void testEncodeHex() {

        System.out.println(getClass().getSimpleName() + ".testEncodeHex");

        for (int c = 0; c <= 255; c++) {
            assertEquals(encodeHex((byte) c), String.format("%2s", Integer.toHexString(c)).replaceAll(" ", "0"));
        }

        assertEquals(encodeHex(SAMPLE1_BYTES), SAMPLE1_STRING);
        assertEquals(encodeHex(SAMPLE2_BYTES), SAMPLE2_STRING);
        assertEquals(encodeHex(SAMPLE3_BYTES), SAMPLE3_STRING);
    }

    @Test
    public void testDecodeHex() {

        System.out.println(getClass().getSimpleName() + ".testDecodeHex");

        assertEquals(Arrays.equals(decodeHex(SAMPLE1_STRING), SAMPLE1_BYTES), true);
        assertEquals(Arrays.equals(decodeHex(SAMPLE2_STRING), SAMPLE2_BYTES), true);
        assertEquals(Arrays.equals(decodeHex(SAMPLE3_STRING), SAMPLE3_BYTES), true);
    }

}
