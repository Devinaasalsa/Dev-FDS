package hbm.fraudDetectionSystem.GeneralComponent.Utility;

import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Exception.ISOException;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.SupportedMTI.SupportedMTI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import javax.script.ScriptException;
import java.io.*;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import static hbm.fraudDetectionSystem.GeneralComponent.Constant.CacheData.SCRIPT_ENGINE;


public class ServiceHelper {
    public static Map<String, SupportedMTI> LIST_MTI = new LinkedHashMap<>();

    public static SupportedMTI getMTIConfiguration(String mti) {
        return LIST_MTI.get(mti);
    }

    public static boolean doesVariableExist(String variableName, Class<?> classType) {
        Field[] fields = classType.getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equalsIgnoreCase(variableName)) {
                return true;
            }
        }
        return false;
    }

    public static void LOG(StringBuilder logger, String message) {
        logger
                .append(new SimpleDateFormat("MM-dd HH:mm:ss.SSS").format(new Date()))
                .append(" |\t")
                .append(message)
                .append("\n");
    }

    public static void LOGSEPARATOR(StringBuilder logger) {
        logger
                .append("==========================================================================================================")
                .append("\n");
    }

    public static void LOGClass(StringBuilder logger, String variableName, String value, int L) {
        logger
                .append("\t")
                .append(rightPaddingString(variableName, L))
                .append(zeroLeftPaddingString(value.length()))
                .append(": ")
                .append(value)
                .append("\n");
    }

    public static void LOGWithoutTime(StringBuilder logger, String message) {
        logger
                .append("\t")
                .append(message)
                .append("\n");
    }

    public static void LOGWithoutNewLine(StringBuilder logger, String message) {
        logger
                .append(new SimpleDateFormat("MM-dd HH:mm:ss.SSS").format(new Date()))
                .append(" |\t")
                .append(message);
    }

    public static String rightPaddingString(String input, int L) {
        return String
                .format("%" + (-L) + "s", input);
    }

    public static String leftPaddingString(String input, int L) {
        return String
                .format("%" + (L) + "s", input);
    }

    public static String zeroLeftPaddingString(int input) {
        return String
                .format("%" + (5) + "d", input)
                .replace(' ', '0');
    }

    public static void writeExceptionToLogFile(Environment logPath, Exception e, Logger LOGGER, String networkId, String configId) {
        String fileName = String.format(
                "%s/transactionEngine.exception.%s.txt",
                logPath.getProperty("exceptionLog.path"), new SimpleDateFormat("ddMMyy").format(new Date())
        );
        try (PrintStream ps = new PrintStream(new FileOutputStream(fileName, true))) {
            ps.printf(
                    "%s| \tException occurred when process data with network id: %s and config id: %s, see the detail below: %n",
                    new SimpleDateFormat("MM-dd HH:mm:ss.SSS").format(new Date()), networkId, configId
            );
            e.printStackTrace(ps);
        } catch (FileNotFoundException ex) {
            LOGGER.error(ex.getMessage());
        }
    }

    public static void writeEngineToLogFile(Environment logPath, Logger LOGGER, StringBuilder parserLog , String configId) {
        String fileName = String.format(
                "%s/transactionEngine.%s.%s.txt",
                logPath.getProperty("engineLog.path"), configId, new SimpleDateFormat("ddMMyy").format(new Date())
        );

        File file = new File(fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.append(parserLog);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public static void writeErrorEngineToLogFile(Environment logPath, Logger LOGGER, StringBuilder parserLog , String configId) {
        String fileName = String.format(
                "%s/transactionEngine.error.%s.%s.txt",
                logPath.getProperty("engineErrorLog.path"), configId, new SimpleDateFormat("ddMMyy").format(new Date())
        );

        File file = new File(fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.append(parserLog);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public static boolean checkCondition(String condition) throws ISOException {
        try {
            return (boolean) SCRIPT_ENGINE.eval(condition);
        } catch (ScriptException e) {
            throw new ISOException(e.getMessage());
        }
    }

    public static String dateFormatter() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").format(LocalDateTime.now());
    }

    public static Logger LOGGER(String className) {
        return LoggerFactory.getLogger(className);
    }
}
