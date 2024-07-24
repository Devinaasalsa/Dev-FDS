package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_format.ascii;

import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.interpreter.common.AsciiInterpreter;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_handler.ISOStringHandler;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.padder.NullPadder;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.prefixer.AsciiPrefixer;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldRuleCondition.FieldRuleCondition;

public class A_LLLVAR extends ISOStringHandler {
    public A_LLLVAR(int fieldId, int length, String description, FieldRuleCondition FieldRuleCondition, boolean pad) {
        super(fieldId, length, description, AsciiPrefixer.LLL, AsciiInterpreter.INSTANCE, FieldRuleCondition, NullPadder.INSTANCE, pad);
        checkLength(length, 999);
    }
}
