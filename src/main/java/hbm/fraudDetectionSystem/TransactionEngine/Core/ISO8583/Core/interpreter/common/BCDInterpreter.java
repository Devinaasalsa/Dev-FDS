package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.interpreter.common;

import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.helper.ConversionHelper;

public class BCDInterpreter implements Interpreter {
    public static final BCDInterpreter INSTANCE = new BCDInterpreter();

    @Override
    public void interpret(String data, byte[] rawData, int nDataUnits) {

    }

    @Override
    public String uninterpret(byte[] rawData, int lastDigitIn, int length) {
        return ConversionHelper.bcd2str(rawData, lastDigitIn, length, false);
    }

    @Override
    public int getPackedLength(int nDataUnits) {
        return (nDataUnits + 1) / 2;
    }
}
