package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.helper;

import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Exception.ISOException;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Enum.TypeField;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.component.ISOBinaryField;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.component.ISOComponent;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.component.ISOMsg;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.container.ISOBitmapContainer;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.container.ISOFieldContainer;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.container.ISOMsgContainer;

import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

import static hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Enum.ModeType.PACK;
import static hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Enum.ModeType.UNPACK;
import static hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Enum.TypeField.SUB_FIELD;
import static hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.helper.LoggerHelper.setMessageLogger;

public class ContainerHelper {
    public static BitSet getActiveBits(ISOFieldContainer[] fieldContainers) {
        BitSet bitsHeader = new BitSet();
        Arrays.stream(fieldContainers).forEach(data -> bitsHeader.set(data.getFieldId()));
        return bitsHeader;
    }

    public static BitSet getActiveBits(Map<Integer, Object> fieldContainers) {
        BitSet bitsHeader = new BitSet();
        fieldContainers.forEach((key, data) -> bitsHeader.set(key));
        return bitsHeader;
    }

    public static boolean isContainerHaveBitmap(ISOFieldContainer[] fieldContainer) {
        return fieldContainer[1] instanceof ISOBitmapContainer;
    }

    public static ISOFieldContainer getBitmapContainer(ISOFieldContainer[] fieldContainer) {
        return fieldContainer[1];
    }

    public static byte[] checkerPackConfigurationConditionField(ISOComponent container, ISOComponent c, List<ISOFieldContainer> filteredBitsActive, int conditionNumber, TypeField typeField) throws ISOException {
        //Check if configuration is other than 1
        if (filteredBitsActive.size() > 1) {
            conditionNumber = RuleFieldConfigurationHelper.checkConfigurationIfMoreThan1(container, null, filteredBitsActive, conditionNumber, typeField);
            return packField(container, c, filteredBitsActive.get(conditionNumber), typeField);
        }

        //Check if configuration only 1 and have condition
        else if (filteredBitsActive.size() == 1 && filteredBitsActive.get(conditionNumber).getFieldCondition() != null) {
            if (RuleFieldConfigurationHelper.checkConfigurationIfOnly1(container, null, filteredBitsActive.get(conditionNumber), typeField)) {
                return packField(container, c, filteredBitsActive.get(conditionNumber), typeField);
            } else return new byte[0];
        }

        //Run this when configuration only 1 and not have condition
        else {
            return packField(container, c, filteredBitsActive.get(conditionNumber), typeField);
        }
    }

    public static byte[] checkerPackConfigurationConditionSubField(ISOComponent c, ISOComponent c1, List<ISOFieldContainer> filteredBitsActive, int conditionNumber) throws ISOException {
        //Check if configuration is other than 1
        if (filteredBitsActive.size() > 1) {
            conditionNumber = RuleFieldConfigurationHelper.checkConfigurationIfMoreThan1(c, c1, filteredBitsActive, conditionNumber, SUB_FIELD);
            return packField(c, c1, filteredBitsActive.get(conditionNumber), SUB_FIELD);
        }

        //Check if configuration only 1 and have condition
        else if (filteredBitsActive.size() == 1 && filteredBitsActive.get(conditionNumber).getFieldCondition() != null) {
            if (RuleFieldConfigurationHelper.checkConfigurationIfOnly1(c, c1, filteredBitsActive.get(conditionNumber), SUB_FIELD)) {
                return packField(c, c1, filteredBitsActive.get(conditionNumber), SUB_FIELD);
            } else return new byte[0];
        }

        //Run this when configuration only 1 and not have condition
        else {
            return packField(c, c1, filteredBitsActive.get(conditionNumber), SUB_FIELD);
        }
    }

