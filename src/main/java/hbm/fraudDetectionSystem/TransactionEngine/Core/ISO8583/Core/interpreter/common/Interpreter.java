package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.interpreter.common;

import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Exception.ISOException;

public interface Interpreter {

    void interpret(String data, byte[] rawData, int nDataUnits);

    String uninterpret(byte[] rawData, int lastDigitIn, int length) throws ISOException;

    int getPackedLength(int nDataUnits);
}
