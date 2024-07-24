package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.prefixer;

public class BinaryPrefixer implements Prefixer {
    public static final BinaryPrefixer B = new BinaryPrefixer(1);
    public static final BinaryPrefixer BB = new BinaryPrefixer(2);
    private int nBytes;

    public BinaryPrefixer(int nBytes) {
        this.nBytes = nBytes;
    }

    @Override
    public void encodeLength(int length, byte[] b) {
        for(int i = this.nBytes - 1; i >= 0; --i) {
            b[i] = (byte)(length & 255);
            length >>= 8;
        }
    }

    @Override
    public int decodeLength(byte[] b, int lastDigitIn) {
        int len = 0;

        for(int i = 0; i < this.nBytes; ++i) {
            len = 256 * len + (b[lastDigitIn + i] & 255);
        }

        return len;
    }

    @Override
    public int getNDigitLength() {
        return this.nBytes;
    }
}
