package com.k2636474.smartstay.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.k2636474.smartstay.model.K2636474Booking;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.List;

/**
 * Reservation search by booking ID, guest name and/or check-in date. Used by both
 * dashboards: admins see every booking, guests only their own (the service applies
 * the privacy rule from the logged-in user).
 */
public class K2636474ReservationsPage extends K2636474Page {

    private final K2636474UiContext context;
    private final K2636474BookingTableModel model = new K2636474BookingTableModel();
    private final JTable table = new JTable(model);
    private final JTextField searchField = new JTextField(22);
    private final JCheckBox dateFilterBox = new JCheckBox("Check-in on");
    private final K2636474DateField dateField = new K2636474DateField(LocalDate.now());
    private final JButton invoiceButton = K2636474UiTheme.secondaryButton("View invoice", "invoice");
    private final JLabel resultCount = K2636474UiTheme.caption(" ");
    private final K2636474TableCard tableCard;

    /**
     * Creates the page.
     *
     * @param context  UI context
     * @param title    page title
     * @param subtitle page subtitle
     */
    public K2636474ReservationsPage(K2636474UiContext context, String title, String subtitle) {
        super(title, subtitle);
        this.context = context;

        table.setRowSorter(new TableRowSorter<>(model));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        K2636474BookingTableModel.configure(table);
        table.getSelectionModel().addListSelectionListener(event ->
                invoiceButton.setEnabled(selectedBooking() != null));
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    viewInvoice();
                }
            }
        });
        tableCard = new K2636474TableCard(table, "search", "No reservations found",
                "Try another booking ID, guest name or check-in date.");

        getBody().add(buildToolbar(), BorderLayout.NORTH);
        getBody().add(tableCard, BorderLayout.CENTER);
        getBody().add(resultCount, BorderLayout.SOUTH);
        invoiceButton.setEnabled(false);
    }

    private JPanel buildToolbar() {
        searchField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Booking ID or guest name");
        searchField.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON,
                K2636474UiTheme.icon("search", 16, K2636474UiTheme.TEXT_SECONDARY));
        searchField.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        searchField.setPreferredSize(new Dimension(280, searchField.getPreferredSize().height));
        searchField.setToolTipText("Type a booking ID (e.g. BK0002) or part of a guest's name");
        // Search as the user types; the list is in memory so this is instant.
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent event) {
                refresh();
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                refresh();
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                refresh();
            }
        });
        dateFilterBox.setOpaque(false);
        dateFilterBox.setToolTipText("Only show reservations checking in on the chosen date");
        dateFilterBox.addActionListener(event -> {
            dateField.setEnabled(dateFilterBox.isSelected());
            refresh();
        });
        dateField.setEnabled(false);
        dateField.addChangeListener(event -> refresh());

        JButton clearButton = K2636474UiTheme.secondaryButton("Clear", null);
        clearButton.setToolTipText("Clear the search and date filter");
        clearButton.addActionListener(event -> {
            dateFilterBox.setSelected(false);
            dateField.setEnabled(false);
            searchField.setText("");
            refresh();
        });

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, K2636474UiTheme.GRID, 2));
        filters.setOpaque(false);
        filters.add(searchField);
        filters.add(javax.swing.Box.createHorizontalStrut(K2636474UiTheme.GRID));
        filters.add(new JLabel(K2636474UiTheme.icon("calendar", 16, K2636474UiTheme.TEXT_SECONDARY)));
        filters.add(dateFilterBox);
        filters.add(dateField);
        filters.add(clearButton);

        invoiceButton.setToolTipText("Show the invoice of the selected reservation (or double-click a row)");
        invoiceButton.addActionListener(event -> viewInvoice());
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actions.setOpaque(false);
        actions.add(invoiceButton);

        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);
        toolbar.add(filters, BorderLayout.WEST);
        toolbar.add(actions, BorderLayout.EAST);
        return toolbar;
    }

    private K2636474Booking selectedBooking() {
        int viewRow = table.getSelectedRow();
        return viewRow < 0 ? null : model.getBookingAt(table.convertRowIndexToModel(viewRow));
    }

    private void viewInvoice() {
        K2636474Booking booking = selectedBooking();
        if (booking != null && booking.getInvoice() != null) {
            new K2636474InvoiceDialog(context, booking.getInvoice()).setVisible(true);
        }
    }

    @Override
    public void refresh() {
        LocalDate date = dateFilterBox.isSelected() ? dateField.getDate() : null;
        List<K2636474Booking> results = context.getBookingService()
                .searchReservations(context.getCurrentUser(), searchField.getText(), date);
        model.setBookings(results);
        tableCard.updateView();
        resultCount.setText(results.size() + (results.size() == 1 ? " reservation" : " reservations"));
        invoiceButton.setEnabled(false);
    }
}