    public static int checkerUnpackConfigurationConditionField(ISOComponent c, byte[] rawData, List<ISOFieldContainer> filteredBitsActive, int conditionNumber, int lastDigitIn, TypeField typeField) throws ISOException {
        //Check if configuration is other than 1
        if (filteredBitsActive.size() > 1) {
            conditionNumber = RuleFieldConfigurationHelper.checkConfigurationIfMoreThan1(c, null, filteredBitsActive, conditionNumber, typeField);
            lastDigitIn += unpackField(rawData, c, filteredBitsActive.get(conditionNumber), lastDigitIn, typeField);
        }

        //Check if configuration only 1 and have condition
        else if (filteredBitsActive.size() == 1 && filteredBitsActive.get(conditionNumber).getFieldCondition() != null) {
            if (RuleFieldConfigurationHelper.checkConfigurationIfOnly1(c, null, filteredBitsActive.get(conditionNumber), typeField)) {
                lastDigitIn += unpackField(rawData, c, filteredBitsActive.get(conditionNumber), lastDigitIn, typeField);
            }
        }

        //Run this when configuration only 1 and not have condition
        else {
            lastDigitIn += unpackField(rawData, c, filteredBitsActive.get(conditionNumber), lastDigitIn, typeField);
        }

        return lastDigitIn;
    }


    public static int checkerUnpackConfigurationConditionSubField(ISOComponent c, ISOComponent c1, byte[] rawData, List<ISOFieldContainer> filteredBitsActive, int conditionNumber, int lastDigitIn) throws ISOException {
        //Check if configuration is other than 1
        if (filteredBitsActive.size() > 1) {
            conditionNumber = RuleFieldConfigurationHelper.checkConfigurationIfMoreThan1(c, c1, filteredBitsActive, conditionNumber, SUB_FIELD);
            lastDigitIn += unpackSubfield(rawData, c, c1, filteredBitsActive.get(conditionNumber), lastDigitIn);
        }

        //Check if configuration only 1 and have condition
        else if (filteredBitsActive.size() == 1 && filteredBitsActive.get(conditionNumber).getFieldCondition() != null) {
            if (RuleFieldConfigurationHelper.checkConfigurationIfOnly1(c, c1, filteredBitsActive.get(conditionNumber), SUB_FIELD)) {
                lastDigitIn += unpackSubfield(rawData, c, c1, filteredBitsActive.get(conditionNumber), lastDigitIn);
            }
        }

        //Run this when configuration only 1 and not have condition
        else {
            lastDigitIn += unpackSubfield(rawData, c, c1, filteredBitsActive.get(conditionNumber), lastDigitIn);
        }

        return lastDigitIn;
    }

    public static int checkerConfigurationConditionTLV(ISOComponent c, ISOComponent c1, List<ISOFieldContainer> filteredBitsActive) throws ISOException {
        int conditionNumber = -1;

        //Check if configuration is other than 1
        if (filteredBitsActive.size() > 1) {
            conditionNumber = RuleFieldConfigurationHelper.checkConfigurationIfMoreThan1(c, c1, filteredBitsActive, conditionNumber, SUB_FIELD);
        }

        //Check if configuration only 1 and have condition
        else if (filteredBitsActive.size() == 1 && filteredBitsActive.get(0).getFieldCondition() != null) {
            if (RuleFieldConfigurationHelper.checkConfigurationIfOnly1(c, c1, filteredBitsActive.get(0), SUB_FIELD))
                conditionNumber = 0;
        } else
            conditionNumber = 0;

        return conditionNumber;
    }

    protected static byte[] packField(ISOComponent container, ISOComponent c, ISOFieldContainer fieldConfiguration, TypeField typeField) throws ISOException {
        if (c instanceof ISOMsg && !(fieldConfiguration instanceof ISOMsgContainer))
            throw new ISOException("This fields is carrying child field, BAD FIELD CONFIGURATION");
        else {
            try {
                if (!(c instanceof ISOMsg)) {
                    switch (typeField) {
                        case FIELD:
                            setMessageLogger(container,
                                    String.format(
                                            "FIELD NO %s [%s]: %s",
                                            c.getFldNo(), c.getValueLength(), c.getValue()
                                    )
                            );
                            break;

                        case SUB_FIELD:
                            setMessageLogger(container,
                                    String.format(
                                            "\tSUB FIELD NO %s [%s]: %s",
                                            c.getFldNo(), c.getValueLength(), c.getValue()
                                    )
                            );
                            break;

                        case HEADER_FIELD:
                            setMessageLogger(container,
                                    String.format(
                                            "HEADER FIELD NO %s [%s]: %s",
                                            c.getFldNo(), c.getValueLength(), c.getValue()
                                    )
                            );
                            break;

                        case BITMAP_FIELD:
                            setMessageLogger(container,
                                    String.format(
                                            "BITS ACTIVE: %s",
                                            c.getValue()
                                    )
                            );
                            break;
                    }
                    return fieldConfiguration.pack(c);
                } else
                    return fieldConfiguration.pack(container, c);
            } catch (Exception e) {
                e.printStackTrace();
                switch (typeField) {
                    case FIELD:
                    case SUB_FIELD:
                    case BITMAP_FIELD:
                        throw new ISOException(c.getFldNo().toString(), e.getMessage(), PACK);
                    case HEADER_FIELD:
                        throw new ISOException((String) c.getFldNo(), e.getMessage(), PACK);
                    default:
                        throw e;
                }
            }
        }
    }

