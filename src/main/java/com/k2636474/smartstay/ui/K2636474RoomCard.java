package com.k2636474.smartstay.ui;

import com.k2636474.smartstay.model.K2636474Room;
import com.k2636474.smartstay.model.room.K2636474RoomPackage;
import com.k2636474.smartstay.util.K2636474Format;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.util.function.Consumer;

/**
 * Search-result card for one available room: coloured strip per room type, room
 * details, breakfast badge, nightly price and a Book button.
 */
public class K2636474RoomCard extends K2636474CardPanel {

    private static final int STRIP_HEIGHT = 6;
    private final K2636474Room room;

    /**
     * Creates a card for a room.
     *
     * @param room   the room to show
     * @param onBook called with the room when "Book" is pressed
     */
    public K2636474RoomCard(K2636474Room room, Consumer<K2636474Room> onBook) {
        super(new BorderLayout(0, K2636474UiTheme.GAP));
        this.room = room;
        K2636474RoomPackage roomPackage = room.getRoomPackage();
        setBorder(BorderFactory.createEmptyBorder(STRIP_HEIGHT + 16, 20, 20, 20));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(K2636474UiTheme.label("ROOM " + room.getRoomId(), K2636474UiTheme.semibold(11),
                K2636474UiTheme.TEXT_SECONDARY), BorderLayout.WEST);
        if (roomPackage.includesBreakfast()) {
            K2636474PillLabel breakfast = new K2636474PillLabel();
            breakfast.setPill("Breakfast", K2636474UiTheme.STATUS_AVAILABLE);
            breakfast.setIcon(K2636474UiTheme.icon("coffee", 12, K2636474UiTheme.STATUS_AVAILABLE));
            top.add(breakfast, BorderLayout.EAST);
        }

        JPanel details = new JPanel();
        details.setOpaque(false);
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        details.add(K2636474UiTheme.label(room.getRoomType().getDisplayName(),
                K2636474UiTheme.font(Font.BOLD, 18), K2636474UiTheme.TEXT));
        details.add(Box.createVerticalStrut(10));
        details.add(detailLine("bed", room.getBedType().getDisplayName() + " bed"));
        details.add(Box.createVerticalStrut(6));
        details.add(detailLine("users", "Sleeps " + roomPackage.getBaseCapacity() + " · up to "
                + room.getMaxOccupancy() + " guests"));
        details.add(Box.createVerticalStrut(6));
        details.add(detailLine("info", "+" + K2636474Format.lkr(roomPackage.getExtraGuestCharge())
                + " per extra guest/night"));

        JPanel north = new JPanel(new BorderLayout(0, 12));
        north.setOpaque(false);
        north.add(top, BorderLayout.NORTH);
        north.add(details, BorderLayout.CENTER);
        add(north, BorderLayout.CENTER);
        add(buildFooter(onBook), BorderLayout.SOUTH);
    }

    private JComponent buildFooter(Consumer<K2636474Room> onBook) {
        JPanel footer = new JPanel(new BorderLayout(0, 12));
        footer.setOpaque(false);
        JPanel price = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        price.setOpaque(false);
        price.add(K2636474UiTheme.label(K2636474Format.lkr(room.getDailyRate()),
                K2636474UiTheme.font(Font.BOLD, K2636474UiTheme.MONEY_LARGE_SIZE), K2636474UiTheme.PRIMARY));
        JLabel perNight = K2636474UiTheme.subtitle("  / night");
        price.add(perNight);
        footer.add(price, BorderLayout.NORTH);
        JButton bookButton = K2636474UiTheme.primaryButton("Book room " + room.getRoomId(), "calendar");
        bookButton.setToolTipText("See the full price and confirm this room");
        bookButton.addActionListener(event -> onBook.accept(room));
        footer.add(bookButton, BorderLayout.SOUTH);
        return footer;
    }

    private static JLabel detailLine(String iconName, String text) {
        JLabel label = K2636474UiTheme.label(text, K2636474UiTheme.font(Font.PLAIN, K2636474UiTheme.BODY_SIZE),
                K2636474UiTheme.TEXT_SECONDARY);
        label.setIcon(K2636474UiTheme.icon(iconName, 15, K2636474UiTheme.TEXT_SECONDARY));
        label.setIconTextGap(K2636474UiTheme.GRID);
        return label;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Clip to the card's rounded outline so the strip follows the top corners.
        g.clip(new RoundRectangle2D.Float(0, 0, getWidth() - 1f, getHeight() - 1f,
                K2636474UiTheme.CARD_ARC, K2636474UiTheme.CARD_ARC));
        g.setColor(K2636474UiTheme.roomTypeColor(room.getRoomType()));
        g.fillRect(0, 0, getWidth(), STRIP_HEIGHT);
        g.dispose();
    }
}
