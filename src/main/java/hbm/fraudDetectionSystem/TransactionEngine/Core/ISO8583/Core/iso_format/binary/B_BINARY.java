package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_format.binary;

import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.interpreter.binary.LiteralBinaryInterpreter;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_handler.ISOBinaryHandler;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.prefixer.NullPrefixer;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldRuleCondition.FieldRuleCondition;

public class B_BINARY extends ISOBinaryHandler {
    public B_BINARY(int fieldId, int length, String description, FieldRuleCondition FieldRuleCondition) {
        super(fieldId, length, description, NullPrefixer.INSTANCE, LiteralBinaryInterpreter.INSTANCE, FieldRuleCondition);
    }
}
