package vn.kis.ben10.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {
    public static String getMd5Hash(String input) {
        try {
            // Get a MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            // Update the digest with the input bytes
            md.update(input.getBytes());
            // Calculate the final hash value as a 16-byte array
            byte[] digest = md.digest();
            // Convert the byte array to a hex string
            BigInteger bigInt = new BigInteger(1, digest);
            String hashText = bigInt.toString(16);

            // Pad with zeros to ensure a 32-character output
            return String.format("%32s", hashText).replace(' ', '0');

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }
}
