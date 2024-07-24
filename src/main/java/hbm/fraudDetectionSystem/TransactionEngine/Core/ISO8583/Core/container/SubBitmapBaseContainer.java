package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.container;

import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Exception.ISOException;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.component.ISOBitmap;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.component.ISOComponent;

import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.stream.Collectors;

import static hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.helper.ContainerHelper.checkerUnpackConfigurationConditionSubField;
import static hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.helper.ConversionHelper.CHARSET;
import static hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.helper.LoggerHelper.setMessageLogger;

public class SubBitmapBaseContainer extends ISOContainer {
    protected ISOFieldContainer[] fieldContainer;

    public SubBitmapBaseContainer(ISOFieldContainer[] fieldContainer) {
        this.fieldContainer = fieldContainer;
    }

    @Override
    public int unpack(ISOComponent c, ISOComponent c1, byte[] rawData) throws ISOException {
        int lastDigitIn = 0;

        //Unpack Sub Bitmap
        ISOBitmap bitmap = new ISOBitmap(0);
        lastDigitIn += fieldContainer[0].unpack(bitmap, rawData, lastDigitIn);
        setMessageLogger(c, "BITS ACTIVE: " + bitmap.getValue());
        c1.set(bitmap);
        BitSet bmap = (BitSet) bitmap.getValue();

        for (int i = bmap.nextSetBit(1); i != -1; i = bmap.nextSetBit(i + 1)) {
            int bitActive = i;
            int conditionNumber = 0;

            List<ISOFieldContainer> filteredBitsActive = Arrays.stream(fieldContainer)
                    .filter(data -> bitActive == data.getFieldId())
                    .collect(Collectors.toList());

            try {
                lastDigitIn = checkerUnpackConfigurationConditionSubField(c, c1, rawData, filteredBitsActive, conditionNumber, lastDigitIn);

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
                throw new ISOException(
                        String.format(
                                "FIELD NO %d IS ACTIVE, BUT THERE IS NO CONFIGURATION FOR THIS FIELD",
                                i
                        )
                );
            }
        }

        return lastDigitIn;
    }

    @Override
    public byte[] pack(ISOComponent c) {
        return new byte[0];
    }

    @Override
    public int unpack(ISOComponent c, byte[] rawData) throws ISOException {
        return 0;
    }

    @Override
    public String getDescription() {
        return null;
    }
}
