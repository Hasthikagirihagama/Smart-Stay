package com.k2636474.smartstay.ui;

import com.k2636474.smartstay.model.K2636474Booking;
import com.k2636474.smartstay.util.K2636474Format;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * The guest's own bookings with Modify, Cancel and View Invoice. Modify and Cancel
 * are disabled (with a tooltip giving the reason) when the 24-hour rule or the
 * booking's status does not allow changes.
 */
public class K2636474MyBookingsPage extends K2636474Page {

    private final K2636474UiContext context;
    private final K2636474BookingTableModel model = new K2636474BookingTableModel();
    private final JTable table = new JTable(model);
    private final JButton modifyButton = K2636474UiTheme.secondaryButton("Modify", "edit");
    private final JButton cancelButton = K2636474UiTheme.dangerButton("Cancel booking", "trash");
    private final JButton invoiceButton = K2636474UiTheme.secondaryButton("View invoice", "invoice");
    private final K2636474TableCard tableCard;

    /**
     * Creates the page.
     *
     * @param context UI context
     */
    public K2636474MyBookingsPage(K2636474UiContext context) {
        super("My bookings", "Change or cancel up to " + K2636474Booking.CHANGE_CUTOFF_HOURS
                + " hours before check-in (" + K2636474Booking.CHECK_IN_TIME + ").");
        this.context = context;

        table.setRowSorter(new TableRowSorter<>(model));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        K2636474BookingTableModel.configure(table);
        table.getSelectionModel().addListSelectionListener(event -> updateButtons());
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    viewInvoice();
                }
            }
        });
        tableCard = new K2636474TableCard(table, "calendar", "No bookings yet",
                "Head to Find a room to plan your first stay.");

        modifyButton.addActionListener(event -> modify());
        cancelButton.addActionListener(event -> cancel());
        invoiceButton.setToolTipText("Show the invoice for the selected booking (or double-click a row)");
        invoiceButton.addActionListener(event -> viewInvoice());
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, K2636474UiTheme.GRID, 0));
        actions.setOpaque(false);
        actions.add(invoiceButton);
        actions.add(modifyButton);
        actions.add(cancelButton);

        getBody().add(actions, BorderLayout.NORTH);
        getBody().add(tableCard, BorderLayout.CENTER);
        updateButtons();
    }

    private K2636474Booking selectedBooking() {
        int viewRow = table.getSelectedRow();
        return viewRow < 0 ? null : model.getBookingAt(table.convertRowIndexToModel(viewRow));
    }

    private void updateButtons() {
        K2636474Booking booking = selectedBooking();
        String reason = booking == null ? "Select a booking first."
                : context.getBookingService().whyCannotChange(booking);
        boolean canChange = reason == null;
        modifyButton.setEnabled(canChange);
        cancelButton.setEnabled(canChange);
        modifyButton.setToolTipText(canChange ? "Change dates or number of guests" : reason);
        cancelButton.setToolTipText(canChange ? "Cancel and get the advance refunded" : reason);
        invoiceButton.setEnabled(booking != null);
    }

    private void modify() {
        K2636474Booking booking = selectedBooking();
        if (booking == null) {
            return;
        }
        new K2636474BookingDialog(context, booking.getRoom(), booking, booking.getCheckInDate(),
                booking.getCheckOutDate(), booking.getNumberOfGuests(), saved -> {
                    refresh();
                    context.toast("Booking " + saved.getBookingId() + " updated — invoice "
                            + saved.getInvoice().getInvoiceId() + " issued.");
                }).setVisible(true);
    }

    private void cancel() {
        K2636474Booking booking = selectedBooking();
        if (booking == null) {
            return;
        }
        int answer = JOptionPane.showConfirmDialog(this, "Cancel booking " + booking.getBookingId() + " (room "
                + booking.getRoom().getRoomId() + ", " + K2636474Format.friendlyDate(booking.getCheckInDate())
                + ")?\nThis cannot be undone.", "Cancel booking", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            K2636474Booking cancelled = context.getBookingService().cancelBooking(booking.getBookingId(),
                    context.getCurrentUser());
            refresh();
            K2636474UiTheme.showSuccess(this, "Booking cancelled", "Booking " + cancelled.getBookingId()
                    + " has been cancelled.\nAdvance of " + K2636474Format.lkr(cancelled.getRefundedAmount())
                    + " refunded.");
            context.toast("Booking " + cancelled.getBookingId() + " cancelled — advance refunded.");
        } catch (RuntimeException error) {
            K2636474UiTheme.showError(this, error);
        }
    }

    private void viewInvoice() {
        K2636474Booking booking = selectedBooking();
        if (booking != null && booking.getInvoice() != null) {
            new K2636474InvoiceDialog(context, booking.getInvoice()).setVisible(true);
        }
    }

    @Override
    public void refresh() {
        model.setBookings(context.getBookingService().getBookingsForGuest(context.getCurrentGuest()));
        tableCard.updateView();
        updateButtons();
    }
}
