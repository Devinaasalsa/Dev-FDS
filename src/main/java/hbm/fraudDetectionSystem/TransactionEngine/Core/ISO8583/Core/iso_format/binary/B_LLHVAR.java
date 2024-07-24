package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_format.binary;


import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.interpreter.common.BCDInterpreter;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_handler.ISOStringHandler;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.padder.NullPadder;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.prefixer.BinaryPrefixer;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldRuleCondition.FieldRuleCondition;

public class B_LLHVAR extends ISOStringHandler {
    public B_LLHVAR(int fieldId, int length, String description, FieldRuleCondition FieldRuleCondition, boolean pad) {
        super(fieldId, length, description, BinaryPrefixer.B, BCDInterpreter.INSTANCE, FieldRuleCondition, NullPadder.INSTANCE, pad);
        checkLength(length, 255);
    }
}
