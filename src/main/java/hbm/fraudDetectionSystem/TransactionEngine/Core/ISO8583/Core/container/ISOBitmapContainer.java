package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.container;

import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.component.ISOBitmap;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.component.ISOComponent;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldRuleCondition.FieldRuleCondition;

public abstract class ISOBitmapContainer extends ISOFieldContainer {

    public ISOBitmapContainer() {
        super();
    }

    public ISOBitmapContainer(int fieldId, int length, String description) {
        super(fieldId, length, description, false);
    }

    public ISOBitmapContainer(int fieldId, int length, String description, FieldRuleCondition FieldRuleCondition) {
        super(fieldId, length, description, FieldRuleCondition, false);
    }

    @Override
    public ISOComponent createComponent(int fldNo) {
        return new ISOBitmap(fldNo);
    }
}
