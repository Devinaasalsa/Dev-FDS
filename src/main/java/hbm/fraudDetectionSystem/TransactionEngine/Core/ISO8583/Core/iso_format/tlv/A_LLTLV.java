package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_format.tlv;


import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.container.ISOFieldContainer;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_format.ascii.A_LLVAR;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_handler.ISOTLVHandler;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldRuleCondition.FieldRuleCondition;

public class A_LLTLV extends ISOTLVHandler {
    public A_LLTLV(int fieldId, String description) {
        super(fieldId, description);
    }

    public A_LLTLV(int fieldId, int length, String description, FieldRuleCondition FieldRuleCondition) {
        super(fieldId, length, description, FieldRuleCondition);
    }

    @Override
    protected ISOFieldContainer getFieldContainer(int fieldId, int length, String description) {
        return new A_LLVAR(fieldId, length, description, null, false);
    }

    @Override
    protected int getTagLength() {
        return 2;
    }
}
