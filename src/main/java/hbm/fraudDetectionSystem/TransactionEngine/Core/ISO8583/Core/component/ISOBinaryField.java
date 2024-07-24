package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.component;

import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Exception.ISOException;

import static hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.helper.ConversionHelper.CHARSET;
import static hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.helper.ConversionHelper.hexString;

public class ISOBinaryField extends ISOComponent{
    protected int fldNo;
    protected byte[] value;
    protected String description;

    public ISOBinaryField(int fldNo) {
        this.fldNo = fldNo;
    }

    public ISOBinaryField(int fldNo, byte[] value) {
        this.fldNo = fldNo;
        this.value = value;
    }

    @Override
    public void setValue(Object obj) {
        if (obj instanceof String)
            value =((String) obj).getBytes(CHARSET);
        else
            value = (byte[]) obj;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public byte[] pack() throws ISOException {
        return new byte[0];
    }

    @Override
    public byte[] getValue() throws ISOException {
        return value;
    }

    @Override
    public Integer getValueLength() throws ISOException {
        return this.getValue().length;
    }

    @Override
    public Object getFldNo() throws ISOException {
        return fldNo;
    }

    @Override
    public String getDescription() throws ISOException {
        return description.toUpperCase();
    }

    @Override
    public String getASCIIValue() throws ISOException {
        return hexString(this.getValue());
    }

    @Override
    public int unpack(byte[] b) throws ISOException {
        throw new ISOException("N/A");
    }
}
