package com.zybooks.weighttrackingemmanuelrivera;

import org.mindrot.jbcrypt.BCrypt;

public class SecurityUtils {
    public static String hashPassword(String plaintextPassword) {
        return BCrypt.hashpw(plaintextPassword, BCrypt.gensalt());
    }
    public static boolean verifyPassword(String plaintextPassword, String hashedPassword) {
        try {
            return BCrypt.checkpw(plaintextPassword, hashedPassword);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}