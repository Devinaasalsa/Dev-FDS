package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.BitSet;
import java.util.Date;

public class ConversionHelper {
    public static final String[] hexStrings;

    static {
        hexStrings = new String[256];
        for (int i = 0; i < 256; i++ ) {
            StringBuilder d = new StringBuilder(2);
            char ch = Character.forDigit((byte)i >> 4 & 0x0F, 16);
            d.append(Character.toUpperCase(ch));
            ch = Character.forDigit((byte)i & 0x0F, 16);
            d.append(Character.toUpperCase(ch));
            hexStrings[i] = d.toString();
        }
    }

    public static final Charset CHARSET = StandardCharsets.ISO_8859_1;
    public static final Charset EBCDIC   = Charset.forName("IBM1047");

    public static Logger LOGGER(String className) {
         return LoggerFactory.getLogger(className);
    }

    public static byte[] hexRaw2Byte(String rawMessage) {
        return rawMessage.length() % 2 == 0 ?

                //run func if correct format
                hex2byte(rawMessage, CHARSET) :

                //run func if wrong format
                hex2byte("0" + rawMessage, CHARSET);
    }

    public static byte[] hex2byte(byte[] b, int offset, int len) {
        byte[] d = new byte[len];

        for (int i = 0; i < len * 2; ++i) {
            int shift = i % 2 == 1 ? 0 : 4;
            d[i >> 1] = (byte) (d[i >> 1] | Character.digit((char) b[offset + i], 16) << shift);
        }

        return d;
    }

    public static byte[] hex2byte(String s, Charset charset) {
        if (charset == null) {
            charset = StandardCharsets.ISO_8859_1;
        }

        return s.length() % 2 == 0 ? hex2byte(s.getBytes(charset), 0, s.length() >> 1) : hex2byte("0" + s, charset);
    }

    public static BitSet hex2BitSet(byte[] b, int offset, int maxBits) {
        int len = maxBits > 64 ? ((Character.digit((char) b[offset], 16) & 8) == 8 ? 128 : 64) : maxBits;
        if (len > 64 && maxBits > 128 && b.length > offset + 16 && (Character.digit((char) b[offset + 16], 16) & 8) == 8) {
            len = 192;
        }

        BitSet bmap = new BitSet(len);

        for (int i = 0; i < len; ++i) {
            int digit = Character.digit((char) b[offset + (i >> 2)], 16);
            if ((digit & 8 >> i % 4) > 0) {
                bmap.set(i + 1);
                if (i == 65 && maxBits > 128) {
                    len = 192;
                }
            }
        }
        return bmap;
    }

    public static BitSet byte2BitSet(byte[] b, int offset, int maxBits) {
        boolean b1 = (b[offset] & 128) == 128;
        boolean b65 = b.length > offset + 8 && (b[offset + 8] & 128) == 128;
        int len = maxBits > 128 && b1 && b65 ? 192 : (maxBits > 64 && b1 ? 128 : (Math.min(maxBits, 64)));
        BitSet bmap = new BitSet(len);

        for (int i = 0; i < len; ++i) {
            if ((b[offset + (i >> 3)] & 128 >> i % 8) > 0) {
                bmap.set(i + 1);
            }
        }

        return bmap;
    }

    public static String hexString(byte[] b) {
        StringBuilder d = new StringBuilder(b.length * 2);
        byte[] var2 = b;
        int var3 = b.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            byte aB = var2[var4];
            d.append(hexStrings[aB & 255]);
        }
        return d.toString();
    }

    public static void asciiToEbcdic(String s, byte[] e, int offset) {
        System.arraycopy (asciiToEbcdic(s), 0, e, offset, s.length());
    }

    public static byte[] asciiToEbcdic(String s) {
        return EBCDIC.encode(s).array();
    }

    public static String ebcdicToAscii(byte[] e, int offset, int len) {
        return EBCDIC.decode(ByteBuffer.wrap(e, offset, len)).toString();
    }

    public static String formatHexDump(byte[] array, int offset, int length) {
        final int width = 16;

        StringBuilder builder = new StringBuilder();
        for (int rowOffset = offset; rowOffset < offset + length; rowOffset += width) {
            builder
                    .append(String.format("%06d:  ", rowOffset));

            for (int index = 0; index < width; index++) {
                if (rowOffset + index < array.length) {
                    builder.append(String.format("%02x ", array[rowOffset + index]));
                } else {
                    builder.append("   ");
                }
            }

            if (rowOffset < array.length) {
                int asciiWidth = Math.min(width, array.length - rowOffset);
                builder.append("  |  ");
                builder.append(new String(array, rowOffset, asciiWidth, StandardCharsets.US_ASCII).replaceAll("[^\\x20-\\x7E]", "."));
            }

            builder.append(String.format("%n"));
        }

        return builder.toString().trim().replaceAll("\\n$", "");
    }

    public static byte[] concat (byte[] array1, byte[] array2) {
        byte[] concatArray = new byte[array1.length + array2.length];
        System.arraycopy(array1, 0, concatArray, 0, array1.length);
        System.arraycopy(array2, 0, concatArray, array1.length, array2.length);
        return  concatArray;
    }

    public static String bcd2str(byte[] b, int offset, int len, boolean padLeft) {
        StringBuilder d = new StringBuilder(len);
        int start = (len & 1) == 1 && padLeft ? 1 : 0;

        for(int i = start; i < len + start; ++i) {
            int shift = (i & 1) == 1 ? 0 : 4;
            char c = Character.forDigit(b[offset + (i >> 1)] >> shift & 15, 16);
            if (c == 'd') {
                c = '=';
            }

            d.append(Character.toUpperCase(c));
        }

        return d.toString();
    }

    public static String byte2hex(byte[] bs) {
        return byte2hex(bs, 0, bs.length);
    }

    public static String byte2hex(byte[] bs, int off, int length) {
        if (bs.length > off && bs.length >= off + length) {
            StringBuilder sb = new StringBuilder(length * 2);
            byte2hexAppend(bs, off, length, sb);
            return sb.toString();
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static byte[] bitSet2byte(BitSet b, int bytes) {
        int len = bytes * 8;
        byte[] d = new byte[bytes];

        for(int i = 0; i < len; ++i) {
            if (b.get(i + 1)) {
                d[i >> 3] = (byte)(d[i >> 3] | 128 >> i % 8);
            }
        }

        if (len > 64) {
            d[0] = (byte)(d[0] | 128);
        }

        if (len > 128) {
            d[8] = (byte)(d[8] | 128);
        }

        return d;
    }

    private static void byte2hexAppend(byte[] bs, int off, int length, StringBuilder sb) {
        if (bs.length > off && bs.length >= off + length) {
            sb.ensureCapacity(sb.length() + length * 2);

            for(int i = off; i < off + length; ++i) {
                sb.append(Character.forDigit(bs[i] >>> 4 & 15, 16));
                sb.append(Character.forDigit(bs[i] & 15, 16));
            }

        } else {
            throw new IllegalArgumentException();
        }
    }

    public static String zeropad(String s, int len) {
        return padleft(s, len, '0');
    }

    protected static String padleft(String s, int len, char c) {
        s.trim();
        StringBuilder d = new StringBuilder(len);
        int fill = len - s.length();

        while (fill-- > 0) {
            d.append(c);
        }

        d.append(s);
        return d.toString();
    }

}

