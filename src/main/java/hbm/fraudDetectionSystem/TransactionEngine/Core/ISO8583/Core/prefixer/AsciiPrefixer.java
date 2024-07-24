package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.prefixer;

public class AsciiPrefixer implements Prefixer {

    public static final AsciiPrefixer LL = new AsciiPrefixer(2);
    public static final AsciiPrefixer LLL = new AsciiPrefixer(3);

    private int nDigit = 0;

    public AsciiPrefixer(int nDigit) {
        this.nDigit = nDigit;
    }

    @Override
    public void encodeLength(int length, byte[] b) {
        int n = length;

        for (int i = this.nDigit - 1; i >= 0; --i) {
            b[i] = (byte) (n % 10 + 48);
            n /= 10;
        }

        if (n != 0) {
            //TODO: Throw Exception
        }
    }

    @Override
    public int decodeLength(byte[] b, int lastDigitIn) {
        int len = 0;
        for (int i = 0; i < nDigit; i++) {
            byte d = b[lastDigitIn + i];
            if (d < '0' || d > '9') {
                //TODO: Throw Exception, Expected Digit not Char
            }
            len = len * 10 + d - (byte) '0';
        }
        return len;
    }

    @Override
    public int getNDigitLength() {
        return nDigit;
    }
}
