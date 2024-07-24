package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.interpreter.binary;

public class LiteralBinaryInterpreter implements BinaryInterpreter {
    public static final LiteralBinaryInterpreter INSTANCE = new LiteralBinaryInterpreter();

    public LiteralBinaryInterpreter() {
    }

    @Override
    public void interpret(byte[] data, byte[] rawData, int nDataUnits) {
        System.arraycopy(data, 0, rawData, nDataUnits, data.length);
    }

    @Override
    public byte[] uninterpret(byte[] rawData, int lastDigitIn, int length) {
        byte[] ret = new byte[length];
        System.arraycopy(rawData, lastDigitIn, ret, 0, length);
        return ret;
    }

    @Override
    public int getPackedLength(int nDataUnits) {
        return nDataUnits;
    }
}
