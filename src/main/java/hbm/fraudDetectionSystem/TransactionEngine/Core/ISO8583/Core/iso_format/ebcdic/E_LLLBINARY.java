package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_format.ebcdic;


import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.interpreter.binary.LiteralBinaryInterpreter;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_handler.ISOBinaryHandler;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.prefixer.EbcdicPrefixer;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldRuleCondition.FieldRuleCondition;

public class E_LLLBINARY extends ISOBinaryHandler {
    public E_LLLBINARY(int fieldId, int length, String description, FieldRuleCondition FieldRuleCondition) {
        super(fieldId, length, description, EbcdicPrefixer.LLL, LiteralBinaryInterpreter.INSTANCE, FieldRuleCondition);
        checkLength(length, 999);
    }
}
