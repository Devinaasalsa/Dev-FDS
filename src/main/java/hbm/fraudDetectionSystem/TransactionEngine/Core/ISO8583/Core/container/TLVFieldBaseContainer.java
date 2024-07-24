package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.container;

import hbm.fraudDetectionSystem.GeneralComponent.Exception.TLVLengthIsOutOfRangeException;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Exception.ISOException;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.component.ISOComponent;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.component.ISOMsg;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.helper.ConversionHelper;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_format.tlv.A_LLLTLV;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_format.tlv.A_LLTLV;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.helper.ContainerHelper.*;
import static hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.helper.LoggerHelper.setMessageLogger;

public class TLVFieldBaseContainer extends ISOContainer {
    protected ISOFieldContainer[] fieldContainer;

    public TLVFieldBaseContainer(ISOFieldContainer[] fieldContainer) {
        this.fieldContainer = fieldContainer;
    }

    @Override
    public int unpack(ISOComponent c, ISOComponent c1, byte[] rawData) throws ISOException {
        int lastDigitIn = 0;
        List<ISOComponent> fields = new LinkedList<>();
        Map<Integer, List<ISOFieldContainer>> container = new HashMap<>();

        while (lastDigitIn != rawData.length) {
            try {
                int finalLastDigitIn = lastDigitIn;
                AtomicInteger tagSize = new AtomicInteger();
                List<ISOFieldContainer> containerFilteredByTagId = Arrays.stream(fieldContainer)
                        .filter(data -> {
                            try {
                                tagSize.set(data.getTLVTagLength());
                                return data.fieldId == this.getTagId(rawData, data.getTLVTagLength(), finalLastDigitIn);
                            } catch (ISOException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .collect(Collectors.toList());

                if (containerFilteredByTagId.size() == 0) {
                    ISOFieldContainer unlistedContainer;
                    int unlistedTagId = this.getTagId(rawData, tagSize.get(), finalLastDigitIn);
                    switch (tagSize.get()) {
                        case 2:
                            unlistedContainer = new A_LLTLV(unlistedTagId, "UNLISTED TAG");
                            break;

                        case 3:
                            unlistedContainer = new A_LLLTLV(unlistedTagId, "UNLISTED TAG");
                            break;

                        default:
                            throw new TLVLengthIsOutOfRangeException("The tag size is not listed in configuration.");
                    }

                    ISOComponent unlistedFieldComponent = unlistedContainer.createComponent(unlistedTagId);
                    unlistedFieldComponent.setDescription(unlistedContainer.getDescription());
                    lastDigitIn += unlistedContainer.unpack(unlistedFieldComponent, rawData, lastDigitIn);
                    setTlvField(fields, unlistedFieldComponent);
                    container.put((Integer) unlistedFieldComponent.getFldNo(), containerFilteredByTagId);
                } else {
                    ISOFieldContainer firstTlvContainer = containerFilteredByTagId.get(0);
                    ISOComponent tlvFieldComponent = firstTlvContainer.createComponent(firstTlvContainer.getFieldId());
                    tlvFieldComponent.setDescription(firstTlvContainer.getDescription());
                    lastDigitIn += firstTlvContainer.unpack(tlvFieldComponent, rawData, lastDigitIn);
                    setTlvField(fields, tlvFieldComponent);
                    container.put((Integer) tlvFieldComponent.getFldNo(), containerFilteredByTagId);
                }
            } catch (IllegalArgumentException e) {
                setMessageLogger(c,
                        String.format(
                                "FIELD NO %d %s",
                                ((ISOMsg) c).getParentFieldNumber(), e.getMessage()
                        )
                );
                break;
            } catch (TLVLengthIsOutOfRangeException e) {
                setMessageLogger(c,
                        String.format(
                                "THERE ARE REMAINING DATA WITH LENGTH: %d AND VALUE: %s WHEN UNPACKING FIELD NO %d",
                                (rawData.length - lastDigitIn), new String(rawData, ConversionHelper.CHARSET).substring(lastDigitIn), ((ISOMsg) c).getParentFieldNumber()
                        )
                );
                break;
            }
        }

        for (ISOComponent field : fields) {
            List<ISOFieldContainer> listFieldContainer = container.get(field.getFldNo());
            int conditionNumber = checkerConfigurationConditionTLV(c, c1, listFieldContainer);
            if (listFieldContainer.size() == 0) {
                setMessageLogger(c,
                        String.format(
                                "FIELD NO %d.%s [%s]: %s",
                                ((ISOMsg) c1).getParentFieldNumber(), field.getFldNo(), field.getValueLength(), field.getValue()
                        )
                );
            } else if (conditionNumber == -1) {
                setMessageLogger(c,
                        String.format(
                                "FIELD NO %d.%s [%s]: %s - UNLISTED TAG",
                                ((ISOMsg) c1).getParentFieldNumber(), field.getFldNo(), field.getValueLength(), field.getValue()
                        )
                );
            } else {
                setMessageLogger(c,
                        String.format(
                                "FIELD NO %d.%s [%s]: %s",
                                ((ISOMsg) c1).getParentFieldNumber(), field.getFldNo(), field.getValueLength(), field.getValue()
                        )
                );
            }

            c1.set(field);
        }
        return lastDigitIn;
    }

    @Override
    public byte[] pack(ISOComponent c) throws ISOException {
        return new byte[0];
    }

    @Override
    public byte[] pack(ISOComponent container, ISOComponent c) throws ISOException {
        int len = 0;
        byte[] bValue;
        byte[] packedValue;
        ArrayList<byte[]> listValue = new ArrayList<>();
        Map<Integer, Object> fields = c.getChildren(1);

        ((ISOMsg) c).createBitmap(-1);
        BitSet bitSubField = (BitSet) ((ISOMsg) c).getValue(-1);

        for (int i = bitSubField.nextSetBit(1); i != -1; i = bitSubField.nextSetBit(i + 1)) {
            int bitActive = i;
            int conditionNumber = 0;

            bValue = checkerPackConfigurationConditionSubField(container, (ISOComponent) fields.get(i), getFilteredBitsActive(fieldContainer, bitActive), conditionNumber);
            len += bValue.length;
            listValue.add(bValue);
        }

        int k = 0;
        packedValue = new byte[len];
        for (byte[] value : listValue) {
            System.arraycopy(value, 0, packedValue, k, value.length);
            k += value.length;
        }

        return packedValue;
    }

    @Override
    public int unpack(ISOComponent c, byte[] rawData) throws ISOException {
        return 0;
    }

    @Override
    public String getDescription() {
        return getClass().getName();
    }

    protected int getTagId(byte[] rawData, int tagLength, int lastDigitIn) {
        byte[] tagBytes = new byte[tagLength];
        System.arraycopy(rawData, lastDigitIn, tagBytes, 0, tagLength);
        return Integer.parseInt(new String(tagBytes, ConversionHelper.CHARSET));
    }

    protected void setTlvField(List<ISOComponent> fields, ISOComponent c) {
        if (c != null) {
            fields.add(c);
        }
    }

    protected List<ISOFieldContainer> getFilteredBitsActive(ISOFieldContainer[] container, int bitActive) {
        List<ISOFieldContainer> filteredBitsActive = Arrays.stream(container)
                .filter(data -> bitActive == data.getFieldId())
                .collect(Collectors.toList());

        if (filteredBitsActive.size() > 0) {
            return filteredBitsActive;
        } else
            throw new IndexOutOfBoundsException(
                    String.format(
                            "FIELD NO %d IS ACTIVE, BUT THERE IS NO CONFIGURATION FOR THIS FIELD",
                            bitActive
                    )
            );
    }
}
