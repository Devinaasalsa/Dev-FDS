package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.interpreter.binary;

import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.helper.ConversionHelper;

import static hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.helper.ConversionHelper.hex2byte;

public class AsciiHexInterpreter implements BinaryInterpreter {
    public static final AsciiHexInterpreter INSTANCE = new AsciiHexInterpreter();
    private static final byte[] HEX_ASCII = new byte[]{48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70};

    public AsciiHexInterpreter() {
    }

    @Override
    public void interpret(byte[] data, byte[] rawData, int nDataUnits) {
        for(int i = 0; i < data.length; ++i) {
            rawData[nDataUnits + i * 2] = HEX_ASCII[(data[i] & 240) >> 4];
            rawData[nDataUnits + i * 2 + 1] = HEX_ASCII[data[i] & 15];
        }
    }

    @Override
    public byte[] uninterpret(byte[] rawData, int lastDigitIn, int length) {
        return ConversionHelper.hex2byte(rawData, lastDigitIn, length);
    }

    @Override
    public int getPackedLength(int nDataUnits) {
        return nDataUnits * 2;
    }
}
