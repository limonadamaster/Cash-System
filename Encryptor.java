package com.example.loginform;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encryptor {
    public static String encryptString(String inputPassword) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");

        byte[] messageDigest = md.digest(inputPassword.getBytes());

        BigInteger bigInteger = new BigInteger(1,messageDigest);

        String hashText=bigInteger.toString(16);

        while (hashText.length() < 32) {
            hashText = "0" + hashText;
        }
        return hashText;
    }

    public static boolean checkPassword(String inputPassword, String storedHashedPassword) throws NoSuchAlgorithmException {
        // Hash the input password
        String hashedInputPassword = encryptString(inputPassword);

        // Compare the two hash values
        return hashedInputPassword.equals(storedHashedPassword);
    }

}
