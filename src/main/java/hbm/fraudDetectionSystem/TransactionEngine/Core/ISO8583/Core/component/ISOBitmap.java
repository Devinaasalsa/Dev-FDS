package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.component;

import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Exception.ISOException;

import java.util.BitSet;

public class ISOBitmap extends ISOComponent {
    protected int fldNo;
    protected BitSet value;
    protected String description;

    public ISOBitmap(int fldNo) {
        this.fldNo = fldNo;
    }

    public ISOBitmap(int fldNo, BitSet value) {
        this.fldNo = fldNo;
        this.value = value;
    }

    @Override
    public void setValue(Object obj) {
        value = (BitSet) obj;
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
    public Object getFldNo() throws ISOException {
        return fldNo;
    }

    @Override
    public Object getValue() throws ISOException {
        return value;
    }

    @Override
    public int unpack(byte[] b) throws ISOException {
        return 0;
    }
}
