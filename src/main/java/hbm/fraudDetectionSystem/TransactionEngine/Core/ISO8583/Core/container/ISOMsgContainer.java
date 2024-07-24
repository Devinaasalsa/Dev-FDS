package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.container;

import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Exception.ISOException;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.component.ISOBinaryField;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.component.ISOComponent;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.component.ISOMsg;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_handler.ISOBinaryHandler;

import static hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.helper.ConversionHelper.CHARSET;
import static hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.helper.LoggerHelper.setMessageLogger;

public class ISOMsgContainer extends ISOFieldContainer {
    protected ISOContainer isoContainer;
    protected ISOFieldContainer isoFieldContainer;

    public ISOMsgContainer(ISOContainer isoContainer, ISOFieldContainer isoFieldContainer) {
        super(isoFieldContainer.getFieldId(), isoFieldContainer.getLength(), isoFieldContainer.getDescription(), isoFieldContainer.getFieldCondition(), false);
        this.isoContainer = isoContainer;
        this.isoFieldContainer = isoFieldContainer;
    }

    @Override
    public int unpack(ISOComponent c, ISOComponent c1, byte[] rawData, int lastDigitIn) throws ISOException {

        ((ISOMsg) c1).setLogger(new StringBuilder());
        ISOBinaryField parentField = new ISOBinaryField(isoFieldContainer.getFieldId());

        //Unpack parent field
        int lastDigitInAfterUnpackParent = isoFieldContainer.unpack(parentField, rawData, lastDigitIn);
        String parentFieldValue = new String(parentField.getValue(), CHARSET);

        if (isoFieldContainer instanceof ISOBinaryHandler)
            setMessageLogger(c,
                    String.format(
                            "FIELD NO %s [%s] (BINARY): %s",
                            parentField.getFldNo(), parentField.getValueLength(), parentField.getASCIIValue()
                    )
            );
        else
            setMessageLogger(c,
                    String.format(
                            "FIELD NO %s [%d]: %s",
                            parentField.getFldNo(), parentFieldValue.length(), parentFieldValue
                    )
            );

        //Unpack Child
        if (parentField.getValue() != null) {
            ((ISOMsg) c1).setParentFieldNumber((Integer) parentField.getFldNo());

            if (isoContainer instanceof EMVFieldBaseContainer) {
                ((ISOMsg) c1).setParentFieldNumberValue(parentField.getASCIIValue());
                isoContainer.unpack(c, c1, parentField.getValue());
            } else {
                ((ISOMsg) c1).setParentFieldNumberValue(parentFieldValue);
                isoContainer.unpack(c, c1, parentField.getValue());
            }
        }

        return lastDigitInAfterUnpackParent;
    }

    @Override
    public ISOComponent createComponent(int fldNo) {
        ISOMsg m = new ISOMsg(fldNo);
        m.setContainer(isoContainer);
        return m;
    }

    @Override
    public byte[] pack(ISOComponent c) throws ISOException {
        return c.getBytes();
    }

    @Override
    public byte[] pack(ISOComponent container, ISOComponent c) throws ISOException {
        ISOMsg m = (ISOMsg) c;

        setMessageLogger(container,
                String.format(
                        "FIELD NO %s [%s]: ",
                        this.isoFieldContainer.getFieldId(), this.isoFieldContainer.getLength()
                )
        );
        ISOBinaryField f = new ISOBinaryField(this.isoFieldContainer.fieldId, this.isoContainer.pack(container, m));
        String parentFieldValue = new String(f.getValue(), CHARSET);
        setMessageLogger(container,
                String.format(
                        "PACKED VALUE FIELD NO %s [%s]: %s",
                        this.isoFieldContainer.fieldId, parentFieldValue.length(), parentFieldValue
                )
        );

        return this.isoFieldContainer.pack(f);
    }

    @Override
    public int unpack(ISOComponent c, byte[] rawData, int lastDigitIn) throws ISOException {
        return 0;
    }
}
