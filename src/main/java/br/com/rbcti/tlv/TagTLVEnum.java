package br.com.rbcti.tlv;

/**
 *
 * https://emvlab.org/emvtags/all/
 *
 * @author renato cunha
 * @version 1.0
 */
public enum TagTLVEnum {
    APPLICATION_TEMPLATE(0x61),
    APPLICATION_LABEL(0x50),
    APPLICATION_IDENTIFIER (0x4F),                           // AID
    APPLICATION_EXPIRATION_DATE(0x5F24),
    DIRECTORY_DISCRETIONARY_TEMPLATE(0x73),
    FILE_CONTROL_INFORMATION_PROPRIETARY_TEMPLATE(0xA5),
    FILE_CONTROL_INFORMATION_TEMPLATE(0x6F),                 // FCI
    APPLICATION_PREFERRED_NAME(0x9F12),
    LANGUAGE_PREFERENCE(0x5F2D),
    APPLICATION_PRIORITY_INDICATOR(0x87),
    SHORT_FILE_IDENTIFIER(0x88),                             // SFI
    DEDICATED_FILE_NAME(0x84),                               // DF
    ISSUER_CODE_TABLE_INDEX(0x9F11),
    FILE_CONTROL_INFORMATION_ISSUER_DISCRETIONARY_DATA(0xBF0C),
    LOG_ENTRY(0x9F4D);

    private TagTLVEnum(int tagId) {
        id = tagId;
    }

    private int id;

    public int getId() {
        return id;
    }

    public static TagTLVEnum valueOf(int id) {
        TagTLVEnum result = null;
        for (TagTLVEnum tag : TagTLVEnum.values()) {
            if (tag.getId() == id) {
                result = tag;
                break;
            }
        }
        return result;
    }

}
