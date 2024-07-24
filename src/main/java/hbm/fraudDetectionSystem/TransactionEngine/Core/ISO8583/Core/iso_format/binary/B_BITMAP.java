package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_format.binary;

import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Exception.ISOException;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.component.ISOComponent;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.container.ISOBitmapContainer;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.helper.ConversionHelper;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldRuleCondition.FieldRuleCondition;

import java.util.BitSet;

public class B_BITMAP extends ISOBitmapContainer {

    public B_BITMAP(int fieldId, int length, String description, FieldRuleCondition FieldRuleCondition) {
        super(fieldId, length, description, FieldRuleCondition);
    }

    @Override
    public byte[] pack(ISOComponent c) throws ISOException {
        BitSet b = (BitSet)c.getValue();
        int len = this.getLength() >= 8 ? b.length() + 62 >> 6 << 3 : this.getLength();
        return ConversionHelper.bitSet2byte(b, len);
    }

    @Override
    public int unpack(ISOComponent c, byte[] rawData, int lastDigitIn) throws ISOException {
        BitSet bmap = ConversionHelper.byte2BitSet(rawData, lastDigitIn, getLength() << 3);
        c.setValue(bmap);

        return Math.min(getLength(), (bmap.get(1) ? 128 : 64) >> 3);
    }
}
