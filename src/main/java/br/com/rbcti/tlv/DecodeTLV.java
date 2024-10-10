package br.com.rbcti.tlv;

import static br.com.rbcti.tlv.ByteUtil.fromBigEndian;
import static br.com.rbcti.tlv.TagTLV.BYTE_LENGTH_FLAG;
import static br.com.rbcti.tlv.TagTLV.BYTE_LENGTH_MASK;
import static br.com.rbcti.tlv.TagTLV.CONSTRUCTED_DATA_OBJECT;
import static br.com.rbcti.tlv.TagTLV.SECOND_BYTE_TAG_NUMBER;

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
                    throw new DecodeTLVException("Invalid TLV data. " + ByteUtil.encodeHexSpaced(new byte[] { data[offset - 2], data[offset - 1] }) + " tag without length field.");
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

        } while (offset != data.length);

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
            char[] decorate = new char[level * 2];
            Arrays.fill(decorate, decorateChar);
            System.out.println(String.valueOf(decorate) + _tag);
            if ((_tag.getChildren() != null) && (_tag.getChildren().size() > 0)) {
                printTagTLV(_tag.getChildren(), level + 2, decorateChar);
            }
        }
    }

}
