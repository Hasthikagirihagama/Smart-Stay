package com.k2636474.smartstay.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.k2636474.smartstay.model.K2636474Booking;
import com.k2636474.smartstay.model.K2636474Room;
import com.k2636474.smartstay.model.enums.K2636474RoomType;
import com.k2636474.smartstay.util.K2636474Format;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.List;

/**
 * Guest room search: choose type, dates and guests, then browse available rooms as
 * cards (or as a table in list view) and book one.
 */
public class K2636474FindRoomPage extends K2636474Page {

    private static final String ANY_TYPE = "Any type";
    private static final int GRID_COLUMNS = 3;
    private static final int MAX_GUESTS_SEARCH = 6;
    private static final String PROMPT_VIEW = "prompt";
    private static final String GRID_VIEW = "grid";
    private static final String LIST_VIEW = "list";
    private static final String EMPTY_VIEW = "empty";

    private final K2636474UiContext context;
    private final JComboBox<Object> typeBox = new JComboBox<>();
    private final K2636474DateField checkInField;
    private final K2636474DateField checkOutField;
    private final JSpinner guestsSpinner = new JSpinner(new SpinnerNumberModel(2, 1, MAX_GUESTS_SEARCH, 1));
    private final JLabel resultsTitle = K2636474UiTheme.sectionTitle(" ");
    private final CardLayout resultViews = new CardLayout();
    private final JPanel resultsArea = new JPanel(resultViews);
    private final JPanel cardGrid = new JPanel(new GridLayout(0, GRID_COLUMNS, K2636474UiTheme.GAP,
            K2636474UiTheme.GAP));
    private final K2636474RoomTableModel listModel = new K2636474RoomTableModel();
    private final JTable listTable = new JTable(listModel);
    private final JToggleButton gridToggle = new JToggleButton(K2636474UiTheme.icon("grid", 16, K2636474UiTheme.TEXT));
    private final JToggleButton listToggle = new JToggleButton(K2636474UiTheme.icon("list", 16, K2636474UiTheme.TEXT));
    private final JButton bookSelectedButton = K2636474UiTheme.primaryButton("Book selected", "calendar");
    private List<K2636474Room> lastResults = List.of();
    private boolean hasSearched;

    /**
     * Creates the page.
     *
     * @param context UI context
     */
    public K2636474FindRoomPage(K2636474UiContext context) {
        super("Find a room", "Pick your dates and party size — we'll show every room that's free.");
        this.context = context;
        LocalDate earliest = context.getBookingService().earliestCheckIn();
        checkInField = new K2636474DateField(earliest);
        checkOutField = new K2636474DateField(earliest.plusDays(1));

        getBody().add(buildSearchCard(), BorderLayout.NORTH);
        getBody().add(buildResults(), BorderLayout.CENTER);
        resultViews.show(resultsArea, PROMPT_VIEW);
    }

    private JComponent buildSearchCard() {
        typeBox.addItem(ANY_TYPE);
        for (K2636474RoomType type : K2636474RoomType.values()) {
            typeBox.addItem(type);
        }
        typeBox.setToolTipText("Choose a room type, or Any to see all types");
        checkInField.setToolTipText("Check-in date (at least " + K2636474Booking.MIN_ADVANCE_DAYS + " days from today)");
        checkOutField.setToolTipText("Check-out date (after check-in)");
        guestsSpinner.setToolTipText("Number of guests staying");
        // Keep check-out after check-in automatically so the common case needs one change, not two.
        checkInField.addChangeListener(event -> {
            if (!checkOutField.getDate().isAfter(checkInField.getDate())) {
                checkOutField.setDate(checkInField.getDate().plusDays(1));
            }
        });

        JButton searchButton = K2636474UiTheme.primaryButton("Search", "search");
        searchButton.setToolTipText("Show rooms available for these dates");
        searchButton.addActionListener(event -> search());

        K2636474CardPanel card = K2636474UiTheme.card();
        JPanel fields = new JPanel(new GridBagLayout());
        fields.setOpaque(false);
        addSearchField(fields, 0, "Room type", typeBox, 1.2);
        addSearchField(fields, 1, "Check-in", checkInField, 1);
        addSearchField(fields, 2, "Check-out", checkOutField, 1);
        addSearchField(fields, 3, "Guests", guestsSpinner, 0.5);
        addSearchField(fields, 4, " ", searchButton, 0);
        card.add(fields, BorderLayout.CENTER);
        return card;
    }

