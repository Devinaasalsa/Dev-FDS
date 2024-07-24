package hbm.fraudDetectionSystem.GeneralComponent.Utility;

import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.container.ISOFieldContainer;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_format.ascii.*;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_format.binary.*;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_format.ebcdic.E_LLLBINARY;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_format.ebcdic.E_LLLVAR;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_format.ebcdic.E_NUMERIC;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_format.tlv.A_LLLTLV;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.iso_format.tlv.A_LLTLV;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldRuleCondition.FieldRuleCondition;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.SubFieldConfiguration.SubFieldConfiguration;
import org.slf4j.Logger;
import org.springframework.core.env.Environment;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class SpringLogicHelper {
    public static final StringBuilder startupLogger = new StringBuilder();

    public static String removeUnusefulExceptionString(String errorMessage) {
        String fixedMessage = errorMessage.substring(errorMessage.indexOf("Detail: ")).replace("Detail: ", "").replace("\"", "");
        if (fixedMessage.contains("null")) {
            fixedMessage = "Value can't be null.";
        }
        return fixedMessage;
    }

    public static boolean isDataNotBlank(String data) {
        return !data.isBlank();
    }

    public static String convertNull2Blank(String data) {
        return data != null ? data : "";
    }

    public static int convertNullInt2Zero(Integer data) {
        return data != null ? data : 0;
    }

    public static long convertNullLong2Zero(Long data) {
        return data != null ? data : 0L;
    }

    public static ISOFieldContainer mappingToInternalASCIIFormat(long fieldFormat, int fieldId, int length, String description, FieldRuleCondition FieldRuleCondition, boolean pad) {
        switch ((int) fieldFormat) {
            case 1:
                return new A_NUMERIC(fieldId, length, description, FieldRuleCondition, pad);

            case 2:
                return new A_LLVAR(fieldId, length, description, FieldRuleCondition, pad);

            case 3:
                return new A_LLLVAR(fieldId, length, description, FieldRuleCondition, pad);

            case 4:
                return new A_BITMAP(fieldId, length, description, FieldRuleCondition);

            case 5:
                return new A_BINARY(fieldId, length, description, FieldRuleCondition);

            case 11:
                return new A_CHAR(fieldId, length, description, FieldRuleCondition, pad);

            default:
                throw new IllegalArgumentException(String.format("Format %s is unknown.", fieldFormat));
        }
    }

    public static ISOFieldContainer mappingToInternalBINARYFormat(long fieldFormat, int fieldId, int length, String description, FieldRuleCondition FieldRuleCondition, boolean pad) {
        switch ((int) fieldFormat) {
            case 1:
                return new B_NUMERIC(fieldId, length, description, FieldRuleCondition, pad);

            case 2:
                return new B_LLVAR(fieldId, length, description, FieldRuleCondition, pad);

            case 3:
                return new B_LLLVAR(fieldId, length, description, FieldRuleCondition, pad);

            case 4:
                return new B_BITMAP(fieldId, length, description, FieldRuleCondition);

            case 5:
                return new B_BINARY(fieldId, length, description, FieldRuleCondition);

            case 8:
                return new B_LLHVAR(fieldId, length, description, FieldRuleCondition, pad);

            case 9:
                return new B_LLHBINARY(fieldId, length, description, FieldRuleCondition);

            default:
                throw new IllegalArgumentException(String.format("Format %s is unknown.", fieldFormat));
        }
    }

    public static ISOFieldContainer mappingToInternalEBCDICFormat(long fieldFormat, int fieldId, int length, String description, FieldRuleCondition FieldRuleCondition, boolean pad) {
        switch ((int) fieldFormat) {
            case 1:
                return new E_NUMERIC(fieldId, length, description, FieldRuleCondition, pad);

            case 2:
                return new B_LLVAR(fieldId, length, description, FieldRuleCondition, pad);

            case 3:
                return new E_LLLVAR(fieldId, length, description, FieldRuleCondition, pad);

            case 4:
                return new B_BITMAP(fieldId, length, description, FieldRuleCondition);

            case 5:
                return new B_BINARY(fieldId, length, description, FieldRuleCondition);

            case 8:
                return new B_LLHVAR(fieldId, length, description, FieldRuleCondition, pad);

            case 9:
                return new B_LLHBINARY(fieldId, length, description, FieldRuleCondition);

            case 10:
                return new E_LLLBINARY(fieldId, length, description, FieldRuleCondition);

            default:
                throw new IllegalArgumentException(String.format("Format %s is unknown.", fieldFormat));
        }
    }

    public static ISOFieldContainer mappingToInternalASCIITLVFormat(long fieldFormat, int fieldId, int length, String description, FieldRuleCondition fieldRuleCondition) {
        switch ((int) fieldFormat) {
            case 6:
                return new A_LLTLV(fieldId, length, description, fieldRuleCondition);

            case 7:
                return new A_LLLTLV(fieldId, length, description, fieldRuleCondition);

            default:
                throw new IllegalArgumentException(String.format("Format %s is unknown.", fieldFormat));
        }
    }

    public static ISOFieldContainer handlerMapper(int encoding, long fieldFormat, int fieldId, int length, String description, FieldRuleCondition FieldRuleCondition, boolean pad) {
        switch (encoding) {
            case 1:
                return mappingToInternalASCIIFormat(fieldFormat, fieldId, length, description, FieldRuleCondition, pad);

            case 2:
                return mappingToInternalBINARYFormat(fieldFormat, fieldId, length, description, FieldRuleCondition, pad);

            case 3:
                return mappingToInternalASCIITLVFormat(fieldFormat, fieldId, length, description, FieldRuleCondition);

            case 4:
                return mappingToInternalEBCDICFormat(fieldFormat, fieldId, length, description, FieldRuleCondition, pad);

            default:
                throw new IllegalArgumentException("encoding not listed.");
        }
    }

    public static List<SubFieldConfiguration> getChildConstraint(List<SubFieldConfiguration> data) {
        return data.stream()
                .collect(groupingBy(SubFieldConfiguration::uniqueAttributes))
                .entrySet().stream()
                .filter(n -> n.getValue().size() > 1)
                .flatMap(e->e.getValue().stream())
                .collect(toList());
    }
}
