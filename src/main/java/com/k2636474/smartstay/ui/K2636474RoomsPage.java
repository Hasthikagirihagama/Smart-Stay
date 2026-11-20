package com.k2636474.smartstay.ui;

import com.k2636474.smartstay.model.K2636474Room;
import com.k2636474.smartstay.model.enums.K2636474RoomStatus;
import com.k2636474.smartstay.model.enums.K2636474RoomType;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Admin room management: sortable, filterable table of rooms with Add, Edit,
 * Remove and Change Status actions.
 */
public class K2636474RoomsPage extends K2636474Page {

    private static final String ALL = "All";
    private static final int STATUS_COLUMN_WIDTH = 170;

    private final K2636474UiContext context;
    private final K2636474RoomTableModel model = new K2636474RoomTableModel();
    private final JTable table = new JTable(model);
    private final TableRowSorter<K2636474RoomTableModel> sorter = new TableRowSorter<>(model);
    private final JComboBox<Object> typeFilter = new JComboBox<>();
    private final JComboBox<Object> statusFilter = new JComboBox<>();
    private final JButton editButton = K2636474UiTheme.secondaryButton("Edit", "edit");
    private final JButton removeButton = K2636474UiTheme.dangerButton("Remove", "trash");
    private final JButton statusButton = K2636474UiTheme.secondaryButton("Change status", "status");
    private final K2636474TableCard tableCard;

