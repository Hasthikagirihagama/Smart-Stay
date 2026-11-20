package com.k2636474.smartstay.ui;

import com.k2636474.smartstay.model.K2636474Booking;
import com.k2636474.smartstay.model.enums.K2636474RoomStatus;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

/**
 * Admin overview: headline numbers as stat cards and the check-ins due this week.
 */
public class K2636474OverviewPage extends K2636474Page {

    private final K2636474UiContext context;
    private final JLabel totalRoomsValue = statValueLabel();
    private final JLabel availableValue = statValueLabel();
    private final JLabel maintenanceValue = statValueLabel();
    private final JLabel activeBookingsValue = statValueLabel();
    private final JLabel checkInsValue = statValueLabel();
    private final K2636474BookingTableModel upcomingModel = new K2636474BookingTableModel();
    private final K2636474TableCard upcomingCard;

    /**
     * Creates the page.
     *
     * @param context UI context
     */
    public K2636474OverviewPage(K2636474UiContext context) {
        super("Overview", "Today's snapshot of rooms and reservations.");
        this.context = context;

        JPanel stats = new JPanel(new GridLayout(1, 5, K2636474UiTheme.GAP, 0));
        stats.setOpaque(false);
        stats.add(statCard("door", "Total rooms", totalRoomsValue, K2636474UiTheme.PRIMARY));
        stats.add(statCard("check", "Available", availableValue, K2636474UiTheme.STATUS_AVAILABLE));
        stats.add(statCard("status", "Under maintenance", maintenanceValue, K2636474UiTheme.STATUS_MAINTENANCE));
        stats.add(statCard("calendar", "Active bookings", activeBookingsValue, K2636474UiTheme.ACCENT));
        stats.add(statCard("users", "Check-ins this week", checkInsValue, K2636474UiTheme.STATUS_RESERVED));
        getBody().add(stats, BorderLayout.NORTH);

        JTable upcomingTable = new JTable(upcomingModel);
        upcomingTable.setRowSorter(new TableRowSorter<>(upcomingModel));
        K2636474BookingTableModel.configure(upcomingTable);
        upcomingCard = new K2636474TableCard(upcomingTable, "calendar", "No check-ins this week",
                "Arrivals for the next 7 days will appear here.");
        JPanel upcoming = new JPanel(new BorderLayout(0, K2636474UiTheme.GRID + 4));
        upcoming.setOpaque(false);
        upcoming.add(K2636474UiTheme.sectionTitle("Arrivals in the next 7 days"), BorderLayout.NORTH);
        upcoming.add(upcomingCard, BorderLayout.CENTER);
        getBody().add(upcoming, BorderLayout.CENTER);
    }

    private static JLabel statValueLabel() {
        return K2636474UiTheme.label("0", K2636474UiTheme.font(Font.BOLD, 28), K2636474UiTheme.TEXT);
    }

    private static JPanel statCard(String iconName, String caption, JLabel valueLabel, Color color) {
        K2636474CardPanel card = new K2636474CardPanel(new BorderLayout(0, 12));
        card.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        K2636474CardPanel badge = new K2636474CardPanel(new BorderLayout());
        badge.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 28));
        badge.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        badge.add(new JLabel(K2636474UiTheme.icon(iconName, 18, color)));
        JPanel badgeRow = new JPanel(new BorderLayout());
        badgeRow.setOpaque(false);
        badgeRow.add(badge, BorderLayout.WEST);
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(valueLabel);
        text.add(Box.createVerticalStrut(2));
        text.add(K2636474UiTheme.subtitle(caption));
        card.add(badgeRow, BorderLayout.NORTH);
        card.add(text, BorderLayout.CENTER);
        return card;
    }

    @Override
    public void refresh() {
        totalRoomsValue.setText(String.valueOf(context.getRoomService().getAllRooms().size()));
        availableValue.setText(String.valueOf(context.getRoomService().countByStatus(K2636474RoomStatus.AVAILABLE)));
        maintenanceValue.setText(String.valueOf(
                context.getRoomService().countByStatus(K2636474RoomStatus.UNDER_MAINTENANCE)));
        activeBookingsValue.setText(String.valueOf(context.getBookingService().countActiveBookings()));
        List<K2636474Booking> upcoming = context.getBookingService().getUpcomingCheckIns();
        checkInsValue.setText(String.valueOf(upcoming.size()));
        upcomingModel.setBookings(upcoming);
        upcomingCard.updateView();
    }
}
