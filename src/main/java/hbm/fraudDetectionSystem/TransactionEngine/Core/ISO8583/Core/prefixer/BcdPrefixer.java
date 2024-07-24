package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.prefixer;

public class BcdPrefixer implements Prefixer {
    public static final BcdPrefixer L = new BcdPrefixer(1);
    public static final BcdPrefixer LL = new BcdPrefixer(2);
    public static final BcdPrefixer LLL = new BcdPrefixer(3);
    private int nDigits;

    public BcdPrefixer(int nDigits) {
        this.nDigits = nDigits;
    }

    @Override
    public void encodeLength(int length, byte[] b) {
        for(int i = this.getNDigitLength() - 1; i >= 0; --i) {
            int twoDigits = length % 100;
            length /= 100;
            b[i] = (byte)((twoDigits / 10 << 4) + twoDigits % 10);
        }
    }

    @Override
    public int decodeLength(byte[] b, int lastDigitIn) {
        int len = 0;

        for(int i = 0; i < (this.nDigits + 1) / 2; ++i) {
            len = 100 * len + ((b[lastDigitIn + i] & 240) >> 4) * 10 + (b[lastDigitIn + i] & 15);
        }

        return len;
    }

    @Override
    public int getNDigitLength() {
        return this.nDigits + 1 >> 1;
    }
}
