package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.padder;

import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Exception.ISOException;

public class LeftPadder implements Padder {
    public static final LeftPadder ZERO_PADDER = new LeftPadder('0');

    private char pad;

    public LeftPadder(char pad)
    {
        this.pad = pad;
    }

    @Override
    public String pad(String data, int maxLength, boolean isPad) throws ISOException {
        if (!isPad) {
            return data;
        }

        StringBuilder padded = new StringBuilder(maxLength);
        int len = data.length();
        if (len > maxLength)
        {
            throw new ISOException("Data is too long. Max = " + maxLength);
        } else
        {
           for (int i = maxLength - len; i > 0; i--)
            {
                padded.append(pad);
            }
            padded.append(data);
        }
        return padded.toString();
    }

    @Override
    public String unpad(String paddedData, boolean isPad) {
        if (!isPad) {
            return paddedData;
        }

        int i = 0;
        int len = paddedData.length();
        while (i < len)
        {
            if (paddedData.charAt(i) != pad)
            {
                return paddedData.substring(i);
            }
            i++;
        }
        return "";
    }
}
