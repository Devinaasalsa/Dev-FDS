package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.helper;

import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.component.ISOComponent;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.component.ISOMsg;

public class LoggerHelper {
    public static void setMessageLogger(ISOComponent isoMsg, String message) {
        ((ISOMsg) isoMsg).setLoggerMessage(message);
    }

    public static void setCalculateMessageLogger(ISOComponent isoMsg, String message) {
        ((ISOMsg) isoMsg).setLoggerMessage("Calculate: " + message);
    }

    public static String getMessageLogger(ISOComponent isoMsg) {
        return ((ISOMsg) isoMsg).getLogger().toString();
    }

    public static void setMessageLoggerWithoutNewLine(ISOComponent isoMsg, String message) {
        ((ISOMsg) isoMsg).setLoggerMessageWithoutNewLine(message);
    }

    public static String replaceNull(String text) {
        return text == null ? "" : text;
    }
}
