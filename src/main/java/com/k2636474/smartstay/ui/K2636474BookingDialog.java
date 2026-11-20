package com.k2636474.smartstay.ui;

import com.k2636474.smartstay.exception.K2636474BookingException;
import com.k2636474.smartstay.exception.K2636474ValidationException;
import com.k2636474.smartstay.model.K2636474Booking;
import com.k2636474.smartstay.model.K2636474Guest;
import com.k2636474.smartstay.model.K2636474Invoice;
import com.k2636474.smartstay.model.K2636474Room;
import com.k2636474.smartstay.service.K2636474BookingService;
import com.k2636474.smartstay.util.K2636474Format;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.function.Consumer;

/**
 * Dialog for booking a room or modifying an existing booking. The price breakdown
 * updates live as dates or guests change, using the same calculator that produces
 * the real invoice (via the booking service), so the preview always matches the bill.
 */
public class K2636474BookingDialog extends JDialog {

    private static final int DIALOG_WIDTH = 900;
    private static final int DIALOG_HEIGHT = 640;

    private final K2636474UiContext context;
    private final K2636474Room room;
    private final K2636474Booking existingBooking;
    private final K2636474Guest guest;
    private final Consumer<K2636474Booking> onSaved;
    private final K2636474DateField checkInField;
    private final K2636474DateField checkOutField;
    private final JSpinner guestsSpinner;
    private final JLabel nightsLabel = K2636474UiTheme.caption(" ");
    private final K2636474PriceBreakdownPanel breakdown;
    private final JButton confirmButton;

    /**
     * Creates the dialog.
     *
     * @param context         UI context
     * @param room            room to book (the booking's room when modifying)
     * @param existingBooking booking to modify, or null to create a new one
     * @param checkIn         initial check-in date
     * @param checkOut        initial check-out date
     * @param guests          initial number of guests
     * @param onSaved         called with the booking after it is saved
     */
    public K2636474BookingDialog(K2636474UiContext context, K2636474Room room, K2636474Booking existingBooking,
                                 LocalDate checkIn, LocalDate checkOut, int guests,
                                 Consumer<K2636474Booking> onSaved) {
        super(context.getWindow(), existingBooking == null ? "Book a room" : "Modify booking",
                ModalityType.APPLICATION_MODAL);
        this.context = context;
        this.room = room;
        this.existingBooking = existingBooking;
        this.guest = existingBooking == null ? context.getCurrentGuest() : existingBooking.getGuest();
        this.onSaved = onSaved;
        checkInField = new K2636474DateField(checkIn);
        checkOutField = new K2636474DateField(checkOut);
        int maxGuests = room.getMaxOccupancy();
        guestsSpinner = new JSpinner(new SpinnerNumberModel(Math.min(Math.max(guests, 1), maxGuests), 1, maxGuests, 1));
        breakdown = new K2636474PriceBreakdownPanel(context.getInvoiceService().describeDiscountPolicy());
        confirmButton = K2636474UiTheme.primaryButton(existingBooking == null ? "Confirm booking" : "Save changes",
                "check");

        JPanel content = new JPanel(new BorderLayout(0, K2636474UiTheme.GAP + 4));
        content.setBackground(K2636474UiTheme.BACKGROUND);
        content.setBorder(BorderFactory.createEmptyBorder(K2636474UiTheme.PAGE_PADDING, K2636474UiTheme.PAGE_PADDING,
                K2636474UiTheme.GAP + 4, K2636474UiTheme.PAGE_PADDING));
        content.add(buildHeader(), BorderLayout.NORTH);
        JPanel columns = new JPanel(new GridLayout(1, 2, K2636474UiTheme.GAP, 0));
        columns.setOpaque(false);
        columns.add(buildForm());
        columns.add(breakdown);
        content.add(columns, BorderLayout.CENTER);
        content.add(buildButtons(), BorderLayout.SOUTH);
        setContentPane(content);

        checkInField.addChangeListener(event -> updatePreview());
        checkOutField.addChangeListener(event -> updatePreview());
        guestsSpinner.addChangeListener(event -> updatePreview());
        updatePreview();

        K2636474UiTheme.installDialogKeys(this, confirmButton);
        setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        setLocationRelativeTo(context.getWindow());
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        String title = existingBooking == null ? "Book room " + room.getRoomId()
                : "Modify booking " + existingBooking.getBookingId();
        header.add(K2636474UiTheme.pageTitle(title));
        header.add(Box.createVerticalStrut(4));
        header.add(K2636474UiTheme.subtitle(existingBooking == null
                ? "Choose your dates and guests. The price updates as you go."
                : "Change dates or guests. A new invoice replaces the old one."));
        return header;
    }

    private JComponent buildForm() {
        K2636474CardPanel card = K2636474UiTheme.card();
        JPanel roomSummary = new JPanel(new BorderLayout());
        roomSummary.setOpaque(false);
        JPanel roomText = new JPanel();
        roomText.setOpaque(false);
        roomText.setLayout(new BoxLayout(roomText, BoxLayout.Y_AXIS));
        roomText.add(K2636474UiTheme.label(room.getRoomType().getDisplayName(), K2636474UiTheme.font(Font.BOLD, 18),
                K2636474UiTheme.TEXT));
        roomText.add(K2636474UiTheme.subtitle("Room " + room.getRoomId() + " · " + room.getBedType() + " bed · "
                + K2636474Format.lkr(room.getDailyRate()) + " / night"));
        roomText.add(K2636474UiTheme.caption("Sleeps " + room.getRoomPackage().getBaseCapacity() + ", up to "
                + room.getMaxOccupancy() + " guests · "
                + (room.getRoomPackage().includesBreakfast() ? "breakfast included" : "room only")));
        roomSummary.add(roomText, BorderLayout.CENTER);
        roomSummary.add(new JLabel(K2636474UiTheme.icon("bed", 28,
                K2636474UiTheme.roomTypeColor(room.getRoomType()))), BorderLayout.EAST);

        K2636474FormPanel form = new K2636474FormPanel();
        form.addRow(roomSummary, 0);
        JPanel dates = new JPanel(new GridLayout(1, 2, K2636474UiTheme.GRID + 4, 0));
        dates.setOpaque(false);
        dates.add(labelled("Check-in", checkInField));
        dates.add(labelled("Check-out", checkOutField));
        form.addRow(dates, K2636474UiTheme.GAP + 4);
        form.addRow(nightsLabel, 6);
        form.addRow(labelled("Guests", guestsSpinner), K2636474UiTheme.GAP);
        form.addRow(buildNote(), K2636474UiTheme.GAP + 4);
        card.add(form, BorderLayout.NORTH);
        return card;
    }

