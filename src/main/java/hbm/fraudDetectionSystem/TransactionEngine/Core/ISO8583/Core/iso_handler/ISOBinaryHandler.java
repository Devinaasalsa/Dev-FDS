package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_handler;

import hbm.fraudDetectionSystem.GeneralComponent.Exception.NDigitTooLongException;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Exception.ISOException;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.component.ISOBinaryField;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.component.ISOComponent;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.container.ISOFieldContainer;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.interpreter.binary.BinaryInterpreter;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.prefixer.Prefixer;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldRuleCondition.FieldRuleCondition;

import static hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Enum.ModeType.PACK;

public class ISOBinaryHandler extends ISOFieldContainer {
    private final BinaryInterpreter interpreter;
    private final Prefixer prefixer;

    public ISOBinaryHandler(int fieldId, int length, String description, Prefixer prefixer, BinaryInterpreter interpreter) {
        super(fieldId, length, description, false);
        this.interpreter = interpreter;
        this.prefixer = prefixer;
    }

    public ISOBinaryHandler(int fieldId, int length, String description, Prefixer prefixer, BinaryInterpreter interpreter, FieldRuleCondition FieldRuleCondition) {
        super(fieldId, length, description, FieldRuleCondition, false);
        this.interpreter = interpreter;
        this.prefixer = prefixer;
    }

    @Override
    public int unpack(ISOComponent c, byte[] rawData, int lastDigitIn) throws ISOException {
        try {
            int len = prefixer.decodeLength(rawData, lastDigitIn);
            if (len == -1) {
                len = getLength();
            } else if (getLength() > 0 && len > getLength()) {
                throw new NDigitTooLongException("Field No " + c.getFldNo() + ", length " + len + " too long. Max: " + getLength());
            }

            int nDigitLength = prefixer.getNDigitLength();
            byte[] unpackedData = interpreter.uninterpret(rawData, lastDigitIn + nDigitLength, len);
            c.setValue(unpackedData);


            return nDigitLength + interpreter.getPackedLength(len);
        } catch (Exception e) {
            throw new ISOException(e.getMessage());
        }
    }

    @Override
    public ISOComponent createComponent(int fieldNumber) {
        return new ISOBinaryField(fieldNumber);
    }

    @Override
    public byte[] pack(ISOComponent c) throws ISOException {
        byte[] data = c.getBytes();
        int packedLength = this.prefixer.getNDigitLength();
        if (packedLength == 0 && data.length != this.getLength()) {
            throw new ISOException(c.getFldNo(), "Binary data length not the same as the packager length (" + data.length + "/" + this.getLength() + ")", PACK);
        } else {
            byte[] ret = new byte[this.interpreter.getPackedLength(data.length) + packedLength];
            this.prefixer.encodeLength(data.length, ret);
            this.interpreter.interpret(data, ret, packedLength);
            return ret;
        }
    }

    protected void checkLength(int len, int maxLength) throws IllegalArgumentException {
        if (len > maxLength) {
            throw new IllegalArgumentException("Length " + len + " too long for " + this.getClass().getName());
        }
    }
}
