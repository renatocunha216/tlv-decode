package br.com.rbcti.tlv;

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

    private List<TagTLV> tags;

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
                // Significa que o segundo byte também identifica a tag
                tagId = (tagId << 8) + (data[offset] & 0xFF);
                offset++;

                if (data.length == offset) {
                    throw new DecodeTLVException("Invalid TLV data. " + ByteUtil.encodeHexSpaced(new byte[] {data[offset-2], data[offset-1]}) + " tag without size field.");
                }
            }

            // Get object size
            int len = data[offset] & 0xFF;
            offset++;

            if (len == 0x81) {
                // Size 2 bytes
                len = data[offset] & 0xFF;

                if (!(len >= 0x80 && len <= 0xFF)) {
                    throw new DecodeTLVException("Object size field is invalid. Expected value 0x80 to 0xFF. Value read " + ByteUtil.encodeHex((byte)(len & 0xFF)));
                }

            } else if (len == 0x82) {
                // Size 3 bytes
                len = data[offset] & 0xFF;
                offset++;
                len = (len << 8) + (data[offset] & 0xFF);
                offset++;

                if (!(len >= 0x0100 && len <= 0xFFFF)) {
                    throw new DecodeTLVException("Object size field is invalid. Expected value 0x0100 to 0xFFFF. Value read " + ByteUtil.encodeHex((byte) (len & 0xFF)));
                }

            } else if (!(len >= 0x00 && len <= 0x7F)) {
                throw new DecodeTLVException("Object size field is invalid. Expected value 0x00 a 0x7F. Value read " + ByteUtil.encodeHex((byte) (len & 0xFF)));
            }

            if ((data.length - offset) < len) {
                //throw new DecodeTLVException("Invalid data size.");
                //System.out.println("WARN");
                //Faz ajuste para tolerar TLV com erro no byte de tamanho, neste caso com tamanho
                //maior que o conteúdo do array de bytes.
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

    public static void main(String[] args) throws Exception {

        DecodeTLV decodeTLV = new DecodeTLV();

        //List<TagTLV>tags = parser.parse(ByteUtil.decodeHex("61 13   4F 05 F0 00 00 01 03    50 0A    4D 55 4C 54 4F 53 20 41 70 70    85 04 08 00 00 08  86 04 00 00 00 00 "));
        List<TagTLV>tags = decodeTLV.decode(ByteUtil.decodeHex(
        "6F 35 84 08 45 4F 50 43 43 41 52 44 A5 29 50 06 55 5A 4B 41 52 54 5F 2D 06 75 7A 72 75 65 6E 87 01 01 9F 11 01 01 9F 12 06 55 5A 4B 41 52 54 BF 0C 05 9F 4D 02 0B 0A"
                ));

        tags = decodeTLV.getTags();
        printTagTLV(tags, 2, '-');

        TagTLV tagTLV = findTagTLV(tags, TagTLVEnum.LOG_ENTRY.getId());

        System.out.println("::" + tagTLV);
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

}
