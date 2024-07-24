package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_format.ascii;

import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.interpreter.common.AsciiInterpreter;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_handler.ISOStringHandler;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.padder.LeftPadder;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.prefixer.NullPrefixer;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldRuleCondition.FieldRuleCondition;

public class A_NUMERIC extends ISOStringHandler {
    public A_NUMERIC(int fieldId, int length, String description, FieldRuleCondition fieldRuleCondition, boolean pad) {
        super(fieldId, length, description, NullPrefixer.INSTANCE, AsciiInterpreter.INSTANCE, fieldRuleCondition, LeftPadder.ZERO_PADDER, pad);
    }


    public A_NUMERIC(int fieldId, int length, String description, boolean pad) {
        super(fieldId, length, description, NullPrefixer.INSTANCE, AsciiInterpreter.INSTANCE, LeftPadder.ZERO_PADDER, pad);
    }
}
