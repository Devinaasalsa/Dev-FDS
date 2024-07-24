package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.container;

import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Exception.ISOException;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.component.ISOComponent;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.component.ISOField;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldRuleCondition.FieldRuleCondition;

public abstract class ISOFieldContainer {
    protected int fieldId;
    protected int length;
    protected boolean pad;
    protected String description;
    protected FieldRuleCondition fieldRuleCondition;

    public ISOFieldContainer() {
        fieldId = -1;
        length = -1;
        description = null;
    }

    public ISOFieldContainer(int fieldId, int length, String description, boolean pad) {
        this.fieldId = fieldId;
        this.length = length;
        this.description = description;
        this.fieldRuleCondition = null;
        this.pad = pad;
    }

    public ISOFieldContainer(int fieldId, int length, String description, FieldRuleCondition FieldRuleCondition, boolean pad) {
        this.fieldId = fieldId;
        this.length = length;
        this.description = description;
        this.fieldRuleCondition = FieldRuleCondition;
        this.pad = pad;
    }

    public ISOFieldContainer(int fieldId, String description) {
        this.fieldId = fieldId;
        this.length = 0;
        this.description = description;
    }

    public ISOFieldContainer(int fieldId, String description, FieldRuleCondition fieldRuleCondition) {
        this.fieldId = fieldId;
        this.length = 0;
        this.description = description;
        this.fieldRuleCondition = fieldRuleCondition;
    }

    public int getFieldId() {
        return fieldId;
    }

    public int getLength() {
        return length;
    }

    public String getDescription() {
        return description;
    }

    public FieldRuleCondition getFieldCondition() {
        return fieldRuleCondition;
    }

    public ISOComponent createComponent(int fldNo) {
        return new ISOField(fldNo);
    }

    public ISOComponent createComponent(int fldNo, Object fldValue) throws ISOException {
        throw new ISOException("N/A");
    }

    public int getTLVTagLength() throws ISOException {
        try {
            throw new ISOException("N/A");
        } catch (Exception e) {
            throw new ISOException(e.getMessage());
        }
    }

    public abstract byte[] pack(ISOComponent c) throws ISOException;

    public byte[] pack(ISOComponent container, ISOComponent c) throws ISOException {
        throw new ISOException("Method not allowed");
    }

    public abstract int unpack(ISOComponent c, byte[] rawData, int lastDigitIn) throws ISOException;

    public int unpack(ISOComponent c, ISOComponent c1, byte[] rawData, int lastDigitIn) throws ISOException {
        throw new ISOException("Method not allowed");
    }
}
