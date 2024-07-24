package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_handler;

import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.component.EMVDataField;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.component.ISOComponent;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.component.ISOField;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.component.ISOMsg;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.helper.ConversionHelper;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.EMVFieldConfiguration.EMVFieldConfiguration;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.*;

public class EMVDataHandler {
    private static final int SKIP_BYTE1     = 0x00;
    private static final int SKIP_BYTE2     = 0xFF;
    private static final int EXT_TAG_MASK   = 0x1F;
    private static final int LEN_SIZE_MASK  = 0x7F;
    private static final int EXT_LEN_MASK   = 0x80;

    public final List<EMVDataField> tags = new ArrayList<>();

    public EMVDataHandler() {
    }

    protected void set(EMVDataField emv) {
        Objects.requireNonNull(emv, "TLV message can't be null");
        this.tags.add(emv);
    }

    public void set(int tag, String value) {
        this.set(new EMVDataField(tag, ConversionHelper.hex2byte(value, ConversionHelper.CHARSET)));
    }

    public byte[] pack() {
        ByteBuffer buffer = ByteBuffer.allocate(516);

        for (EMVDataField emv : this.tags) {
            buffer.put(emv.getTLV());
        }

        byte[] b = new byte[buffer.position()];
        buffer.flip();
        buffer.get(b);
        return b;
    }

    public void unpack(ISOComponent c, ISOComponent c1, byte[] rawChip, Map<String, EMVFieldConfiguration> emvConfigurations) throws Exception {
        unpack(c, c1, rawChip, 0, emvConfigurations);
    }

    public void unpack(ISOComponent c, ISOComponent c1, byte[] rawChip, int offset, Map<String, EMVFieldConfiguration> emvConfigurations) throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(rawChip, offset, rawChip.length - offset);
        EMVDataField currentNode;
        while (buffer.hasRemaining()) {
            currentNode = getChipDataField(buffer);
            if (currentNode != null){
                EMVFieldConfiguration emvFieldConfiguration = emvConfigurations.get(currentNode.getTag());
                ISOComponent emvFieldComponent = new ISOField(emvFieldConfiguration.getId().intValue());
                emvFieldComponent.setDescription(emvFieldConfiguration.getDescription());
                emvFieldComponent.setValue(currentNode.getValue());
                c1.set(emvFieldComponent);
                setMessageLogger(
                        c,
                        String.format(
                                "\tEMV TAG ID %s: %s - %s",
                                currentNode.getTag(), currentNode.getValue(), emvFieldConfiguration.getDescription()
                        )
                );
            }
        }
    }

    private EMVDataField getChipDataField(ByteBuffer buffer) {
        int tag = getTAG(buffer);
        if (!buffer.hasRemaining())
            throw new IllegalArgumentException(String.format("BAD TLV FORMAT: tag (%x)"
                    + " without length or value",tag)
            );
        int length = getValueLength(buffer);
        if (length > buffer.remaining())
            throw new IllegalArgumentException(String.format("BAD TLV FORMAT: tag (%x)"
                    + " length (%d) exceeds available data", tag, length)
            );
        byte[] arrValue = new byte[length];
        buffer.get(arrValue);
        return createChipDataField(tag, arrValue);
    }

    private int getTAG(ByteBuffer buffer) {
        skipBytes(buffer);
        return readTagID(buffer);
    }

    private void skipBytes(ByteBuffer buffer) {
        buffer.mark();
        int b;
        do {
            if (!buffer.hasRemaining())
                break;

            buffer.mark();
            b = buffer.get() & 0xff;
        } while (b == SKIP_BYTE1 || b == SKIP_BYTE2);
        buffer.reset();
    }

    private int readTagID(ByteBuffer buffer) {
        int b = buffer.get() & 0xff;
        int tag = b;
        if (isExtTagByte(b)) {
            // Get rest of Tag identifier
            do {
                tag <<= 8;
                if (buffer.remaining() < 1)
                    throw new IllegalArgumentException("BAD TLV FORMAT: encoded tag id is too short");

                b = buffer.get() & 0xff;
                tag |= b;
            } while ((b & EXT_LEN_MASK) == EXT_LEN_MASK);
        }
        return tag;
    }

    private boolean isExtTagByte(int b) {
        return (b & EXT_TAG_MASK) == EXT_TAG_MASK;
    }

    protected int getValueLength(ByteBuffer buffer) {
        byte b = buffer.get();
        int count = b & LEN_SIZE_MASK;
        // check first byte for more bytes to follow
        if ((b & EXT_LEN_MASK) == 0 || count == 0)
            return count;

        //fetch rest of bytes
        byte[] bb = readBytes(buffer, count);
        return bytesToInt(bb);
    }

    private byte[] readBytes(ByteBuffer buffer, int length) {
        if (length > buffer.remaining())
            throw new IllegalArgumentException(
                    String.format("BAD TLV FORMAT: (%d) remaining bytes are not"
                                    + " enough to get tag id of length (%d)"
                            , buffer.remaining(), length
                    )
            );
        byte[] bb = new byte[length];
        buffer.get(bb);
        return bb;
    }

    private int bytesToInt(byte[] bb){
        if ((bb[0] & 0x80) > 0)
            bb = ConversionHelper.concat(new byte[1], bb);

        return new BigInteger(bb).intValue();
    }

    protected EMVDataField createChipDataField(int tag, byte[] value) {
        return new EMVDataField(tag, value);
    }

    public void setMessageLogger(ISOComponent isoMsg, String message) {
        ((ISOMsg) isoMsg).setLoggerMessage(message);
    }
}
