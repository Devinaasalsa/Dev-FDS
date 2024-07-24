package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_format.binary;


import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.interpreter.common.AsciiInterpreter;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_handler.ISOStringHandler;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.padder.NullPadder;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.prefixer.BcdPrefixer;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldRuleCondition.FieldRuleCondition;

public class B_LLLVAR extends ISOStringHandler {
    public B_LLLVAR(int fieldId, int length, String description, FieldRuleCondition fieldRuleCondition, boolean pad) {
        super(fieldId, length, description, BcdPrefixer.LLL, AsciiInterpreter.INSTANCE, fieldRuleCondition, NullPadder.INSTANCE, pad);
        checkLength(length, 999);
    }
}
