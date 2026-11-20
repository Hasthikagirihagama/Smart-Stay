package com.k2636474.smartstay.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Display formatting for money, percentages and dates. Values are kept at full
 * precision internally and only rounded to two decimals here, for display.
 */
public final class K2636474Format {

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static final DateTimeFormatter FRIENDLY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("EEE d MMM yyyy", Locale.UK);

    private static final String MONEY_PATTERN = "#,##0.00";
    private static final String PERCENT_PATTERN = "0.##";

    private K2636474Format() {
    }

    /**
     * Formats an amount in Sri Lankan rupees.
     *
     * @param amount amount in LKR
     * @return text such as "LKR 12,345.00"
     */
    public static String lkr(double amount) {
        return "LKR " + amount(amount);
    }

    /**
     * Formats an amount with thousands separators and two decimals, without currency.
     *
     * @param amount amount in LKR
     * @return text such as "12,345.00"
     */
    public static String amount(double amount) {
        // DecimalFormat is not thread-safe, so a fresh instance is created per call.
        DecimalFormat format = new DecimalFormat(MONEY_PATTERN, DecimalFormatSymbols.getInstance(Locale.US));
        // Negative zero (e.g. "−advance" when no advance is deducted) would otherwise print as "-0.00".
        return format.format(amount == 0 ? 0 : amount);
    }

    /**
     * Formats a fractional rate as a percentage.
     *
     * @param rate rate as a fraction (0.12)
     * @return text such as "12%"
     */
    public static String percent(double rate) {
        DecimalFormat format = new DecimalFormat(PERCENT_PATTERN, DecimalFormatSymbols.getInstance(Locale.US));
        return format.format(rate * 100) + "%";
    }

    /**
     * Formats a date as yyyy-MM-dd.
     *
     * @param date the date, may be null
     * @return formatted date, or an empty string for null
     */
    public static String date(LocalDate date) {
        return date == null ? "" : DATE_FORMAT.format(date);
    }

    /**
     * Formats a date for reading, e.g. "Mon 24 Nov 2026".
     *
     * @param date the date, may be null
     * @return formatted date, or an empty string for null
     */
    public static String friendlyDate(LocalDate date) {
        return date == null ? "" : FRIENDLY_DATE_FORMAT.format(date);
    }

    /**
     * Formats a date-time as yyyy-MM-dd HH:mm.
     *
     * @param dateTime the date-time, may be null
     * @return formatted date-time, or an empty string for null
     */
    public static String dateTime(LocalDateTime dateTime) {
        return dateTime == null ? "" : DATE_TIME_FORMAT.format(dateTime);
    }
}
