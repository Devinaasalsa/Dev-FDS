package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.padder;

public class NullPadder implements Padder {
    public static final NullPadder INSTANCE = new NullPadder();

    @Override
    public String pad(String data, int maxLength, boolean isPad) {
        return data;
    }

    @Override
    public String unpad(String paddedData, boolean isPad) {
        return paddedData;
    }
}
