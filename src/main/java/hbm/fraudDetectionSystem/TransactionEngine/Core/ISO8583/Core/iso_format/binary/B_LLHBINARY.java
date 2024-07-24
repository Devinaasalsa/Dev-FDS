package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_format.binary;

import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.interpreter.binary.LiteralBinaryInterpreter;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_handler.ISOBinaryHandler;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.prefixer.BinaryPrefixer;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldRuleCondition.FieldRuleCondition;

public class B_LLHBINARY extends ISOBinaryHandler {
    public B_LLHBINARY(int fieldId, int length, String description, FieldRuleCondition FieldRuleCondition) {
        super(fieldId, length, description, BinaryPrefixer.B, LiteralBinaryInterpreter.INSTANCE, FieldRuleCondition);
        checkLength(length, 255);
    }
}
