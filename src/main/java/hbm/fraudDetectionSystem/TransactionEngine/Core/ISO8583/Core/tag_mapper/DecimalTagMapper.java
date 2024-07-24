package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.tag_mapper;

import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.helper.ConversionHelper;

public class DecimalTagMapper implements TagMapper {
    protected final int tagSize;
    protected final int UN_DECIMAL_RADIX = 10;

    public DecimalTagMapper(int tagSize) {
        this.tagSize = tagSize;
    }

    @Override
    public String getTagForField(int subFldNo) {
        if (subFldNo == 0) {
            throw new IllegalArgumentException("Can't pack tag with no: 0");
        } else {
            String sfn = Integer.toString(subFldNo);
            String ret = leftZeroPad(sfn, this.tagSize);
            return ret;
        }
    }

    @Override
    public Integer getFieldNumberForTag(String tag) {
        if (tag == null) {
            throw new IllegalArgumentException("handler for tag if null");
        } else if (tag.length() != this.tagSize){
            throw new IllegalArgumentException("handler if tag size not same");
        } else {
            try {
                return Integer.parseUnsignedInt(tag, UN_DECIMAL_RADIX);
            } catch (NumberFormatException e) {
                throw new NumberFormatException(String.format("subtag '%s' cannot be converted to integer value", tag));
            }
        }
    }

    protected static String leftZeroPad(String s, int len) {
        return ConversionHelper.zeropad(s, len);
    }
}
