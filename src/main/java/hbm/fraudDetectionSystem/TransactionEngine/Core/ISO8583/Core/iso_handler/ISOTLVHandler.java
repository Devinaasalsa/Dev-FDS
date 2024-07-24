package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_handler;

import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Exception.ISOException;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.component.ISOBinaryField;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.component.ISOComponent;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.component.ISOField;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.container.ISOFieldContainer;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.tag_mapper.LLDecimalTagMapper;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.tag_mapper.LLLDecimalTagMapper;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.tag_mapper.TagMapper;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.helper.ConversionHelper;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldRuleCondition.FieldRuleCondition;

public abstract class ISOTLVHandler extends ISOFieldContainer {
    private TagMapper tagMapper;
    private ISOFieldContainer fieldContainer;

    public ISOTLVHandler() {

    }

    public ISOTLVHandler(int fieldId, String description) {
        super(fieldId, description);
    }

    public ISOTLVHandler(int fieldId, int length, String description, FieldRuleCondition fieldRuleCondition) {
        super(fieldId, length, description, fieldRuleCondition, false);
    }

    @Override
    public byte[] pack(ISOComponent c) throws ISOException {
        String tag = this.getTagMapper().getTagForField((Integer) c.getFldNo());
        byte[] tagBytes = tag.getBytes(ConversionHelper.CHARSET);
        byte[] value = this.getFieldContainer().pack(c);
        byte[] packed = new byte[tagBytes.length + value.length];
        System.arraycopy(tagBytes, 0, packed, 0, tagBytes.length);
        System.arraycopy(value, 0, packed, tagBytes.length, value.length);

        return packed;
    }

    @Override
    public int unpack(ISOComponent c, byte[] rawData, int lastDigitIn) throws ISOException {
        int consumed;
        byte[] tagBytes = new byte[this.getTagLength()];
        System.arraycopy(rawData, lastDigitIn, tagBytes, 0, this.getTagLength());
        String tag = new String(tagBytes, ConversionHelper.CHARSET);

        if (!(c instanceof ISOField) && !(c instanceof ISOBinaryField)) {
            throw new IllegalArgumentException("This is not Parent field component");
        } else {
            Integer fldNo = this.getTagMapper().getFieldNumberForTag(tag);
            if (fldNo == null || fldNo < 0) {
                throw new IllegalArgumentException("no field mapping found for tag: " + tag);
            } else {
                if (isTLVLengthIsOutOfRange(rawData.length, lastDigitIn, this.getTagLength())) {
                    byte[] tagLengthBytes = new byte[this.getTagLength()];
                    System.arraycopy(rawData, lastDigitIn + tagBytes.length, tagLengthBytes, 0, this.getTagLength());
                    String tagLength = new String(tagLengthBytes, ConversionHelper.CHARSET);

                    if ((rawData.length - lastDigitIn) > Integer.parseInt(tagLength)) {
                        consumed = this.getTagLength() + this.getFieldContainer().unpack(c, rawData, lastDigitIn + tagBytes.length);
                    } else {
                        throw new ISOException("Tag length is out of range.");
                    }
                } else {
                    throw new ISOException("Tag length is out of range.");
                }
            }
        }
        return consumed;
    }

    private synchronized ISOFieldContainer getFieldContainer() {
        if (this.fieldContainer == null) {
            this.fieldContainer = this.getFieldContainer(this.getFieldId(), this.getLength(), this.getDescription());
        }

        return this.fieldContainer;
    }

    private boolean isTLVLengthIsOutOfRange(int rawDataLength, int lastDigitIn, int tagLength) {
        return (rawDataLength - (lastDigitIn + tagLength)) > tagLength;
    }

    public TagMapper getTagMapper() {
        if (getTagLength() == 2) tagMapper = new LLDecimalTagMapper();
        else if (getTagLength() == 3) tagMapper = new LLLDecimalTagMapper();
        return tagMapper;
    }

    @Override
    public int getTLVTagLength() {
        return this.getTagLength();
    }

    protected abstract ISOFieldContainer getFieldContainer(int fieldId, int length, String description);

    protected abstract int getTagLength();
}
