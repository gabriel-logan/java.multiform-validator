package io.github.multiform_validator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Pattern;

public class Validator {
    private static final String INPUT_VALUE_CANNOT_BE_EMPTY = "Input value cannot be empty.";

    private Validator() {
        throw new IllegalStateException("Utility class");
    }

    // ##############################################################################################################
    // ##############################################################################################################
    // ##############################################################################################################
    // isAscii

    /**
     * Checks if the given string contains only ASCII characters.
     *
     * @param value the string to be checked
     * @return true if the string contains only ASCII characters, false otherwise
     * @throws IllegalArgumentException if the input value is null or empty
     */
    public static boolean isAscii(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException(INPUT_VALUE_CANNOT_BE_EMPTY);
        }

        return value.chars().allMatch(c -> c < 128);
    }

    // ##############################################################################################################
    // ##############################################################################################################
    // ##############################################################################################################
    // isBase64

    /**
     * Checks if the given string is a valid Base64 encoded string.
     *
     * @param value the string to be checked
     * @return true if the string is a valid Base64 encoded string, false otherwise
     * @throws IllegalArgumentException if the input value is null or empty
     */
    public static boolean isBase64(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException(INPUT_VALUE_CANNOT_BE_EMPTY);
        }

        return value.matches("^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$");
    }

    // ##############################################################################################################
    // ##############################################################################################################
    // ##############################################################################################################
    // isCEP

    /**
     * Checks if the given string is a valid CEP (Brazilian postal code).
     *
     * @param cep the string to be checked
     * @return true if the string is a valid CEP, false otherwise
     */
    public static boolean isCEP(String cep) {
        if (cep.length() < 8 || cep.length() > 10) {
            return false;
        }

        final String cepString = cep.replaceAll("\\D", "");

        if (cepString.length() != 8) {
            return false;
        }

        try {
            Integer.parseInt(cepString);
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    // ##############################################################################################################
    // ##############################################################################################################
    // ##############################################################################################################
    // isDate

    private static final List<DateTimeFormatter> DATE_FORMATTERS = Arrays.asList(
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),
            DateTimeFormatter.ofPattern("dd.MM.yyyy"),
            DateTimeFormatter.ofPattern("yyyy.MM.dd"),
            DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("dd-MMMM-yyyy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("dd-MMM-yy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("dd-MMMM-yy", Locale.ENGLISH)
    );

    private static final List<DateTimeFormatter> DATE_TIME_FORMATTERS = Arrays.asList(
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"),
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("dd-MMMM-yyyy HH:mm:ss", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("dd-MMM-yy HH:mm:ss", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("dd-MMMM-yy HH:mm:ss", Locale.ENGLISH)
    );

    /**
     * Checks if the given string is a valid date.
     * The date can be in the following formats:
     *
     * @param dateStr the string to be checked
     * @return true if the string is a valid date, false otherwise
     */
    public static boolean isDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            throw new IllegalArgumentException("Date string cannot be null or empty");
        }

        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            if (isValidFormat(dateStr, formatter, false)) {
                return true;
            }
        }

        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            if (isValidFormat(dateStr, formatter, true)) {
                return true;
            }
        }

        return false;
    }

    private static boolean isValidFormat(String dateStr, DateTimeFormatter formatter, boolean isDateTime) {
        try {
            if (isDateTime) {
                LocalDateTime.parse(dateStr, formatter);
            } else {
                LocalDate.parse(dateStr, formatter);
            }
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    // ##############################################################################################################
    // ##############################################################################################################
    // ##############################################################################################################
    // isDecimal

    /**
     * Checks if the given string is a valid decimal number.
     *
     * @param value the string to be checked
     * @return true if the string is a valid decimal number, false otherwise
     * @throws IllegalArgumentException if the input value is null or empty
     */
    public static boolean isDecimal(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException(INPUT_VALUE_CANNOT_BE_EMPTY);
        }

        try {
            double parsedValue = Double.parseDouble(value);

            return parsedValue % 1 != 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // ##############################################################################################################
    // ##############################################################################################################
    // ##############################################################################################################
    // isEmail

    /**
     * Checks if the given string is a valid email address.
     *
     * @param email the string to be checked
     * @return true if the string is a valid email address, false otherwise
     * @throws NullPointerException if the email is null
     */
    public static boolean isEmail(String email) {
        if (email == null) {
            throw new NullPointerException("Email cannot be null");
        }

        final Pattern startsWithSpecialChar = Pattern.compile("^[^a-zA-Z]");

        if (startsWithSpecialChar.matcher(email).find()) {
            return false;
        }

        final Pattern regex = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

        if (!regex.matcher(email).find()) {
            return false;
        }

        final int beforeAt = email.indexOf("@");
        final int afterAt = email.indexOf("@") + 1;
        final int afterLastDot = email.lastIndexOf(".");

        if (Character.isDigit(email.charAt(afterAt))) {
            return false;
        }

        if (Character.isDigit(email.charAt(afterLastDot))) {
            return false;
        }

        if (email.substring(0, beforeAt).contains("..")) {
            return false;
        }

        if (email.substring(0, beforeAt).endsWith(".")) {
            return false;
        }

        final String[] parts = email.split("\\.");

        if (parts.length > 2 && parts[parts.length - 2].equals(parts[parts.length - 3])) {
            return false;
        }

        // Check if there is more than one @
        if (email.split("@").length - 1 > 1) {
            return false;
        }

        if (email.substring(afterAt).contains("..")) {
            return false;
        }

        String[] domainParts = email.split("@")[1].split("\\.");
        Set<String> uniqueDomainParts = new HashSet<>(Arrays.asList(domainParts));

        return domainParts.length == uniqueDomainParts.size();
    }

    // ##############################################################################################################
    // ##############################################################################################################
    // ##############################################################################################################
    // isMACAddress

    /**
     * Checks if the given string is a valid MAC address.
     *
     * @param macAddress the string to be checked
     * @return true if the string is a valid MAC address, false otherwise
     * @throws IllegalArgumentException if the input value is null or empty
     */
    public static boolean isMACAddress(String macAddress) {
        if (macAddress == null || macAddress.isEmpty()) {
            throw new IllegalArgumentException(INPUT_VALUE_CANNOT_BE_EMPTY);
        }

        return macAddress.matches("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$");
    }

    // ##############################################################################################################
    // ##############################################################################################################
    // ##############################################################################################################
    // isMD5

    /**
     * Checks if the given string is a valid MD5 hash.
     *
     * @param value the string to be checked
     * @return true if the string is a valid MD5 hash, false otherwise
     * @throws IllegalArgumentException if the input value is null or empty
     */
    public static boolean isMD5(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException(INPUT_VALUE_CANNOT_BE_EMPTY);
        }

        return value.matches("^[a-fA-F0-9]{32}$");
    }

    // ##############################################################################################################
    // ##############################################################################################################
    // ##############################################################################################################
    // isNumber

    /**
     * Checks if the given string is a valid number.
     *
     * @param value the string to be checked
     * @return true if the string is a valid number, false otherwise
     * @throws IllegalArgumentException if the input value is null or empty
     */
    public static boolean isNumber(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException(INPUT_VALUE_CANNOT_BE_EMPTY);
        }

        return value.matches("^-?\\d+$");
    }

    // ##############################################################################################################
    // ##############################################################################################################
    // ##############################################################################################################
    // isPort

    /**
     * Checks if the given port number is valid.
     *
     * @param port the port number to be checked
     * @return true if the port number is valid, false otherwise
     */
    public static boolean isPort(int port) {
        return port >= 0 && port <= 65535;
    }

    /**
     * Checks if the given string is a valid port number.
     *
     * @param port the string to be checked
     * @return true if the string is a valid port number, false otherwise
     * @throws IllegalArgumentException if the input value is null or empty
     */
    public static boolean isPort(String port) {
        if (port == null || port.isEmpty()) {
            throw new IllegalArgumentException(INPUT_VALUE_CANNOT_BE_EMPTY);
        }

        try {
            final int portNumber = Integer.parseInt(port);
            return portNumber >= 0 && portNumber <= 65535;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // ##############################################################################################################
    // ##############################################################################################################
    // ##############################################################################################################
    // isPostalCode

    /**
     * Checks if the given string is a valid postal code.
     *
     * @param postalCode the string to be checked
     * @return true if the string is a valid postal code, false otherwise
     * @throws IllegalArgumentException if the input value is null or empty
     */
    public static boolean isPostalCode(String postalCode) {
        if (postalCode == null || postalCode.isEmpty()) {
            throw new IllegalArgumentException("Input value must be a string.");
        }

        final String REGEX_TEST1 = "^\\d{5}(-\\d{4})?$"; // US ZIP code
        final String REGEX_TEST2 = "^[A-Za-z]\\d[A-Za-z] \\d[A-Za-z]\\d$"; // Canada postal code
        final String REGEX_TEST3 = "^[A-Za-z]{1,2}\\d[A-Za-z\\d]? \\d[A-Za-z]{2}$"; // UK postal code
        final String REGEX_TEST4 = "^\\d{5}$"; // France, Spain, Italy, Germany, US postal code
        final String REGEX_TEST5 = "^\\d{4}$"; // Netherlands, South Africa, Switzerland postal code
        final String REGEX_TEST6 = "^\\d{3}-\\d{4}$"; // Japan postal code
        final String REGEX_TEST7 = "^\\d{5}-\\d{3}$"; // Brazil postal code

        return postalCode.matches(REGEX_TEST1) || postalCode.matches(REGEX_TEST2) || postalCode.matches(REGEX_TEST3)
                || postalCode.matches(REGEX_TEST4) || postalCode.matches(REGEX_TEST5) || postalCode.matches(REGEX_TEST6)
                || postalCode.matches(REGEX_TEST7);
    }

    // ##############################################################################################################
    // ##############################################################################################################
    // ##############################################################################################################
    // isTime

    /**
     * Checks if the given string is a valid time in the format "HH:mm:ss" or "HH:mm:ss a".
     *
     * @param time the string to be checked
     * @return true if the string is a valid time, false otherwise
     * @throws IllegalArgumentException if the input value is null or empty
     */
    public static boolean isTime(String time) {
        if (time == null || time.isEmpty()) {
            throw new IllegalArgumentException(INPUT_VALUE_CANNOT_BE_EMPTY);
        }

        return time.matches("^(?:2[0-3]|1\\d|0?\\d):[0-5]\\d(?::[0-5]\\d)?(?: [APap][Mm])?$");
    }
}