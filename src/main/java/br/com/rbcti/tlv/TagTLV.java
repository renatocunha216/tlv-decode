package br.com.rbcti.tlv;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a TLV tag.
 *
 * @see DecodeTLV
 * @author Renato Cunha
 * @version 1.0
 */
public class TagTLV implements Serializable {

    private static final long serialVersionUID = -7248849029801418545L;

    public static final int APPLICATION_CLASS = 0x40;        // bit 7 = 1
    public static final int CONTEXT_DEPENDENT_CLASS = 0x80;  // bit 8 = 1

    /**
     * Significa que o objeto da tag é um objeto complexo, ou seja, contém
     * outros objetos no seu conteúdo.
     * // bit 6 = 1
     */
    public static final int CONSTRUCTED_DATA_OBJECT = 0x20;

    /**
     * Significa que o segundo byte também faz parte do id da tag
     * bit 1,2,3,4,5 = 1
     */
    public static final int SECOND_BYTE_TAG_NUMBER = 0x1F;

    public static final int BYTE_LENGTH_FLAG = 0X80;

    public static final int BYTE_LENGTH_MASK = 0X7F;

    private int tagId;
    private int tagIdFirstByte;
    private byte[] dataObject;
    private List<TagTLV> children;

    public TagTLV(int tagId, int tagIdFirstByte, byte[] dataObject) {
        this.tagId = tagId;
        this.tagIdFirstByte = tagIdFirstByte;
        this.dataObject = dataObject;
    }

    public int getTagId() {
        return tagId;
    }

    public int getTagIdFirstByte() {
        return tagIdFirstByte;
    }

    public byte[] getDataObject() {
        return dataObject;
    }

    public void addChildTag(TagTLV tagTLV) {
        if (children == null) {
            children = new ArrayList<TagTLV>();
        }
        children.add(tagTLV);
    }

    public List<TagTLV> getChildren() {
        return children;
    }

    /**
     * Indica que o conteúdo desta tag contém 0, 1 ou mais outros elementos.
     * @return
     */
    public boolean isConstructedObject() {
        return ((tagIdFirstByte & CONSTRUCTED_DATA_OBJECT) == CONSTRUCTED_DATA_OBJECT);
    }

    /**
     * Indica que o conteúdo desta tag contém diretamente o valor do elemento.
     * @return
     */
    public boolean isPrimitiveObject() {
        return !((tagIdFirstByte & CONSTRUCTED_DATA_OBJECT) == CONSTRUCTED_DATA_OBJECT);
    }

    public String getDescripton() {
        TagTLVEnum tag = TagTLVEnum.valueOf(tagId);
        if (tag != null) {
            return tag.toString();
        }
        return "";
    }

    public String getDataObjectAsString() {

        TagTLVEnum tag = TagTLVEnum.valueOf(tagId);

        if (tag != null) {
            switch (tag) {
                case APPLICATION_LABEL:
                case APPLICATION_PREFERRED_NAME:
                case LANGUAGE_PREFERENCE:
                case DEDICATED_FILE_NAME:
                    return new String(dataObject);
                default:
            }
        }
        return "";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + tagId;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TagTLV other = (TagTLV) obj;
        if (tagId != other.tagId)
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TagTLV [tagId=");
        builder.append(tagId);
        builder.append(", description=");
        builder.append(getDescripton());
        builder.append(", dataObject=");
        builder.append(ByteUtil.encodeHexSpaced(dataObject));
        builder.append(", dataObjectAsString=");
        builder.append(getDataObjectAsString());
        builder.append("]");
        return builder.toString();
    }

}
