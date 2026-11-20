package com.k2636474.smartstay.ui;

import com.k2636474.smartstay.model.K2636474Guest;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.util.List;

/**
 * Admin list of registered guests with their identity details and booking count.
 */
public class K2636474GuestsPage extends K2636474Page {

    private static final String[] COLUMNS = {"Guest ID", "Full name", "ID type", "ID number", "Contact", "Email",
            "Bookings"};

    private final K2636474UiContext context;
    private final DefaultTableModel model = new DefaultTableModel(COLUMNS, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        @Override
        public Class<?> getColumnClass(int column) {
            return column == COLUMNS.length - 1 ? Integer.class : String.class;
        }
    };
    private final K2636474TableCard tableCard;

    /**
     * Creates the page.
     *
     * @param context UI context
     */
    public K2636474GuestsPage(K2636474UiContext context) {
        super("Guests", "Everyone registered with SmartStay.");
        this.context = context;
        JTable table = new JTable(model);
        table.setRowSorter(new TableRowSorter<>(model));
        tableCard = new K2636474TableCard(table, "users", "No guests yet",
                "Guests appear here once they register.");
        getBody().add(tableCard, BorderLayout.CENTER);
    }

    @Override
    public void refresh() {
        model.setRowCount(0);
        List<K2636474Guest> guests = context.getAuthService().getAllGuests();
        for (K2636474Guest guest : guests) {
            model.addRow(new Object[]{guest.getUserId(), guest.getFullName(), guest.getIdType().getDisplayName(),
                    guest.getIdNumber(), guest.getContactNumber(), guest.getEmail(),
                    context.getBookingService().getBookingsForGuest(guest).size()});
        }
        tableCard.updateView();
    }
}
