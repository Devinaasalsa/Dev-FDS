package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.container;

import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Exception.ISOException;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.component.ISOBitmap;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.component.ISOComponent;

import java.util.*;
import java.util.stream.Collectors;

import static hbm.fraudDetectionSystem.GeneralComponent.Utility.ServiceHelper.getMTIConfiguration;
import static hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Enum.TypeField.*;
import static hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.helper.ContainerHelper.*;
import static hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.helper.ConversionHelper.CHARSET;
import static hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.helper.LoggerHelper.setMessageLogger;

public class ISOBaseContainer extends ISOContainer {
    protected ISOFieldContainer[] headerContainer;
    protected ISOFieldContainer[] fieldContainer;

    public ISOBaseContainer(ISOFieldContainer[] headerContainer, ISOFieldContainer[] fieldContainer) {
        this.headerContainer = headerContainer;
        this.fieldContainer = fieldContainer;
    }

    @Override
    public byte[] pack(ISOComponent c) throws ISOException {
        int len = 0;
        byte[] bValue;
        byte[] packedValue = new byte[0];

        ArrayList<byte[]> listValue = new ArrayList<>();
        Map<Integer, Object> fields = c.getChildren(1);
        Map<Integer, Object> headerFields = c.getChildren(2);

        if (headerContainer != null) {
            BitSet bitsHeader = getActiveBits(headerFields);
            for (int i = bitsHeader.nextSetBit(1); i != -1; i = bitsHeader.nextSetBit(i + 1)) {
                int bitActive = i;
                int conditionNumber = 0;

                bValue = checkerPackConfigurationConditionField(c, (ISOComponent) headerFields.get(i), getFilteredBitsActive(headerContainer, bitActive), conditionNumber, HEADER_FIELD);
                len += bValue.length;
                listValue.add(bValue);
            }
        }

        ISOComponent MTI = (ISOComponent) fields.get(0);
        bValue = this.fieldContainer[0].pack(MTI);
        setMessageLogger(c, "MTI: " + MTI.getValue());

        len += bValue.length;
        listValue.add(bValue);

        if (isContainerHaveBitmap(fieldContainer)) {
            BitSet bmap = (BitSet) ((ISOBitmap) fields.get(1)).getValue();
            bValue = checkerPackConfigurationConditionField(c, (ISOComponent) fields.get(1), getFilteredBitsActive(fieldContainer, 1), 0, BITMAP_FIELD);
            len += bValue.length;
            listValue.add(bValue);

            for (int i = bmap.nextSetBit(2); i != -1; i = bmap.nextSetBit(i + 1)) {
                int bitActive = i;
                int conditionNumber = 0;

                bValue = checkerPackConfigurationConditionField(c, (ISOComponent) fields.get(i), getFilteredBitsActive(fieldContainer, bitActive), conditionNumber, FIELD);
                len += bValue.length;
                listValue.add(bValue);
            }

            int k = 0;
            packedValue = new byte[len];
            for (byte[] value : listValue) {
                System.arraycopy(value, 0, packedValue, k, value.length);
                k += value.length;
            }

        } else {
            setMessageLogger(c, "THIS MESSAGE NOT CONTAIN BITMAP");
            setMessageLogger(c, "=============STOP PARSE PROCESS=============");
        }

        return packedValue;
    }

    @Override
    public int unpack(ISOComponent c, byte[] rawData) throws ISOException {
        int lastDigitIn = 0;

        //Header Handler
        if (headerContainer != null) {
            BitSet bitsHeader = getActiveBits(headerContainer);
            for (int i = bitsHeader.nextSetBit(1); i != -1; i = bitsHeader.nextSetBit(i + 1)) {
                int bitActive = i;
                int conditionNumber = 0;

                lastDigitIn = checkerUnpackConfigurationConditionField(c, rawData, getFilteredBitsActive(headerContainer, bitActive), conditionNumber, lastDigitIn, HEADER_FIELD);
            }
        }

        //Unpack MTI
        ISOComponent mti = fieldContainer[0].createComponent(0);
        lastDigitIn += fieldContainer[0].unpack(mti, rawData, lastDigitIn);
        mti.setDescription(fieldContainer[0].getDescription());
        if (isUnpackedMTIIsListed(mti.getValue()))
            setMessageLogger(c, "MTI: " + mti.getValue());
        else {
            setMessageLogger(c, String.format("MTI %S NOT LISTED - STOP PROCESS", mti.getValue()));
            throw new ISOException(String.format("MTI %S NOT LISTED - STOP PROCESS", mti.getValue()));
        }
        c.set(mti);

        //Checking Normal Fields
        if (isContainerHaveBitmap(fieldContainer)) {
            //Unpack BITMAP
            ISOBitmap bitmap = new ISOBitmap(1);
            lastDigitIn += getBitmapContainer(fieldContainer).unpack(bitmap, rawData, lastDigitIn);
            setMessageLogger(c, "BITS ACTIVE: " + bitmap.getValue());
            c.set(bitmap);
            BitSet bmap = (BitSet) bitmap.getValue();

            //Loop all active fields in BITMAP
            for (int i = bmap.nextSetBit(2); i != -1; i = bmap.nextSetBit(i + 1)) {
                int bitActive = i;
                int conditionNumber = 0;

                try {
                    lastDigitIn = checkerUnpackConfigurationConditionField(c, rawData, getFilteredBitsActive(fieldContainer, bitActive), conditionNumber, lastDigitIn, FIELD);

                    //Checking is there remaining data that not processed yet
                    if (i + 1 == bmap.length() && lastDigitIn != rawData.length) {
                        setMessageLogger(c,
                                String.format(
                                        "THERE ARE REMAINING DATA WITH LENGTH: %d AND VALUE: %s, THAT NOT PROCESSED YET",
                                        (rawData.length - lastDigitIn), new String(rawData, CHARSET).substring(lastDigitIn)
                                )
                        );
                    }
                } catch (IndexOutOfBoundsException e) {
                    setMessageLogger(c, String.format(
                            "FIELD NO %d IS ACTIVE, BUT THERE IS NO CONFIGURATION FOR THIS FIELD",
                            i
                    ));
                    throw new ISOException(
                            String.format(
                                    "FIELD NO %d IS ACTIVE, BUT THERE IS NO CONFIGURATION FOR THIS FIELD",
                                    i
                            )
                    );
                }
            }
        } else {
            setMessageLogger(c, "THIS MESSAGE NOT CONTAIN BITMAP");
            setMessageLogger(c, "=============STOP PARSE PROCESS=============");
        }

        return lastDigitIn;
    }

    protected boolean isUnpackedMTIIsListed(Object mti) {
        return getMTIConfiguration(mti.toString()) != null;
    }

    @Override
    public String getDescription() {
        return getClass().getName();
    }

    public ISOFieldContainer getFieldContainer(int fldNo) {
        return this.fieldContainer != null && fldNo < this.fieldContainer.length ? this.fieldContainer[fldNo] : null;
    }


    public List<ISOFieldContainer> getFilteredBitsActive(ISOFieldContainer[] container, int bitActive) {
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

//    protected int getFirstField() {
//        if (!(this.fieldContainer[0] instanceof ISOMsgContainer) && this.fieldContainer.length > 1) {
//            return this.fieldContainer[1] instanceof ISOBitmapContainer ? 2 : 1;
//        } else return 0;
//    }
}

