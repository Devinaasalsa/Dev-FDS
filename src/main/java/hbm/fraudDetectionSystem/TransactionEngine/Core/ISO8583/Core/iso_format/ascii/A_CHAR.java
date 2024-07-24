package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_format.ascii;

import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.interpreter.common.AsciiInterpreter;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_handler.ISOStringHandler;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.padder.RightTPadder;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.prefixer.NullPrefixer;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldRuleCondition.FieldRuleCondition;

public class A_CHAR extends ISOStringHandler {
    public A_CHAR(int fieldId, int length, String description, FieldRuleCondition fieldRuleCondition, boolean pad) {
        super(fieldId, length, description, NullPrefixer.INSTANCE, AsciiInterpreter.INSTANCE, fieldRuleCondition, RightTPadder.SPACE_PADDER, pad);
    }


    public A_CHAR(int fieldId, int length, String description, boolean pad) {
        super(fieldId, length, description, NullPrefixer.INSTANCE, AsciiInterpreter.INSTANCE, RightTPadder.SPACE_PADDER, pad);
    }
}