    protected static int unpackField(byte[] rawData, ISOComponent c, ISOFieldContainer fieldConfiguration, int lastDigitIn, TypeField typeField) throws ISOException {
        int lastDigitInAfterUnpack;

        ISOComponent fieldComponent = fieldConfiguration.createComponent(fieldConfiguration.getFieldId());
        fieldComponent.setDescription(fieldConfiguration.getDescription());

        if (fieldComponent instanceof ISOMsg) {
            lastDigitInAfterUnpack = unpackerSubfield(c, fieldComponent, fieldConfiguration, rawData, lastDigitIn);
            c.set(fieldComponent);
        } else {
            lastDigitInAfterUnpack = unpackerField(fieldComponent, fieldConfiguration, rawData, lastDigitIn);

            switch (typeField) {
                case FIELD:
                    if (fieldComponent instanceof ISOBinaryField)
                        setMessageLogger(c,
                                String.format(
                                        "FIELD NO %s [%s] (BINARY): %s",
                                        fieldComponent.getFldNo(), fieldComponent.getValueLength(), fieldComponent.getASCIIValue()
                                )
                        );
                    else
                        setMessageLogger(c,
                                String.format(
                                        "FIELD NO %s [%s]: %s",
                                        fieldComponent.getFldNo(), fieldComponent.getValueLength(), fieldComponent.getValue()
                                )
                        );

                    c.set(fieldComponent);
                    break;

                case HEADER_FIELD:
                    setMessageLogger(c,
                            String.format(
                                    "HEADER FIELD NO %s [%s]: %s",
                                    fieldComponent.getFldNo(), fieldComponent.getValueLength(), fieldComponent.getValue()
                            )
                    );
                    break;
            }
        }

        return lastDigitInAfterUnpack;
    }

    protected static int unpackSubfield(byte[] rawData, ISOComponent c, ISOComponent c1, ISOFieldContainer fieldConfiguration, int lastDigitIn) throws ISOException {
        int lastDigitInAfterUnpack;

        ISOComponent fieldComponent = fieldConfiguration.createComponent(fieldConfiguration.getFieldId());
        fieldComponent.setDescription(fieldConfiguration.getDescription());

        try {
            lastDigitInAfterUnpack = unpackerField(fieldComponent, fieldConfiguration, rawData, lastDigitIn);

            setMessageLogger(c,
                    String.format(
                            "\tSUB FIELD NO %d.%s [%s]: %s",
                            ((ISOMsg) c1).getParentFieldNumber(), fieldComponent.getFldNo(), fieldComponent.getValueLength(), fieldComponent.getValue()
                    )
            );
            c1.set(fieldComponent);
            return lastDigitInAfterUnpack;
        } catch (Exception e) {
            throw new ISOException(
                    String.format(
                            "%s.%s",
                            ((ISOMsg) c1).getParentFieldNumber(), fieldComponent.getFldNo()
                    ),
                    e.getMessage(),
                    UNPACK
            );
        }
    }

    protected static int unpackerField(ISOComponent c, ISOFieldContainer fieldConfiguration, byte[] rawData, int lastDigitIn) throws ISOException {
        return fieldConfiguration.unpack(c, rawData, lastDigitIn);
    }

    protected static int unpackerSubfield(ISOComponent c, ISOComponent fieldComponent, ISOFieldContainer fieldConfiguration, byte[] rawData, int lastDigitIn) throws ISOException {
        return fieldConfiguration.unpack(c, fieldComponent, rawData, lastDigitIn);
    }
}
