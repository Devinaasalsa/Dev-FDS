package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.prefixer;

public class EbcdicPrefixer implements Prefixer {
    public static final EbcdicPrefixer L = new EbcdicPrefixer(1);
    public static final EbcdicPrefixer LL = new EbcdicPrefixer(2);
    public static final EbcdicPrefixer LLL = new EbcdicPrefixer(3);
    private static final byte[] EBCDIC_DIGITS = {(byte)0xF0, (byte)0xF1, (byte)0xF2,
            (byte)0xF3, (byte)0xF4, (byte)0xF5, (byte)0xF6, (byte)0xF7, (byte)0xF8,
            (byte)0xF9};

    private final int nDigits;

    public EbcdicPrefixer(int nDigits)
    {
        this.nDigits = nDigits;
    }

    @Override
    public void encodeLength(int length, byte[] b) {
        for (int i = nDigits - 1; i >= 0; i--)
        {
            b[i] = EBCDIC_DIGITS[length % 10];
            length /= 10;
        }
    }

    @Override
    public int decodeLength(byte[] b, int lastDigitIn) {
        int len = 0;
        for (int i = 0; i < nDigits; i++)
        {
            len = len * 10 + (b[lastDigitIn + i] & 0x0F);
        }
        return len;
    }

    @Override
    public int getNDigitLength() {
        return nDigits;
    }
}
