package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.container;

import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Exception.ISOException;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.component.ISOComponent;

public abstract class ISOContainer {
    public abstract byte[] pack(ISOComponent c) throws ISOException;
    public  byte[] pack(ISOComponent container, ISOComponent c) throws ISOException {
        throw new ISOException("Method not allowed");
    }
    public abstract int unpack(ISOComponent c, byte[] rawData) throws ISOException;
    public abstract String getDescription();
    public int unpack(ISOComponent c, ISOComponent c1, byte[] rawData) throws ISOException {
        throw new ISOException("Method not allowed");
    }
}
