package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_format.ascii;

import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.interpreter.common.AsciiInterpreter;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_handler.ISOStringHandler;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.padder.NullPadder;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.prefixer.AsciiPrefixer;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldRuleCondition.FieldRuleCondition;

public class A_LLVAR extends ISOStringHandler {
    public A_LLVAR(int fieldId, int length, String description, FieldRuleCondition fieldRuleCondition, boolean pad) {
        super(fieldId, length, description, AsciiPrefixer.LL, AsciiInterpreter.INSTANCE, fieldRuleCondition, NullPadder.INSTANCE, pad);
        checkLength(length, 99);
    }
}