    private static JComponent labelled(String label, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);
        panel.add(K2636474UiTheme.fieldLabel(label), BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private static JComponent buildNote() {
        K2636474CardPanel note = new K2636474CardPanel(new BorderLayout(10, 0));
        note.setBackground(K2636474UiTheme.PRIMARY_TINT);
        note.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        note.add(new JLabel(K2636474UiTheme.icon("info", 18, K2636474UiTheme.PRIMARY)), BorderLayout.WEST);
        note.add(K2636474UiTheme.label("<html><div style='width:250px'>An advance of <b>" + K2636474Format.lkr(K2636474Booking.ADVANCE_AMOUNT)
                + "</b> is recorded as paid when you confirm and is deducted from your bill. You can modify or "
                + "cancel (full advance refund) until " + K2636474Booking.CHANGE_CUTOFF_HOURS
                + " hours before check-in at " + K2636474Booking.CHECK_IN_TIME + ".</div></html>",
                K2636474UiTheme.font(Font.PLAIN, 12), K2636474UiTheme.TEXT), BorderLayout.CENTER);
        return note;
    }

    private JComponent buildButtons() {
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, K2636474UiTheme.GRID + 4, 0));
        buttons.setOpaque(false);
        JButton cancelButton = K2636474UiTheme.secondaryButton("Close", null);
        cancelButton.setToolTipText("Close without saving (Esc)");
        cancelButton.addActionListener(event -> dispose());
        confirmButton.setToolTipText(existingBooking == null ? "Confirm and pay the advance (Enter)"
                : "Save the new dates and guests (Enter)");
        confirmButton.addActionListener(event -> confirm());
        buttons.add(cancelButton);
        buttons.add(confirmButton);
        return buttons;
    }

    private void updatePreview() {
        LocalDate in = checkInField.getDate();
        LocalDate out = checkOutField.getDate();
        long nights = java.time.temporal.ChronoUnit.DAYS.between(in, out);
        nightsLabel.setText(nights > 0 ? nights + " night" + (nights == 1 ? "" : "s") + " · check-in from "
                + K2636474Booking.CHECK_IN_TIME + " on " + K2636474Format.friendlyDate(in) : " ");
        K2636474BookingService bookingService = context.getBookingService();
        try {
            bookingService.validateStayDates(in, out);
            K2636474Invoice preview = bookingService.previewInvoice(guest, room.getRoomId(), in, out,
                    (Integer) guestsSpinner.getValue());
            breakdown.showInvoice(preview);
            confirmButton.setEnabled(true);
        } catch (K2636474BookingException | K2636474ValidationException error) {
            breakdown.showMessage(error.getMessage());
            confirmButton.setEnabled(false);
        }
    }

    private void confirm() {
        LocalDate in = checkInField.getDate();
        LocalDate out = checkOutField.getDate();
        int guests = (Integer) guestsSpinner.getValue();
        try {
            K2636474Booking saved = existingBooking == null
                    ? context.getBookingService().createBooking(guest, room.getRoomId(), in, out, guests)
                    : context.getBookingService().modifyBooking(existingBooking.getBookingId(),
                            context.getCurrentUser(), in, out, guests);
            dispose();
            onSaved.accept(saved);
            showConfirmation(saved);
        } catch (RuntimeException error) {
            K2636474UiTheme.showError(this, error);
        }
    }

    private void showConfirmation(K2636474Booking booking) {
        K2636474Invoice invoice = booking.getInvoice();
        String heading = existingBooking == null ? "Booking " + booking.getBookingId() + " is confirmed!"
                : "Booking " + booking.getBookingId() + " has been updated.";
        String message = "<html><b>" + heading + "</b><br><br>"
                + "Room " + booking.getRoom().getRoomId() + " (" + booking.getRoom().getRoomType() + ")<br>"
                + K2636474Format.friendlyDate(booking.getCheckInDate()) + " → "
                + K2636474Format.friendlyDate(booking.getCheckOutDate()) + " · " + booking.getNights() + " nights<br>"
                + "Advance paid: " + K2636474Format.lkr(booking.getAdvancePaid()) + "<br>"
                + "Balance at the hotel: <b>" + K2636474Format.lkr(invoice.getFinalPayable()) + "</b><br><br>"
                + "Invoice " + invoice.getInvoiceId() + " has been issued.</html>";
        Object[] options = {"View invoice", "Done"};
        int choice = JOptionPane.showOptionDialog(context.getWindow(), message, "Booking saved",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                K2636474UiTheme.icon("check", 32, K2636474UiTheme.SUCCESS), options, options[0]);
        if (choice == 0) {
            new K2636474InvoiceDialog(context, invoice).setVisible(true);
        }
    }
}
