package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.padder;

import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Exception.ISOException;

public class RightPadder implements Padder {
    public static final RightPadder SPACE_PADDER = new RightPadder(' ');

    private char pad;

    public RightPadder(char pad)
    {
        this.pad = pad;
    }

    @Override
    public String pad(String data, int maxLength, boolean isPad) throws ISOException {
        if (!isPad) {
            return data;
        }

        int len = data.length();

        if (len < maxLength) {
            StringBuilder padded = new StringBuilder(maxLength);
            padded.append(data);
            for (; len < maxLength; len++) {
                padded.append(pad);
            }
            data = padded.toString();
        }
        else if (len > maxLength) {
            throw new ISOException("Data is too long. Max = " + maxLength);
        }
        return data;
    }

    @Override
    public String unpad(String paddedData, boolean isPad) {
        if (!isPad) {
            return paddedData;
        }

        int len = paddedData.length();
        for (int i = len; i > 0; i--)
        {
            if (paddedData.charAt(i - 1) != pad)
            {
                return paddedData.substring(0, i);
            }
        }
        return "";
    }
}
