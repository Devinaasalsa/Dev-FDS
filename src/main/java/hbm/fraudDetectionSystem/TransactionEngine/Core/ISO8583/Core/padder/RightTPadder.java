package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.padder;

import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Exception.ISOException;

public class RightTPadder extends RightPadder {
    public static final RightTPadder SPACE_PADDER = new RightTPadder(' ');

    public RightTPadder(char pad) {
        super(pad);
    }

    public String pad(String data, int maxLength, boolean isPad) throws ISOException
    {
        if (data.length() > maxLength)
        {
            return super.pad(data.substring(0,maxLength), maxLength, isPad);
        } else
        {
            return super.pad(data, maxLength, isPad);
        }
    }
}
