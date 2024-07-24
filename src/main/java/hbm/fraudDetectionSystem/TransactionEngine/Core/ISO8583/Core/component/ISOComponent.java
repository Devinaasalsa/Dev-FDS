package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.component;

import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Exception.ISOException;

import java.util.Collections;
import java.util.Map;

public abstract class ISOComponent {
    public void set (ISOComponent c) throws ISOException {

    }

    public void setHeader (ISOComponent c) throws ISOException {

    }


    public Object getFldNo() throws ISOException {
        throw new ISOException("N/A");
    }

    public Object getValue() throws ISOException {
        throw new ISOException("N/A");
    }

    public Object getValueLength() throws ISOException {
        throw new ISOException("N/A");
    }

    public Object getDescription() throws ISOException {
        throw new ISOException("N/A");
    }

    public Object getASCIIValue() throws ISOException {
        throw new ISOException("N/A");
    }

    public int getMaxField() {
        return 0;
    }

    /*
        1 = field
        2 = header
     */
    public Map<Integer, Object> getChildren(int fieldType) {
        return Collections.emptyMap();
    }

    public byte[] getBytes() throws ISOException {
        throw new ISOException("N/A");
    }

    public abstract void setValue(Object obj);

    public abstract void setDescription(String description);

    public abstract byte[] pack() throws ISOException;
    public abstract int unpack(byte[] b) throws ISOException;
}
