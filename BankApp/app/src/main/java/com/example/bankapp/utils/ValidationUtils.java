package com.example.bankapp.utils;

public class ValidationUtils {

    public static boolean isValidAccountNumber(String bankAccountNumber) {
        if (bankAccountNumber == null || bankAccountNumber.length() != 26) {
            return false;
        }

        return bankAccountNumber.matches("\\d{26}");
    }

    public static String formatAccountNumber(String bankAccountNumber) {
        if (bankAccountNumber.length() != 26) return bankAccountNumber;

        return bankAccountNumber.replaceAll("(\\d{2})(\\d{4})(\\d{4})(\\d{4})(\\d{4})(\\d{4})(\\d{4})",
                "$1 $2 $3 $4 $5 $6 $7");
    }

    public static String maskAccountNumber(String bankAccountNumber) {
        if (bankAccountNumber.length() != 26) return bankAccountNumber;

        return "**" + bankAccountNumber.substring(22);
    }

}
