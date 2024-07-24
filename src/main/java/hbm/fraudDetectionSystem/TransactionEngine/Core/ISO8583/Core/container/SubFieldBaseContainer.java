package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.container;

import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Exception.ISOException;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.component.ISOComponent;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.component.ISOMsg;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.helper.ConversionHelper;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_format.ascii.A_NUMERIC;

import java.util.*;
import java.util.stream.Collectors;

import static hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.helper.ContainerHelper.*;
import static hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.helper.LoggerHelper.setMessageLogger;

public class SubFieldBaseContainer extends ISOContainer {
    protected ISOFieldContainer[] fieldContainer;

    public SubFieldBaseContainer(ISOFieldContainer[] fieldContainer) {
        this.fieldContainer = fieldContainer;
    }

    @Override
    public int unpack(ISOComponent c, ISOComponent c1, byte[] rawData) throws ISOException {
        int lastDigitIn = 0;
        BitSet bitSubField = getActiveBits(fieldContainer);
        for (int i = bitSubField.nextSetBit(1); i != -1; i = bitSubField.nextSetBit(i + 1)) {
            int bitActive = i;
            int conditionNumber = 0;

            lastDigitIn = checkerUnpackConfigurationConditionSubField(c, c1, rawData, getFilteredBitsActive(fieldContainer, bitActive), conditionNumber, lastDigitIn);
        }

        //Checking if there is remaining data in subField
        //TODO: Need to refactor this based on db configuration for extra data field, is it want to be shown as field or just warning notification
        if (lastDigitIn != rawData.length) {
            if (((ISOMsg) c1).getParentFieldNumber() > 0) {
                ISOFieldContainer extraFieldContainer = new A_NUMERIC(bitSubField.stream().max().getAsInt() + 1, 999, "EXTRA DATA", false);
                ISOComponent extraComponent = extraFieldContainer.createComponent(extraFieldContainer.getFieldId());
                extraComponent.setDescription(extraFieldContainer.getDescription());
                extraComponent.setValue(new String(rawData, ConversionHelper.CHARSET).substring(lastDigitIn));
                setMessageLogger(c,
                        String.format(
                                "\tSUB FIELD NO %d.%s [%s]: %s - (EXTRA DATA)",
                                ((ISOMsg) c1).getParentFieldNumber(), extraComponent.getFldNo(), extraComponent.getValueLength(), extraComponent.getValue()
                        )
                );
            }
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

        BitSet bitSubField = getActiveBits(fields);
//        BitSet bitSubField = getActiveBits(fieldContainer);

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

    protected List<ISOFieldContainer> getFilteredBitsActive(ISOFieldContainer[] container, int bitActive) throws ISOException {
        List<ISOFieldContainer> filteredBitsActive = Arrays.stream(container)
                .filter(data -> bitActive == data.getFieldId())
                .collect(Collectors.toList());

        if (filteredBitsActive.size() > 0) {
            return filteredBitsActive;
        } else
            throw new ISOException(
                    String.format(
                            "FIELD NO %d IS ACTIVE, BUT THERE IS NO CONFIGURATION FOR THIS FIELD",
                            bitActive
                    )
            );
    }
}
