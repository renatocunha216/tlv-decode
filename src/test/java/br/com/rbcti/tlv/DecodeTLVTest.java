package br.com.rbcti.tlv;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

/**
 * Unit tests for the DecodeTLV class.
 *
 * @author Renato Cunha
 *
 */
public class DecodeTLVTest {

    @Test
    public void testDecodeTLVUniversalClass() throws DecodeTLVException {

        System.out.println(getClass().getSimpleName() + ".testDecodeTLVUniversalClass");

        DecodeTLV decodeTLV = new DecodeTLV(true);

        List<TagTLV> oneTag = decodeTLV.decode(ByteUtil.decodeHex("01    07     00 02 03 04 05 06 FF"));

        // Correct number of TAGs
        assertEquals(oneTag.size(), 1);

        // Correct content
        assertEquals(Arrays.equals(oneTag.get(0).getDataObject(), new byte[] { (byte) 0, (byte) 2, (byte) 3, (byte) 4, (byte) 5, (byte) 6, (byte) 0xFF }), true);


        List<TagTLV> threeTags = decodeTLV.decode(ByteUtil.decodeHex("00  03  A1 A2 A3     01  02  B1 B2     02  04  C1 C2 C3 C4"));

        // Correct number of TAGs
        assertEquals(threeTags.size(), 3);

        // Correct content
        assertEquals(Arrays.equals(threeTags.get(0).getDataObject(), new byte[] { (byte) 0xA1, (byte) 0xA2, (byte) 0xA3}), true);
        assertEquals(Arrays.equals(threeTags.get(1).getDataObject(), new byte[] { (byte) 0xB1, (byte) 0xB2}), true);
        assertEquals(Arrays.equals(threeTags.get(2).getDataObject(), new byte[] { (byte) 0xC1, (byte) 0xC2, (byte) 0xC3, (byte) 0xC4}), true);
    }

    @Test
    public void testDecodeTLVStrinctMode() throws DecodeTLVException {

        final boolean STRICT_MODE = true;

        System.out.println(getClass().getSimpleName() + ".testDecodeTLVStrinctMode");

        DecodeTLV decodeTLV = new DecodeTLV(STRICT_MODE);

        // Wrong size for 01 TAG
        assertThrows(DecodeTLVException.class, () -> decodeTLV.decode(ByteUtil.decodeHex("01    0A     00 02 03 04 05 06 FF")));

        // Wrong size for 02 TAG
        assertThrows(DecodeTLVException.class, () -> decodeTLV.decode(ByteUtil.decodeHex("01    02    A1 A2      02    04    C1 C2 C3")));
    }

}