    /**
     * Creates the page.
     *
     * @param context UI context
     */
    public K2636474RoomsPage(K2636474UiContext context) {
        super("Rooms", "Add, edit and take rooms in or out of service.");
        this.context = context;

        table.setRowSorter(sorter);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(K2636474RoomTableModel.RATE_COLUMN)
                .setCellRenderer(K2636474UiTheme.moneyRenderer());
        table.getColumnModel().getColumn(K2636474RoomTableModel.STATUS_COLUMN)
                .setCellRenderer(new K2636474StatusPillRenderer());
        // Wide enough for the longest pill, "Under Maintenance", so it is never clipped.
        table.getColumnModel().getColumn(K2636474RoomTableModel.STATUS_COLUMN).setMinWidth(STATUS_COLUMN_WIDTH);
        table.getSelectionModel().addListSelectionListener(event -> updateButtons());
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2 && selectedRoom() != null) {
                    editRoom();
                }
            }
        });
        tableCard = new K2636474TableCard(table, "door", "No rooms match these filters",
                "Change the filters above, or add a new room.");

        getBody().add(buildToolbar(), BorderLayout.NORTH);
        getBody().add(tableCard, BorderLayout.CENTER);
        updateButtons();
    }

    private JPanel buildToolbar() {
        typeFilter.addItem(ALL);
        for (K2636474RoomType type : K2636474RoomType.values()) {
            typeFilter.addItem(type);
        }
        statusFilter.addItem(ALL);
        for (K2636474RoomStatus status : K2636474RoomStatus.values()) {
            statusFilter.addItem(status);
        }
        typeFilter.setToolTipText("Show only one room type");
        statusFilter.setToolTipText("Show only one status");
        typeFilter.addActionListener(event -> applyFilters());
        statusFilter.addActionListener(event -> applyFilters());

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, K2636474UiTheme.GRID, 5));
        filters.setOpaque(false);
        filters.add(K2636474UiTheme.fieldLabel("Type"));
        filters.add(typeFilter);
        filters.add(javax.swing.Box.createHorizontalStrut(K2636474UiTheme.GRID));
        filters.add(K2636474UiTheme.fieldLabel("Status"));
        filters.add(statusFilter);

        JButton addButton = K2636474UiTheme.primaryButton("Add room", "plus");
        addButton.setToolTipText("Add a new room");
        addButton.addActionListener(event -> addRoom());
        editButton.setToolTipText("Edit the selected room (or double-click a row)");
        editButton.addActionListener(event -> editRoom());
        removeButton.setToolTipText("Remove the selected room");
        removeButton.addActionListener(event -> removeRoom());
        statusButton.setToolTipText("Set the selected room to Available, Reserved or Under Maintenance");
        statusButton.addActionListener(event -> showStatusMenu());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, K2636474UiTheme.GRID, 0));
        actions.setOpaque(false);
        actions.add(statusButton);
        actions.add(editButton);
        actions.add(removeButton);
        actions.add(addButton);

        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);
        toolbar.add(filters, BorderLayout.WEST);
        toolbar.add(actions, BorderLayout.EAST);
        return toolbar;
    }

    private void applyFilters() {
        Object type = typeFilter.getSelectedItem();
        Object status = statusFilter.getSelectedItem();
        sorter.setRowFilter(new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends K2636474RoomTableModel, ? extends Integer> entry) {
                K2636474Room room = entry.getModel().getRoomAt(entry.getIdentifier());
                return (ALL.equals(type) || room.getRoomType() == type)
                        && (ALL.equals(status) || room.getStatus() == status);
            }
        });
        tableCard.updateView();
        updateButtons();
    }

    private K2636474Room selectedRoom() {
        int viewRow = table.getSelectedRow();
        return viewRow < 0 ? null : model.getRoomAt(table.convertRowIndexToModel(viewRow));
    }

    private void updateButtons() {
        boolean selected = selectedRoom() != null;
        editButton.setEnabled(selected);
        removeButton.setEnabled(selected);
        statusButton.setEnabled(selected);
    }

    private void addRoom() {
        new K2636474RoomFormDialog(context, null, room -> {
            refresh();
            context.toast("Room " + room.getRoomId() + " added.");
        }).setVisible(true);
    }

    private void editRoom() {
        K2636474Room room = selectedRoom();
        if (room != null) {
            new K2636474RoomFormDialog(context, room, saved -> {
                refresh();
                context.toast("Room " + saved.getRoomId() + " updated.");
            }).setVisible(true);
        }
    }

    private void removeRoom() {
        K2636474Room room = selectedRoom();
        if (room == null) {
            return;
        }
        int answer = JOptionPane.showConfirmDialog(this, "Remove room " + room.getRoomId() + " ("
                + room.getRoomType() + ")? This cannot be undone.", "Remove room",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            context.getRoomService().removeRoom(room.getRoomId());
            refresh();
            context.toast("Room " + room.getRoomId() + " removed.");
        } catch (RuntimeException error) {
            K2636474UiTheme.showError(this, error);
        }
    }

    private void showStatusMenu() {
        K2636474Room room = selectedRoom();
        if (room == null) {
            return;
        }
        JPopupMenu menu = new JPopupMenu();
        for (K2636474RoomStatus status : K2636474RoomStatus.values()) {
            JMenuItem item = new JMenuItem(status.getDisplayName(),
                    K2636474UiTheme.icon("status", 14, K2636474UiTheme.statusColor(status)));
            item.setEnabled(status != room.getStatus());
            item.addActionListener(event -> {
                context.getRoomService().changeStatus(room.getRoomId(), status);
                refresh();
                context.toast("Room " + room.getRoomId() + " is now " + status.getDisplayName() + ".");
            });
            menu.add(item);
        }
        menu.show(statusButton, 0, statusButton.getHeight() + 4);
    }

    @Override
    public void refresh() {
        String selectedId = selectedRoom() == null ? null : selectedRoom().getRoomId();
        model.setRooms(context.getRoomService().getAllRooms());
        applyFilters();
        // Keep the same room selected after a reload so repeated actions stay on it.
        for (int row = 0; row < table.getRowCount(); row++) {
            if (model.getRoomAt(table.convertRowIndexToModel(row)).getRoomId().equals(selectedId)) {
                table.setRowSelectionInterval(row, row);
            }
        }
    }
}
