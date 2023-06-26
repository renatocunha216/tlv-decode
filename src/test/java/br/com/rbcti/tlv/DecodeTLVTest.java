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
    public void testDecodeTLVStrictMode() throws DecodeTLVException {

        final boolean STRICT_MODE = true;

        System.out.println(getClass().getSimpleName() + ".testDecodeTLVStrictMode");

        DecodeTLV decodeTLV = new DecodeTLV(STRICT_MODE);

        // Wrong size for 01 TAG
        assertThrows(DecodeTLVException.class, () -> decodeTLV.decode(ByteUtil.decodeHex("01    0A     00 02 03 04 05 06 FF")));

        // Wrong size for 02 TAG
        assertThrows(DecodeTLVException.class, () -> decodeTLV.decode(ByteUtil.decodeHex("01    02    A1 A2      02    04    C1 C2 C3")));
    }

    @Test
    public void testDecodeTLVNoStrictMode() throws DecodeTLVException {

        System.out.println(getClass().getSimpleName() + ".testDecodeTLVNoStrictMode");

        DecodeTLV decodeTLV = new DecodeTLV();

        // 0X9F4D - Log Entry
        List<TagTLV> tlvData = decodeTLV.decode(ByteUtil.decodeHex("9F 4D 84 00 00 00 FF  01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F"));

        // Correct number of TAGs
        assertEquals(tlvData.size(), 1);

        TagTLV logEntryTag = DecodeTLV.findTagTLV(tlvData, TagTLVEnum.LOG_ENTRY);

        assertEquals(Arrays.equals(logEntryTag.getDataObject(), new byte[] { (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09, (byte) 0x0A, (byte) 0x0B, (byte) 0x0C, (byte) 0x0D, (byte) 0x0E, (byte) 0x0F }), true);
    }

    @Test
    public void testDecodeTLVApplicationClass() throws DecodeTLVException {

        System.out.println(getClass().getSimpleName() + ".testDecodeTLVApplicationClass");

        DecodeTLV decodeTLV = new DecodeTLV(true);

        // 0X50 Application Label =  SmartcardApplication
        // 0X4F Application Identifier = 52 42 43 41 50 50
        List<TagTLV> tlvData = decodeTLV.decode(ByteUtil.decodeHex("50  14  53 6D 61 72 74 63 61 72 64 41 70 70 6C 69 63 61 74 69 6F 6E    4F  06   52 42 43 41 50 50"));

        // Correct number of TAGs
        assertEquals(tlvData.size(), 2);

        TagTLV applicationLabelTag = DecodeTLV.findTagTLV(tlvData, TagTLVEnum.APPLICATION_LABEL);
        TagTLV applicationIdentifierTag = DecodeTLV.findTagTLV(tlvData, TagTLVEnum.APPLICATION_IDENTIFIER);

        assertEquals(applicationLabelTag.getDataObjectAsString(), "SmartcardApplication");

        assertEquals(Arrays.equals(applicationIdentifierTag.getDataObject(), new byte[] { (byte) 0x52, (byte) 0x42, (byte) 0x43, (byte) 0x41, (byte) 0x50, (byte) 0x50 }), true);
    }

    @Test
    public void testDecodeTLVApplicationClass2Bytes() throws DecodeTLVException {

        System.out.println(getClass().getSimpleName() + ".testDecodeTLVApplicationClass2Bytes");

        DecodeTLV decodeTLV = new DecodeTLV(true);

        // 0x5F2D - Language preference
        // 0x9F4E - Merchant Name and Location
        List<TagTLV> tlvData = decodeTLV.decode(ByteUtil.decodeHex("5F 2D    05   70 74 2D 62 72     9F 4E   08   72 62 63 74 69 5F 42 52"));

        // Correct number of TAGs
        assertEquals(tlvData.size(), 2);

        TagTLV languagePreferenceTag = DecodeTLV.findTagTLV(tlvData, TagTLVEnum.LANGUAGE_PREFERENCE);
        TagTLV merchantNameAndLocation = DecodeTLV.findTagTLV(tlvData, 40782);

        assertEquals(languagePreferenceTag.getDataObjectAsString(), "pt-br");
        assertEquals(new String(merchantNameAndLocation.getDataObject()), "rbcti_BR");
    }


    @Test
    public void testDecodeTLVLimitLength() throws DecodeTLVException {

        System.out.println(getClass().getSimpleName() + ".testDecodeTLVLimitLength");

        DecodeTLV decodeTLV = new DecodeTLV();

        // 0X9F4D - Log Entry - 5 bytes for the length - limit is 4 bytes
        assertThrows(DecodeTLVException.class, () -> decodeTLV.decode(ByteUtil.decodeHex("9F 4D 85 00 00 00 00 FF  01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F")));
    }

    @Test
    public void testDecodeTLVLargeLength() throws DecodeTLVException {

        System.out.println(getClass().getSimpleName() + ".testDecodeTLVLargeLength");

        final boolean STRICT_MODE = true;

        byte[] tlvApplicationLabel = ByteUtil.decodeHex("50    03    41 42 43");

        byte[] _tlData = ByteUtil.decodeHex("9F 4D 84 00 FF FF FF");
        byte[] _tlvData = new byte[_tlData.length + 16777215 + tlvApplicationLabel.length];
        System.arraycopy(_tlData, 0, _tlvData, 0, _tlData.length);

        System.arraycopy(tlvApplicationLabel, 0, _tlvData, (_tlData.length + 16777215), tlvApplicationLabel.length);

        DecodeTLV decodeTLV = new DecodeTLV(STRICT_MODE);

        List<TagTLV> tlvData = decodeTLV.decode(_tlvData);

        // Correct number of TAGs (LOG ENTRY + APPLICATION LABEL)
        assertEquals(tlvData.size(), 2);

        assertEquals(Arrays.equals(tlvData.get(0).getDataObject(), new byte[16777215]), true);
        assertEquals(tlvData.get(1).getDataObjectAsString(), "ABC");
    }

}
