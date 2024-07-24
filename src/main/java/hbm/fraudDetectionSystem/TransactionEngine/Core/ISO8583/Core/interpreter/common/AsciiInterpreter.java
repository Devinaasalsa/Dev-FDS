package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.interpreter.common;

import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Exception.ISOException;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.helper.ConversionHelper;

public class AsciiInterpreter implements Interpreter {

    public static final AsciiInterpreter INSTANCE = new AsciiInterpreter();

    @Override
    public void interpret(String data, byte[] rawData, int nDataUnits) {
        System.arraycopy(data.getBytes(ConversionHelper.CHARSET), 0, rawData, nDataUnits, data.length());
    }

    @Override
    public String uninterpret(byte[] rawData, int lastDigitIn, int length) throws ISOException {
        byte[] ret = new byte[length];
        try {
            System.arraycopy(rawData, lastDigitIn, ret, 0, length);
            return new String(ret, ConversionHelper.CHARSET);
        } catch (IndexOutOfBoundsException e) {
            throw new ISOException(
                    String.format("Data length configuration is %d for this field, but just got %d", length, rawData.length - lastDigitIn)
            );
        }
    }

    @Override
    public int getPackedLength(int nDataUnits) {
        return nDataUnits;
    }
}
