package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_format.ebcdic;


import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.interpreter.common.EbcdicInterpreter;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_handler.ISOStringHandler;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.padder.NullPadder;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.prefixer.NullPrefixer;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldRuleCondition.FieldRuleCondition;

public class E_NUMERIC extends ISOStringHandler {
    public E_NUMERIC(int fieldId, int length, String description, FieldRuleCondition fieldRuleCondition, boolean pad) {
        super(fieldId, length, description, NullPrefixer.INSTANCE, EbcdicInterpreter.INSTANCE, fieldRuleCondition, NullPadder.INSTANCE, pad);
    }
}
