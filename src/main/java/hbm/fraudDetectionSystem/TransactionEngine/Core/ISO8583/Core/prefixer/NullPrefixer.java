package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.prefixer;

public class NullPrefixer implements Prefixer{

    public static final NullPrefixer INSTANCE = new NullPrefixer();

    public NullPrefixer() {
    }

    @Override
    public void encodeLength(int length, byte[] b) {

    }

    @Override
    public int decodeLength(byte[] b, int lastDigitIn) {
        return -1;
    }

    @Override
    public int getNDigitLength() {
        return 0;
    }
}
