package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.component;

import java.math.BigInteger;

import static hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.helper.ConversionHelper.*;

public class EMVDataField {
    private final int tag;
    private final byte[] value;

    public EMVDataField(int tag, byte[] value) {
        this.tag = tag;
        this.value = value;
    }

    public String getTag() {
        return Integer.toHexString(this.tag).toUpperCase();
    }

    public String getValue() {
        return hexString(value);
    }

    public byte[] getTLV() {
        String hexTag = Integer.toHexString(this.tag);
        byte[] bTag = hex2byte(hexTag, CHARSET);

        byte[] bLen = this.getL();
        byte[] bVal = this.value;
        if (bVal == null) {
            bVal = new byte[0];
        }

        int tLength = bTag.length + bLen.length + bVal.length;
        byte[] out = new byte[tLength];
        System.arraycopy(bTag, 0, out, 0, bTag.length);
        System.arraycopy(bLen, 0, out, bTag.length, bLen.length);
        System.arraycopy(bVal, 0, out, bTag.length + bLen.length, bVal.length);
        return out;
    }

    public byte[] getL() {
        if (this.value == null) {
            return new byte[1];
        } else {
            BigInteger bi = BigInteger.valueOf(this.value.length);
            byte[] rBytes = bi.toByteArray();
            if (this.value.length >= 128) {
                if (rBytes[0] > 0) {
                    rBytes = concat(new byte[1], rBytes);
                }

                rBytes[0] = (byte) (128 | rBytes.length - 1);
            }
            return rBytes;
        }
    }

    public void dump() {
        System.out.printf(
                "Tag Id: %s value: %s%n",
                Integer.toHexString(this.tag).toUpperCase(), hexString(value)
        );
    }
}
