package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_handler;

import hbm.fraudDetectionSystem.GeneralComponent.Exception.NDigitTooLongException;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.padder.Padder;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Exception.ISOException;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.component.ISOComponent;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.container.ISOFieldContainer;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.interpreter.common.Interpreter;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.prefixer.Prefixer;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.helper.ConversionHelper;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldRuleCondition.FieldRuleCondition;

public class ISOStringHandler extends ISOFieldContainer {
    private final Prefixer prefixer;
    private final Interpreter interpreter;
    private final Padder padder;

    public ISOStringHandler(int fieldId, int length, String description, Prefixer prefixer, Interpreter interpreter, Padder padder, boolean pad) {
        super(fieldId, length, description, pad);
        this.prefixer = prefixer;
        this.interpreter = interpreter;
        this.padder = padder;
    }

    public ISOStringHandler(int fieldId, int length, String description, Prefixer prefixer, Interpreter interpreter, FieldRuleCondition fieldRuleCondition, Padder padder, boolean pad) {
        super(fieldId, length, description, fieldRuleCondition, pad);
        this.prefixer = prefixer;
        this.interpreter = interpreter;
        this.padder = padder;
    }

    @Override
    public byte[] pack(ISOComponent c) throws ISOException {
        String data;
        if (c.getValue() instanceof byte[]) {
            data = new String((byte[]) c.getValue(), ConversionHelper.CHARSET);
        } else {
            data = (String) c.getValue();
        }

        if (data.length() > this.getLength()) {
            throw new ISOException("Field length " + data.length() + " too long. Max: " + this.getLength());
        } else {
            String paddedData = padder.pad(data, getLength(), pad);
            byte[] rawData = new byte[this.prefixer.getNDigitLength() + this.interpreter.getPackedLength(paddedData.length())];
            this.prefixer.encodeLength(paddedData.length(), rawData);
            this.interpreter.interpret(paddedData, rawData, this.prefixer.getNDigitLength());
            return rawData;
        }
    }

    @Override
    public int unpack(ISOComponent c, byte[] rawData, int lastDigitIn) throws ISOException {
        int len = prefixer.decodeLength(rawData, lastDigitIn);
        if (len == -1)
            len = getLength();
        else if (getLength() > 0 && len > getLength())
            throw new ISOException("Field No " + c.getFldNo() + ", length " + len + " too long. Max: " + getLength());

        int nDigitLength = prefixer.getNDigitLength();
        String unpackedData = interpreter.uninterpret(rawData, lastDigitIn + nDigitLength, len);

        //Data that was set is already clear without padding
        String clearData = padder.unpad(unpackedData, pad);
        c.setValue(clearData);

        return nDigitLength + interpreter.getPackedLength(len);
    }

    @Override
    public int unpack(ISOComponent c, ISOComponent c1, byte[] rawData, int lastDigitIn) throws ISOException {
        try {
            int len = prefixer.decodeLength(rawData, lastDigitIn);
            if (len == -1) {
                len = getLength();
            } else if (getLength() > 0 && len > getLength())
                throw new NDigitTooLongException("Field No " + c1.getFldNo() + ", length " + len + " too long. Max: " + getLength());

            int nDigitLength = prefixer.getNDigitLength();
            String unpackedData = interpreter.uninterpret(rawData, lastDigitIn + nDigitLength, len);

            //Data that was set is already clear without padding
            String clearData = padder.unpad(unpackedData, pad);
            c1.setValue(clearData);

            return nDigitLength + interpreter.getPackedLength(len);
        } catch (NDigitTooLongException e) {
            throw new ISOException(e.getMessage());
        } catch (Exception e) {
            throw new ISOException(
                    String.format(
                            "Problem unpacking field no: %s.%s: %s",
                            c.getFldNo(), c1.getFldNo(), e.getMessage()
                    )
            );
        }
    }

    protected void checkLength(int len, int maxLength) {
        if (len > maxLength) {
            throw new IllegalArgumentException("Length " + len + " too long for " + getClass().getName());
        }
    }
}