    private static void addSearchField(JPanel panel, int column, String label, JComponent field, double weight) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = column;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(0, column == 0 ? 0 : K2636474UiTheme.GAP, 6, 0);
        panel.add(K2636474UiTheme.fieldLabel(label), constraints);
        constraints.gridy = 1;
        constraints.weightx = weight;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(0, column == 0 ? 0 : K2636474UiTheme.GAP, 0, 0);
        panel.add(field, constraints);
    }

    private JComponent buildResults() {
        gridToggle.setToolTipText("Show rooms as cards");
        listToggle.setToolTipText("Show rooms as a table");
        ButtonGroup viewGroup = new ButtonGroup();
        viewGroup.add(gridToggle);
        viewGroup.add(listToggle);
        gridToggle.setSelected(true);
        for (JToggleButton toggle : new JToggleButton[]{gridToggle, listToggle}) {
            toggle.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_TOOLBAR_BUTTON);
            toggle.putClientProperty(FlatClientProperties.STYLE, "selectedBackground:"
                    + K2636474UiTheme.hex(K2636474UiTheme.PRIMARY_TINT) + ";margin:6,8,6,8");
            toggle.addActionListener(event -> showResults());
        }
        bookSelectedButton.setToolTipText("Book the room selected in the table");
        bookSelectedButton.addActionListener(event -> bookSelectedRow());

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, K2636474UiTheme.GRID, 0));
        controls.setOpaque(false);
        controls.add(bookSelectedButton);
        controls.add(gridToggle);
        controls.add(listToggle);
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(resultsTitle, BorderLayout.WEST);
        header.add(controls, BorderLayout.EAST);

        cardGrid.setOpaque(false);
        JPanel gridHolder = new JPanel(new BorderLayout());
        gridHolder.setBackground(K2636474UiTheme.BACKGROUND);
        gridHolder.setBorder(BorderFactory.createEmptyBorder(0, 0, K2636474UiTheme.GAP, 2));
        gridHolder.add(cardGrid, BorderLayout.NORTH);
        JScrollPane gridScroll = K2636474UiTheme.scroll(gridHolder);
        gridScroll.getViewport().setBackground(K2636474UiTheme.BACKGROUND);

        listTable.setRowSorter(new TableRowSorter<>(listModel));
        listTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listTable.getColumnModel().getColumn(K2636474RoomTableModel.RATE_COLUMN)
                .setCellRenderer(K2636474UiTheme.moneyRenderer());
        listTable.getColumnModel().getColumn(K2636474RoomTableModel.STATUS_COLUMN)
                .setCellRenderer(new K2636474StatusPillRenderer());
        listTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    bookSelectedRow();
                }
            }
        });
        K2636474TableCard listCard = new K2636474TableCard(listTable, "bed", "No rooms", " ");

        resultsArea.setOpaque(false);
        resultsArea.add(wrapInCard(K2636474UiTheme.emptyState("hotel", "Where will you stay?",
                "Choose dates and guests above, then press Search.")), PROMPT_VIEW);
        resultsArea.add(gridScroll, GRID_VIEW);
        resultsArea.add(listCard, LIST_VIEW);
        resultsArea.add(wrapInCard(K2636474UiTheme.emptyState("search", "No rooms match those dates",
                "Try different dates, fewer guests or another room type.")), EMPTY_VIEW);

        JPanel results = new JPanel(new BorderLayout(0, K2636474UiTheme.GRID + 4));
        results.setOpaque(false);
        results.add(header, BorderLayout.NORTH);
        results.add(resultsArea, BorderLayout.CENTER);
        return results;
    }

    private static JComponent wrapInCard(JComponent content) {
        K2636474CardPanel card = new K2636474CardPanel(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        card.add(content);
        return card;
    }

    private void search() {
        Object selectedType = typeBox.getSelectedItem();
        K2636474RoomType type = selectedType instanceof K2636474RoomType roomType ? roomType : null;
        try {
            lastResults = context.getBookingService().findAvailableRooms(type, checkInField.getDate(),
                    checkOutField.getDate(), (Integer) guestsSpinner.getValue());
            hasSearched = true;
            showResults();
        } catch (RuntimeException error) {
            K2636474UiTheme.showError(this, error);
        }
    }

    private void showResults() {
        if (!hasSearched) {
            return;
        }
        long nights = java.time.temporal.ChronoUnit.DAYS.between(checkInField.getDate(), checkOutField.getDate());
        resultsTitle.setText(lastResults.size() + (lastResults.size() == 1 ? " room" : " rooms") + " available · "
                + K2636474Format.friendlyDate(checkInField.getDate()) + " → "
                + K2636474Format.friendlyDate(checkOutField.getDate()) + " · " + nights
                + (nights == 1 ? " night" : " nights"));
        cardGrid.removeAll();
        lastResults.forEach(room -> cardGrid.add(new K2636474RoomCard(room, this::openBooking)));
        cardGrid.revalidate();
        cardGrid.repaint();
        listModel.setRooms(lastResults);
        bookSelectedButton.setVisible(listToggle.isSelected() && !lastResults.isEmpty());
        String view = lastResults.isEmpty() ? EMPTY_VIEW : listToggle.isSelected() ? LIST_VIEW : GRID_VIEW;
        resultViews.show(resultsArea, view);
    }

    private void bookSelectedRow() {
        int viewRow = listTable.getSelectedRow();
        if (viewRow < 0) {
            javax.swing.JOptionPane.showMessageDialog(this, "Select a room in the table first.", "No room selected",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        openBooking(listModel.getRoomAt(listTable.convertRowIndexToModel(viewRow)));
    }

    private void openBooking(K2636474Room room) {
        new K2636474BookingDialog(context, room, null, checkInField.getDate(), checkOutField.getDate(),
                (Integer) guestsSpinner.getValue(), booking -> {
                    context.toast("Booking " + booking.getBookingId() + " confirmed for room "
                            + booking.getRoom().getRoomId() + ".");
                    search();
                }).setVisible(true);
    }

    @Override
    public void refresh() {
        bookSelectedButton.setVisible(false);
        if (hasSearched) {
            search();
        }
    }
}
