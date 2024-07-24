package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_format.binary;


import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.interpreter.common.BCDInterpreter;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_handler.ISOStringHandler;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.padder.NullPadder;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.prefixer.NullPrefixer;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldRuleCondition.FieldRuleCondition;

public class B_NUMERIC extends ISOStringHandler {
    public B_NUMERIC(int fieldId, int length, String description, FieldRuleCondition fieldRuleCondition, boolean pad) {
        super(fieldId, length, description, NullPrefixer.INSTANCE, BCDInterpreter.INSTANCE, fieldRuleCondition, NullPadder.INSTANCE, pad);
    }
}
