package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_format.ebcdic;


import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.interpreter.common.EbcdicInterpreter;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_handler.ISOStringHandler;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.padder.NullPadder;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.prefixer.EbcdicPrefixer;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldRuleCondition.FieldRuleCondition;

public class E_LLLVAR extends ISOStringHandler {
    public E_LLLVAR(int fieldId, int length, String description, FieldRuleCondition FieldRuleCondition, boolean pad) {
        super(fieldId, length, description, EbcdicPrefixer.LLL, EbcdicInterpreter.INSTANCE, FieldRuleCondition, NullPadder.INSTANCE, pad);
        checkLength(length, 999);
    }
}
