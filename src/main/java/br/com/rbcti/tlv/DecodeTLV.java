package br.com.rbcti.tlv;

import static br.com.rbcti.tlv.TagTLV.BYTE_LENGTH_FLAG;
import static br.com.rbcti.tlv.TagTLV.BYTE_LENGTH_MASK;
import static br.com.rbcti.tlv.TagTLV.CONSTRUCTED_DATA_OBJECT;
import static br.com.rbcti.tlv.TagTLV.SECOND_BYTE_TAG_NUMBER;
import static br.com.rbcti.tlv.ByteUtil.fromBigEndian;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Decode data in TLV (Tag Length Value) format.<br>
 *
 * @see TagTLV
 * @see TagTLVEnum
 * @author Renato Cunha
 * @version 1.0
 */
public class DecodeTLV {

    private static final int MAX_BYTE_LENGTH = 4;

    private List<TagTLV> tags;
    private boolean strict;

    public DecodeTLV() {
        this.strict = false;
    }

    public DecodeTLV(boolean strict) {
        this.strict = strict;
    }

    public List<TagTLV> decode(byte[] data) throws DecodeTLVException {

        List<TagTLV> tags = new ArrayList<TagTLV>();

        int offset = 0;

        do {
            int tagId = data[offset] & 0xFF;
            int tagIdFirstByte = tagId;

            offset++;

            if (data.length == offset) {
                throw new DecodeTLVException("Invalid TLV data. " + ByteUtil.encodeHex((byte)(tagIdFirstByte & 0xFF)) + " tag without size field.");
            }

            if ((tagId & SECOND_BYTE_TAG_NUMBER) == SECOND_BYTE_TAG_NUMBER) {
                // It means that the second byte also identifies the tag
                tagId = (tagId << 8) + (data[offset] & 0xFF);
                offset++;

                if (data.length == offset) {
                    throw new DecodeTLVException("Invalid TLV data. " + ByteUtil.encodeHexSpaced(new byte[] {data[offset-2], data[offset-1]}) + " tag without length field.");
                }
            }

            // Get object size
            int len = data[offset] & 0xFF;
            offset++;

            if ((len & BYTE_LENGTH_FLAG) == BYTE_LENGTH_FLAG) {
                int numberBytesLength = len & BYTE_LENGTH_MASK;

                if (numberBytesLength > MAX_BYTE_LENGTH) {
                    throw new DecodeTLVException("Maximum size is 4 bytes.");
                }

                byte[] lengthBytesArray = new byte[MAX_BYTE_LENGTH];

                System.arraycopy(data, offset, lengthBytesArray, (MAX_BYTE_LENGTH - numberBytesLength), numberBytesLength);

                offset += numberBytesLength;

                long lengthRead = fromBigEndian(lengthBytesArray);

                if (lengthRead > Integer.MAX_VALUE) {
                    throw new DecodeTLVException("The maximum value of the length field is " + Integer.MAX_VALUE);
                }

                len = (int) lengthRead;
            }

            if ((data.length - offset) < len) {
                if (strict) {
                    throw new DecodeTLVException("Invalid data length for " + tagId + " TAG.");
                }

                // fix the size when the content size is less than the value entered
                len = data.length - offset;
            }

            byte[] dataObj = new byte[len];
            System.arraycopy(data, offset, dataObj, 0, len);
            offset += len;

            TagTLV tagTLV = new TagTLV(tagId, tagIdFirstByte, dataObj);
            tags.add(tagTLV);

            if ((tagIdFirstByte & CONSTRUCTED_DATA_OBJECT) == CONSTRUCTED_DATA_OBJECT) {
                List<TagTLV> childrenTags = decode(dataObj);

                for (TagTLV tag : childrenTags) {
                    tagTLV.addChildTag(tag);
                }
            }

        } while(offset != data.length);

        this.tags = tags;

        return this.tags;
    }

    public List<TagTLV> getTags() {
        return tags;
    }

    public static TagTLV findTagTLV(List<TagTLV>tags, int tagId) {
        for (TagTLV tag : tags) {
            if (tag.getTagId() == tagId) {
                return tag;
            }
            if ((tag.getChildren() != null) && (tag.getChildren().size() > 0)) {
                TagTLV tagReturn = findTagTLV(tag.getChildren(), tagId);
                if (tagReturn != null) {
                    return tagReturn;
                }
            }
        }
        return null;
    }

    public static TagTLV findTagTLV(List<TagTLV>tags, TagTLVEnum tagTLV) {
        return findTagTLV(tags, tagTLV.getId());
    }

    public static void printTagTLV(List<TagTLV> tags, int level, char decorateChar) {
        for (TagTLV _tag : tags) {
            char[] decorate = new char[level*2];
            Arrays.fill(decorate, decorateChar);
            System.out.println(String.valueOf(decorate) + _tag);
            if ((_tag.getChildren() != null) && (_tag.getChildren().size() > 0)) {
                printTagTLV(_tag.getChildren(), level+2, decorateChar);
            }
        }
    }

    public static void __main(String[] args) {

        DecodeTLV decodeTLV = new DecodeTLV();
        try {
            //Com tamanho errado
            List<TagTLV> tags = decodeTLV.decode(ByteUtil.decodeHex("61    14    4f 04 44 47 30 37 50 03 56 41 4c   5F 24 03 03 03 31"));

            printTagTLV(tags, 2, '-');


        } catch (DecodeTLVException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void _main(String[] args) throws Exception {

        DecodeTLV decodeTLV = new DecodeTLV(true);

        //List<TagTLV>tags = decodeTLV.decode(ByteUtil.decodeHex("61 13   4F 05 F0 00 00 01 03    50 0A    4D 55 4C 54 4F 53 20 41 70 70    85 04 08 00 00 08  86 04 00 00 00 00 "));
        //List<TagTLV>tags = decodeTLV.decode(ByteUtil.decodeHex(
        //"6F 35 84 08 45 4F 50 43 43 41 52 44 A5 29 50 06 55 5A 4B 41 52 54 5F 2D 06 75 7A 72 75 65 6E 87 01 01 9F 11 01 01 9F 12 06 55 5A 4B 41 52 54 BF 0C 05 9F 4D 02 0B 0A"
        //        ));

        List<TagTLV>tags = decodeTLV.decode(ByteUtil.decodeHex("01 81 03 c1 c2 c3"));

        tags = decodeTLV.getTags();
        printTagTLV(tags, 2, '-');

        // TagTLV tagTLV = findTagTLV(tags, TagTLVEnum.LOG_ENTRY.getId());

        //System.out.println("::" + tagTLV);
    }

    public static void main(String[] args) {

        final boolean STRICT_MODE = true;

        DecodeTLV decodeTLV = new DecodeTLV(STRICT_MODE);

        try {
            List<TagTLV> tags = decodeTLV.decode(ByteUtil.decodeHex("6F 1E A5 1C 50 06 41 70 70 54 73 74 5F 2D 09 50 6F 72 74 75 67 75 65 73 BF 0C 05 9F 4D 02 0B 0A"));
            DecodeTLV.printTagTLV(tags, 1, '-');

            TagTLV logEntry = DecodeTLV.findTagTLV(tags, TagTLVEnum.LOG_ENTRY);
            System.out.println("");
            System.out.println(logEntry);

        } catch (DecodeTLVException e) {
            e.printStackTrace();
        }

    }



}
