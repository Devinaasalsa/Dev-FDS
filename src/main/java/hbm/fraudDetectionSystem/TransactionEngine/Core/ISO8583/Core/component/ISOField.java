package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.component;

import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Exception.ISOException;

import static hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.helper.ConversionHelper.CHARSET;

public class ISOField extends ISOComponent {

    protected int fldNo;
    protected String value;
    protected String description;

    public ISOField(int fldNo) {
        this.fldNo = fldNo;
    }

    public ISOField(int fldNo, String value) {
        this.fldNo = fldNo;
        this.value = value;
    }

    @Override
    public int unpack(byte[] b) throws ISOException {
        throw new ISOException("Not Used");
    }

    @Override
    public Integer getFldNo() throws ISOException {
        return fldNo;
    }

    @Override
    public String getValue() throws ISOException {
        return value;
    }

    @Override
    public Object getValueLength() throws ISOException {
        return this.getValue().length();
    }

    @Override
    public String getDescription() throws ISOException {
        return description.toUpperCase();
    }

    public byte[] getBytes() {
        return this.value != null ? this.value.getBytes(CHARSET) : new byte[0];
    }

    @Override
    public void setValue(Object obj) {
        if (obj instanceof String) {
            value = (String) obj;
        } else value = obj.toString();
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public byte[] pack() throws ISOException {
        return new byte[0];
    }
}
