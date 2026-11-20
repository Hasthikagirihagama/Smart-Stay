package com.k2636474.smartstay.ui;

import com.k2636474.smartstay.model.K2636474Booking;
import com.k2636474.smartstay.model.enums.K2636474BookingStatus;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Table model listing bookings. Columns keep their real types (dates, numbers,
 * status) so a {@code TableRowSorter} sorts them correctly.
 */
public class K2636474BookingTableModel extends AbstractTableModel {

    private static final String[] COLUMNS =
            {"Booking", "Guest", "Room", "Type", "Check-in", "Check-out", "Nights", "Guests", "Total", "Status"};
    private static final Class<?>[] TYPES = {String.class, String.class, String.class, String.class,
            LocalDate.class, LocalDate.class, Long.class, Integer.class, Double.class, K2636474BookingStatus.class};
    /** Column index of the total amount, used to attach the money renderer. */
    public static final int TOTAL_COLUMN = 8;
    /** Column index of the status, used to attach the pill renderer. */
    public static final int STATUS_COLUMN = 9;

    /** Preferred column widths: wide for names and money, narrow for small numbers. */
    private static final int[] WIDTHS = {80, 140, 60, 100, 95, 95, 60, 60, 120, 110};

    private final List<K2636474Booking> rows = new ArrayList<>();

    /**
     * Applies the money and status renderers and sensible column widths to a table
     * showing this model.
     *
     * @param table table using a booking table model
     */
    public static void configure(JTable table) {
        for (int column = 0; column < WIDTHS.length; column++) {
            table.getColumnModel().getColumn(column).setPreferredWidth(WIDTHS[column]);
        }
        table.getColumnModel().getColumn(TOTAL_COLUMN).setCellRenderer(K2636474UiTheme.moneyRenderer());
        table.getColumnModel().getColumn(STATUS_COLUMN).setCellRenderer(new K2636474StatusPillRenderer());
    }

    /**
     * Replaces the rows.
     *
     * @param bookings bookings to show
     */
    public void setBookings(List<K2636474Booking> bookings) {
        rows.clear();
        rows.addAll(bookings);
        fireTableDataChanged();
    }

    /**
     * Returns the booking on a model row.
     *
     * @param modelRow row index in the model (convert view indexes first)
     * @return booking
     */
    public K2636474Booking getBookingAt(int modelRow) {
        return rows.get(modelRow);
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    @Override
    public Class<?> getColumnClass(int column) {
        return TYPES[column];
    }

    @Override
    public Object getValueAt(int row, int column) {
        K2636474Booking booking = rows.get(row);
        return switch (column) {
            case 0 -> booking.getBookingId();
            case 1 -> booking.getGuest().getFullName();
            case 2 -> booking.getRoom().getRoomId();
            case 3 -> booking.getRoom().getRoomType().getDisplayName();
            case 4 -> booking.getCheckInDate();
            case 5 -> booking.getCheckOutDate();
            case 6 -> booking.getNights();
            case 7 -> booking.getNumberOfGuests();
            case 8 -> booking.getInvoice() == null ? null : booking.getInvoice().getGrossTotal();
            default -> booking.getStatus();
        };
    }
}
