package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.interpreter.binary;

public interface BinaryInterpreter {
    void interpret(byte[] data, byte[] rawData, int nDataUnits);
    byte[] uninterpret(byte[] rawData, int lastDigitIn, int length);
    int getPackedLength(int nDataUnits);
}
