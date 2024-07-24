package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_format.ascii;

import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.interpreter.binary.AsciiHexInterpreter;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_handler.ISOBinaryHandler;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.prefixer.NullPrefixer;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldRuleCondition.FieldRuleCondition;

public class A_BINARY extends ISOBinaryHandler {

    public A_BINARY(int fieldId, int length, String description, FieldRuleCondition FieldRuleCondition) {
        super(fieldId, length, description, NullPrefixer.INSTANCE, AsciiHexInterpreter.INSTANCE, FieldRuleCondition);
    }
}
