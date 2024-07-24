package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.padder;

import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Exception.ISOException;

public interface Padder {
    String pad(String data, int maxLength, boolean isPad) throws ISOException;

    String unpad(String paddedData, boolean isPad);
}
