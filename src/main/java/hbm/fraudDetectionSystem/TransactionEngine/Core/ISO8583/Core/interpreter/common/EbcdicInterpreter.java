package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.interpreter.common;

import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.helper.ConversionHelper;

public class EbcdicInterpreter implements Interpreter {

    public static final EbcdicInterpreter INSTANCE = new EbcdicInterpreter();

    @Override
    public void interpret(String data, byte[] rawData, int nDataUnits) {

    }

    @Override
    public String uninterpret(byte[] rawData, int lastDigitIn, int length) {
        return ConversionHelper.ebcdicToAscii(rawData, lastDigitIn, length);
    }

    @Override
    public int getPackedLength(int nDataUnits) {
        return nDataUnits;
    }
}
