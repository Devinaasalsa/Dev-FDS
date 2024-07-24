package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Exception;

import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Enum.ModeType;

public class ISOException extends Exception {

    public ISOException(String msg) {
        super(msg);
    }

    public ISOException(String fldNumber, String message, ModeType modeType) {
        super(
                modeType == ModeType.PACK ?
                        String.format(
                                "Problem packing field no: %s, detail: %s",
                                fldNumber, message
                        ) :
                        String.format(
                                "Problem unpacking field no: %s, detail: %s",
                                fldNumber, message
                        )
        );
    }

    public ISOException(int fldNumber, String message) {
        super(
                String.format(
                        "Problem unpacking field no: %s, detail: %s",
                        fldNumber, message
                )
        );
    }

    public ISOException(Object headerField, String message, ModeType modeType) {
        super(
                modeType == ModeType.PACK ?
                        String.format(
                                "Problem packing header field no: %s, detail: %s",
                                headerField, message
                        ) :
                        String.format(
                                "Problem unpacking header field no: %s, detail: %s",
                                headerField, message
                        )
        );
    }
}
