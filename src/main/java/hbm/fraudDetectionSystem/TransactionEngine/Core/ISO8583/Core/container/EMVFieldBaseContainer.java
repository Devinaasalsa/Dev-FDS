package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.container;

import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Exception.ISOException;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.component.ISOComponent;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.component.ISOMsg;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.helper.ConversionHelper;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_handler.EMVDataHandler;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.EMVFieldConfiguration.EMVFieldConfiguration;

import java.util.Map;

import static hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.helper.LoggerHelper.setMessageLogger;

public class EMVFieldBaseContainer extends ISOContainer {
    private final Map<String, EMVFieldConfiguration> emvConfigurations;
    private final EMVDataHandler emvDataHandler;

    public EMVFieldBaseContainer(Map<String, EMVFieldConfiguration> emvConfigurations, EMVDataHandler emvDataHandler) {
        this.emvConfigurations = emvConfigurations;
        this.emvDataHandler = emvDataHandler;
    }

    @Override
    public int unpack(ISOComponent c, ISOComponent c1, byte[] rawData) throws ISOException {
        setMessageLogger(c, "[START PARSE EMV FIELD]");
        byte[] fixedRaw = ConversionHelper.hex2byte(((ISOMsg) c1).getParentFieldNumberValue().toString(), ConversionHelper.CHARSET);
        try {
            emvDataHandler.unpack(c, c1, fixedRaw, emvConfigurations);
        } catch (Exception e) {
            ConversionHelper.LOGGER(this.getClass().getName()).error(e.getMessage());
        } finally {
            setMessageLogger(c, "[PARSE EMV FIELD COMPLETED]");
        }
        return 0;
    }

    @Override
    public byte[] pack(ISOComponent container, ISOComponent c) throws ISOException {
        return c.getBytes();
    }

    @Override
    public byte[] pack(ISOComponent c) throws ISOException {
        return new byte[0];
    }

    @Override
    public int unpack(ISOComponent c, byte[] rawData) {
        return 0;
    }

    @Override
    public String getDescription() {
        return getClass().getName();
    }
}
