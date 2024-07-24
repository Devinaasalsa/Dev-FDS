package hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Core.Utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class JSONHashHelper {
    public static String unhashBase64(String value) {
        byte[] decrypt = Base64.getDecoder().decode(value);
        return new String(decrypt, StandardCharsets.UTF_8);
    }

    public static String hashSHA256(String value) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = messageDigest.digest(value.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String maskString(String inputString, int clearDigit) {
        if (inputString == null || inputString.length() <= clearDigit) {
            return inputString;
        }

        int visibleChars = clearDigit; // Number of characters to keep visible at the end
        int maskedLength = inputString.length() - visibleChars;

        // Replace the characters from index 0 to maskedLength with asterisks
        String maskedPart = inputString.substring(0, maskedLength).replaceAll(".", "*");

        // Concatenate the masked part with the last visible characters
        String maskedString = maskedPart + inputString.substring(maskedLength);

        return maskedString;
    }
}
