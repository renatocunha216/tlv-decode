# tlv-decode
[![en](https://github.com/renatocunha216/common/blob/main/images/lang-en.svg?raw=true)](https://github.com/renatocunha216/tlv-decode/blob/main/README.en.md)
[![pt-br](https://github.com/renatocunha216/common/blob/main/images/lang-pt-br.svg?raw=true)](https://github.com/renatocunha216/tlv-decode/blob/main/README.md)

Java implementation of a data decoder encoded in BER-TLV format.<br>

**T** - Tag - One or more bytes that define the class, type and number of bytes of the tag.<br>
**L** - Length - One or more bytes that define the length of the value field.<br>
**V** - Field with object data with length defined in **L**. If L = '00' this field does not exist.<br>
        

### TAG field encoding

| b8 | b7 | b6 | b5 | b4 | b3 | b2 | b1 | Descrição               |
|----|----|----|----|----|----|----|----|-------------------------|
|  0 |  0 |  - |  - |  - |  - |  - |  - | Universal class         |
|  0 |  1 |  - |  - |  - |  - |  - |  - | Application class       |
|  1 |  0 |  - |  - |  - |  - |  - |  - | Context-specific class  |
|  1 |  1 |  - |  - |  - |  - |  - |  - | Private class           |
|  - |  - |  0 |  - |  - |  - |  - |  - | Primitive data object   |
|  - |  - |  1 |  - |  - |  - |  - |  - | Constructed data object |
|  - |  - |  - |  1 |  1 |  1 |  1 |  1 | See subsequent bytes    |
|  - |  - |  - |  - |  - |  - |  - |  - | Any other value < 31 - Tag number |



### Example of use

```java
/**
 * Example of use.
 *
 * @param args
 */
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
```

Output
```
--TagTLV [tagId=111, description=FILE_CONTROL_INFORMATION_TEMPLATE, dataObject=a5 1c 50 06 41 70 70 54 73 74 5f 2d 09 50 6f 72 74 75 67 75 65 73 bf 0c 05 9f 4d 02 0b 0a, dataObjectAsString=]
------TagTLV [tagId=165, description=FILE_CONTROL_INFORMATION_PROPRIETARY_TEMPLATE, dataObject=50 06 41 70 70 54 73 74 5f 2d 09 50 6f 72 74 75 67 75 65 73 bf 0c 05 9f 4d 02 0b 0a, dataObjectAsString=]
----------TagTLV [tagId=80, description=APPLICATION_LABEL, dataObject=41 70 70 54 73 74, dataObjectAsString=AppTst]
----------TagTLV [tagId=24365, description=LANGUAGE_PREFERENCE, dataObject=50 6f 72 74 75 67 75 65 73, dataObjectAsString=Portugues]
----------TagTLV [tagId=48908, description=FILE_CONTROL_INFORMATION_ISSUER_DISCRETIONARY_DATA, dataObject=9f 4d 02 0b 0a, dataObjectAsString=]
--------------TagTLV [tagId=40781, description=LOG_ENTRY, dataObject=0b 0a, dataObjectAsString=]

TagTLV [tagId=40781, description=LOG_ENTRY, dataObject=0b 0a, dataObjectAsString=]
```