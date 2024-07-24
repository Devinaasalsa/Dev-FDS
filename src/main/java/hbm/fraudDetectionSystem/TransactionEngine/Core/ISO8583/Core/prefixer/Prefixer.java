package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.prefixer;

public interface Prefixer {
    void encodeLength(int length, byte[] b);
    int decodeLength(byte[] b, int lastDigitIn);
    int getNDigitLength();
}
