package br.com.rbcti.tlv;

import static br.com.rbcti.tlv.ByteUtil.decodeHex;
import static br.com.rbcti.tlv.ByteUtil.encodeHex;
import static br.com.rbcti.tlv.ByteUtil.fromBigEndian;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

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

    @Test
    public void testFromBigEndian() {

        byte[] minInt = new byte[] {(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        byte[] maxInt = new byte[] {(byte) 0x7f, (byte) 0xff, (byte) 0xff, (byte) 0xff};
        byte[] minLong = new byte[] {(byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        byte[] maxLong = new byte[] {(byte) 0x7f, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff};
        byte[] zeroLong = new byte[8];

        long zeroNumber = fromBigEndian(zeroLong);

        long minIntegerNumber = fromBigEndian(minInt);
        long maxIntegerNumber = fromBigEndian(maxInt);

        long minLongNumber = fromBigEndian(minLong);
        long maxLongNumber = fromBigEndian(maxLong);

        assertEquals(minIntegerNumber, Long.valueOf(Integer.MIN_VALUE).longValue());
        assertEquals(maxIntegerNumber, Long.valueOf(Integer.MAX_VALUE).longValue());

        assertEquals(minLongNumber, Long.MIN_VALUE);
        assertEquals(maxLongNumber, Long.MAX_VALUE);

        assertEquals(zeroNumber, 0L);
    }

    @Test
    public void testFromBigEndianThrowException() {

        byte[] zeroBytes = new byte[0];
        byte[] nineBytes = new byte[9];

        assertThrows(NullPointerException.class, () -> fromBigEndian(null));
        assertThrows(IllegalArgumentException.class, () -> fromBigEndian(zeroBytes));
        assertThrows(IllegalArgumentException.class, () -> fromBigEndian(nineBytes));
    }

}
