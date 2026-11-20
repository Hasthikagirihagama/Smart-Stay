package com.k2636474.smartstay.ui;

import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

/**
 * Date picker built on a {@link JSpinner} with a {@link SpinnerDateModel}
 * (format yyyy-MM-dd), exposing {@link LocalDate} so the rest of the code never
 * deals with {@link Date}.
 */
public class K2636474DateField extends JSpinner {

    private static final String PATTERN = "yyyy-MM-dd";

    /**
     * Creates a date field showing a date.
     *
     * @param initial date to show first
     */
    public K2636474DateField(LocalDate initial) {
        super(new SpinnerDateModel(toDate(initial), null, null, Calendar.DAY_OF_MONTH));
        DateEditor editor = new DateEditor(this, PATTERN);
        editor.getTextField().setColumns(9);
        setEditor(editor);
    }

    /**
     * Returns the selected date.
     *
     * @return selected date
     */
    public LocalDate getDate() {
        Date value = (Date) getValue();
        return value.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Shows a date.
     *
     * @param date date to show
     */
    public void setDate(LocalDate date) {
        setValue(toDate(date));
    }

    private static Date toDate(LocalDate date) {
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
