package com.k2636474.smartstay.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.fonts.inter.FlatInterFont;
import com.k2636474.smartstay.exception.K2636474AuthenticationException;
import com.k2636474.smartstay.exception.K2636474BookingException;
import com.k2636474.smartstay.exception.K2636474NotFoundException;
import com.k2636474.smartstay.exception.K2636474ValidationException;
import com.k2636474.smartstay.model.enums.K2636474BookingStatus;
import com.k2636474.smartstay.model.enums.K2636474RoomStatus;
import com.k2636474.smartstay.model.enums.K2636474RoomType;
import com.k2636474.smartstay.util.K2636474Format;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Single source of the visual design: colours, fonts, spacing, FlatLaf tuning and
 * small factory helpers so every screen looks consistent. No other class hard-codes
 * a colour or font size.
 */
public final class K2636474UiTheme {

    // ----- Palette -----
    public static final Color PRIMARY = new Color(0x0F4C5C);
    public static final Color PRIMARY_HOVER = new Color(0x0B3A47);
    public static final Color PRIMARY_TINT = new Color(0xE3EEF0);
    public static final Color ACCENT = new Color(0xC9A227);
    public static final Color BACKGROUND = new Color(0xF7F5F0);
    public static final Color SURFACE = new Color(0xFFFFFF);
    public static final Color BORDER = new Color(0xE6E1D8);
    public static final Color TEXT = new Color(0x1E2A2F);
    public static final Color TEXT_SECONDARY = new Color(0x6B7780);
    public static final Color SIDEBAR_TEXT_MUTED = new Color(0xB9D0D6);
    public static final Color STATUS_AVAILABLE = new Color(0x2E7D5B);
    public static final Color STATUS_RESERVED = new Color(0xC98A1B);
    public static final Color STATUS_MAINTENANCE = new Color(0x7A828A);
    public static final Color STATUS_CANCELLED = new Color(0xB3443A);
    public static final Color SUCCESS = STATUS_AVAILABLE;
    public static final Color ERROR = STATUS_CANCELLED;
    public static final Color STANDARD_STRIP = new Color(0x5E8C99);
    public static final Color DELUXE_STRIP = PRIMARY;
    public static final Color SUITE_STRIP = ACCENT;
    public static final Color FAMILY_SUITE_STRIP = new Color(0x9A6B4F);

    // ----- Spacing (8px grid) -----
    public static final int GRID = 8;
    public static final int GAP = 16;
    public static final int PAGE_PADDING = 24;
    public static final int SIDEBAR_WIDTH = 240;
    public static final int CARD_ARC = 14;
    public static final int CONTROL_ARC = 10;

    // ----- Type scale -----
    public static final int TITLE_SIZE = 24;
    public static final int SECTION_SIZE = 16;
    public static final int BODY_SIZE = 13;
    public static final int CAPTION_SIZE = 11;
    public static final int MONEY_LARGE_SIZE = 22;

    /** How long a toast message stays on screen. */
    public static final int TOAST_MILLIS = 3000;

    private static final String ICON_FOLDER = "icons/";
    private static boolean interAvailable;

    private K2636474UiTheme() {
    }

    /**
     * Installs FlatLaf with the Inter font and the SmartStay tuning. Falls back to the
     * Nimbus look and feel if FlatLaf cannot be loaded.
     */
    public static void install() {
        try {
            FlatInterFont.install();
            FlatLaf.setPreferredFontFamily(FlatInterFont.FAMILY);
            interAvailable = true;
            UIManager.put("defaultFont", new FontUIResource(FlatInterFont.FAMILY, Font.PLAIN, BODY_SIZE));
            applyFlatLafTuning();
            FlatLightLaf.setup();
        } catch (RuntimeException | LinkageError flatLafFailure) {
            installNimbusFallback();
        }
    }

    private static void applyFlatLafTuning() {
        Map<String, Object> settings = Map.ofEntries(
                Map.entry("Component.arc", CONTROL_ARC),
                Map.entry("Button.arc", CONTROL_ARC),
                Map.entry("TextComponent.arc", CONTROL_ARC),
                Map.entry("CheckBox.arc", 6),
                Map.entry("Component.focusColor", PRIMARY),
                Map.entry("Component.focusedBorderColor", PRIMARY),
                Map.entry("Component.borderColor", BORDER),
                Map.entry("Component.focusWidth", 1),
                Map.entry("Panel.background", BACKGROUND),
                Map.entry("Table.rowHeight", 36),
                Map.entry("Table.showHorizontalLines", true),
                Map.entry("Table.showVerticalLines", false),
                Map.entry("Table.gridColor", new Color(0xF0ECE4)),
                Map.entry("Table.intercellSpacing", new Dimension(0, 1)),
                Map.entry("Table.selectionBackground", PRIMARY_TINT),
                Map.entry("Table.selectionForeground", TEXT),
                Map.entry("Table.selectionInactiveBackground", PRIMARY_TINT),
                Map.entry("Table.selectionInactiveForeground", TEXT),
                Map.entry("Table.cellMargins", new Insets(0, 12, 0, 12)),
                Map.entry("TableHeader.height", 38),
                Map.entry("TableHeader.background", SURFACE),
                Map.entry("TableHeader.foreground", TEXT_SECONDARY),
                Map.entry("TableHeader.separatorColor", SURFACE),
                Map.entry("TableHeader.bottomSeparatorColor", BORDER),
                Map.entry("TableHeader.cellMargins", new Insets(0, 12, 0, 12)),
                Map.entry("ScrollPane.background", SURFACE),
                Map.entry("ScrollBar.thumbArc", 999),
                Map.entry("ScrollBar.width", 10),
                Map.entry("ScrollBar.trackInsets", new Insets(2, 2, 2, 2)),
                Map.entry("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2)),
                Map.entry("ToolTip.background", TEXT),
                Map.entry("ToolTip.foreground", SURFACE),
                Map.entry("OptionPane.background", SURFACE),
                Map.entry("Spinner.buttonStyle", "button"),
                Map.entry("PopupMenu.borderColor", BORDER),
                Map.entry("MenuItem.selectionBackground", PRIMARY_TINT),
                Map.entry("MenuItem.selectionForeground", TEXT),
                Map.entry("ComboBox.selectionBackground", PRIMARY_TINT),
                Map.entry("ComboBox.selectionForeground", TEXT),
                Map.entry("List.selectionBackground", PRIMARY_TINT),
                Map.entry("List.selectionForeground", TEXT),
                Map.entry("Label.foreground", TEXT),
                Map.entry("TitlePane.background", SURFACE));
        settings.forEach(UIManager::put);
    }

    private static void installNimbusFallback() {
        try {
            for (UIManager.LookAndFeelInfo lookAndFeel : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(lookAndFeel.getName())) {
                    UIManager.setLookAndFeel(lookAndFeel.getClassName());
                    return;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ignored) {
            // The default Metal look and feel stays in place; the app still works.
        }
    }

    // ----- Fonts -----

    /**
     * Returns the UI font at a size and style.
     *
     * @param style {@link Font#PLAIN} or {@link Font#BOLD}
     * @param size  point size
     * @return font
     */
    public static Font font(int style, int size) {
        String family = interAvailable ? FlatInterFont.FAMILY : Font.SANS_SERIF;
        return new Font(family, style, size);
    }

    /**
     * Returns the semibold UI font (falls back to bold when Inter is missing).
     *
     * @param size point size
     * @return font
     */
    public static Font semibold(int size) {
        return interAvailable ? new Font(FlatInterFont.FAMILY_SEMIBOLD, Font.PLAIN, size) : font(Font.BOLD, size);
    }

    /**
     * Returns a monospaced font for aligned invoice text.
     *
     * @param size point size
     * @return font
     */
    public static Font monospaced(int size) {
        return new Font(Font.MONOSPACED, Font.PLAIN, size);
    }

    // ----- Labels -----

    /**
     * Creates a page title label (24 bold).
     *
     * @param text title text
     * @return label
     */
    public static JLabel pageTitle(String text) {
        return label(text, font(Font.BOLD, TITLE_SIZE), TEXT);
    }

    /**
     * Creates a section title label (16 semibold).
     *
     * @param text title text
     * @return label
     */
    public static JLabel sectionTitle(String text) {
        return label(text, semibold(SECTION_SIZE), TEXT);
    }

    /**
     * Creates a body-text label in the secondary colour.
     *
     * @param text label text
     * @return label
     */
    public static JLabel subtitle(String text) {
        return label(text, font(Font.PLAIN, BODY_SIZE), TEXT_SECONDARY);
    }

    /**
     * Creates a small caption label in the secondary colour.
     *
     * @param text caption text
     * @return label
     */
    public static JLabel caption(String text) {
        return label(text, font(Font.PLAIN, CAPTION_SIZE), TEXT_SECONDARY);
    }

    /**
     * Creates a form field label (12 semibold).
     *
     * @param text label text
     * @return label
     */
    public static JLabel fieldLabel(String text) {
        return label(text, semibold(12), TEXT);
    }

    /**
     * Creates a label with a font and colour.
     *
     * @param text  text
     * @param font  font
     * @param color foreground colour
     * @return label
     */
    public static JLabel label(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    // ----- Icons -----

    /**
     * Loads one of the bundled SVG icons tinted to a colour.
     *
     * @param name  icon file name without extension (e.g. "bed")
     * @param size  width and height in pixels
     * @param color tint colour
     * @return the icon
     */
    public static Icon icon(String name, int size, Color color) {
        FlatSVGIcon icon = new FlatSVGIcon(ICON_FOLDER + name + ".svg", size, size);
        icon.setColorFilter(new FlatSVGIcon.ColorFilter(original -> color));
        return icon;
    }

    /**
     * Paints the SmartStay app icon (teal tile with a gold roof mark) at several sizes.
     *
     * @return window icons from 16 to 128 pixels
     */
    public static List<Image> appIcons() {
        List<Image> icons = new ArrayList<>();
        for (int size : new int[]{16, 32, 48, 64, 128}) {
            BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = image.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(PRIMARY);
            g.fill(new RoundRectangle2D.Float(0, 0, size, size, size * 0.4f, size * 0.4f));
            paintBrandMark(g, size * 0.2f, size * 0.22f, size * 0.6f);
            g.dispose();
            icons.add(image);
        }
        return icons;
    }

    /**
     * Paints the gold brand mark: a roof line over a small bar, suggesting a stay.
     *
     * @param g    graphics to paint on
     * @param x    left edge
     * @param y    top edge
     * @param size width of the mark
     */
    public static void paintBrandMark(Graphics2D g, float x, float y, float size) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(ACCENT);
        g.setStroke(new BasicStroke(Math.max(1.5f, size * 0.12f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Path2D roof = new Path2D.Float();
        roof.moveTo(x, y + size * 0.55f);
        roof.lineTo(x + size / 2, y + size * 0.1f);
        roof.lineTo(x + size, y + size * 0.55f);
        g.draw(roof);
        g.fill(new RoundRectangle2D.Float(x + size * 0.3f, y + size * 0.62f, size * 0.4f, size * 0.22f,
                size * 0.1f, size * 0.1f));
    }

    // ----- Buttons -----

    /**
     * Creates a filled primary button.
     *
     * @param text     button text
     * @param iconName icon name, or null
     * @return button
     */
    public static JButton primaryButton(String text, String iconName) {
        return styledButton(text, iconName, SURFACE, "background:" + hex(PRIMARY) + ";foreground:#FFFFFF;"
                + "hoverBackground:" + hex(PRIMARY_HOVER) + ";pressedBackground:" + hex(PRIMARY_HOVER) + ";"
                + "borderWidth:0;focusWidth:0;innerFocusWidth:0;disabledBackground:#C9D3D6;"
                + "disabledText:#FFFFFF");
    }

    /**
     * Creates an outlined secondary button.
     *
     * @param text     button text
     * @param iconName icon name, or null
     * @return button
     */
    public static JButton secondaryButton(String text, String iconName) {
        return styledButton(text, iconName, TEXT, "background:#FFFFFF;foreground:" + hex(TEXT) + ";"
                + "hoverBackground:" + hex(BACKGROUND) + ";borderColor:" + hex(BORDER) + ";"
                + "focusWidth:0;innerFocusWidth:0");
    }

    /**
     * Creates an outlined button in the error colour for destructive actions.
     *
     * @param text     button text
     * @param iconName icon name, or null
     * @return button
     */
    public static JButton dangerButton(String text, String iconName) {
        return styledButton(text, iconName, ERROR, "background:#FFFFFF;foreground:" + hex(ERROR) + ";"
                + "hoverBackground:#FBEFEE;borderColor:#E9C6C2;focusWidth:0;innerFocusWidth:0");
    }

    /**
     * Creates a borderless text button that looks like a link.
     *
     * @param text link text
     * @return button
     */
    public static JButton linkButton(String text) {
        JButton button = styledButton(text, null, PRIMARY, "background:null;foreground:" + hex(PRIMARY) + ";"
                + "hoverBackground:null;pressedBackground:null;borderWidth:0;focusWidth:0;innerFocusWidth:0;"
                + "margin:0,0,0,0");
        button.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);
        button.setContentAreaFilled(false);
        button.setFont(semibold(BODY_SIZE));
        return button;
    }

    private static JButton styledButton(String text, String iconName, Color iconColor, String style) {
        JButton button = new JButton(text);
        if (iconName != null) {
            button.setIcon(icon(iconName, 16, iconColor));
            button.setIconTextGap(GRID);
            Color disabledColor = new Color(iconColor.getRed(), iconColor.getGreen(), iconColor.getBlue(), 90);
            button.setDisabledIcon(icon(iconName, 16, disabledColor));
        }
        button.putClientProperty(FlatClientProperties.STYLE, style + ";margin:7,16,7,16");
        button.setFont(semibold(BODY_SIZE));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        return button;
    }

    // ----- Containers and tables -----

    /**
     * Creates a white rounded card with the standard inner padding.
     *
     * @return card panel using a BorderLayout
     */
    public static K2636474CardPanel card() {
        K2636474CardPanel card = new K2636474CardPanel(new BorderLayout(GAP, GAP));
        card.setBorder(BorderFactory.createEmptyBorder(GAP + 4, GAP + 4, GAP + 4, GAP + 4));
        return card;
    }

    /**
     * Applies the SmartStay table style: row height, header font, no vertical lines.
     *
     * @param table table to style
     */
    public static void styleTable(JTable table) {
        table.setFillsViewportHeight(true);
        table.setBackground(SURFACE);
        table.setFont(font(Font.PLAIN, BODY_SIZE));
        table.getTableHeader().setFont(semibold(12));
        table.getTableHeader().setReorderingAllowed(false);
        table.setRowHeight(36);
        table.setShowVerticalLines(false);
        table.setAutoCreateRowSorter(false);
    }

    /**
     * Wraps a component in a borderless scroll pane on a white background.
     *
     * @param view component to scroll
     * @return scroll pane
     */
    public static JScrollPane scroll(Component view) {
        JScrollPane scrollPane = new JScrollPane(view);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(SURFACE);
        scrollPane.getVerticalScrollBar().setUnitIncrement(GAP);
        return scrollPane;
    }

    /**
     * Returns a right-aligned renderer that shows numbers as LKR amounts.
     *
     * @return money cell renderer
     */
    public static TableCellRenderer moneyRenderer() {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object value) {
                setText(value instanceof Number number ? K2636474Format.lkr(number.doubleValue()) : "");
            }
        };
        renderer.setHorizontalAlignment(SwingConstants.RIGHT);
        return renderer;
    }

    /**
     * Builds a friendly empty-state message with an icon.
     *
     * @param iconName icon name
     * @param title    headline
     * @param message  helpful hint
     * @return empty-state panel
     */
    public static JPanel emptyState(String iconName, String title, String message) {
        JPanel panel = new JPanel(new java.awt.GridBagLayout());
        panel.setBackground(SURFACE);
        JPanel stack = new JPanel();
        stack.setOpaque(false);
        stack.setLayout(new javax.swing.BoxLayout(stack, javax.swing.BoxLayout.Y_AXIS));
        JLabel iconLabel = new JLabel(icon(iconName, 40, BORDER.darker()));
        JLabel titleLabel = sectionTitle(title);
        JLabel messageLabel = subtitle(message);
        for (JComponent component : new JComponent[]{iconLabel, titleLabel, messageLabel}) {
            component.setAlignmentX(Component.CENTER_ALIGNMENT);
            stack.add(component);
            stack.add(javax.swing.Box.createVerticalStrut(GRID));
        }
        panel.add(stack);
        return panel;
    }

    // ----- Colours by meaning -----

    /**
     * Returns the colour for a room or booking status value.
     *
     * @param status a {@link K2636474RoomStatus} or {@link K2636474BookingStatus}
     * @return status colour (grey for unknown values)
     */
    public static Color statusColor(Object status) {
        if (status instanceof K2636474RoomStatus roomStatus) {
            return switch (roomStatus) {
                case AVAILABLE -> STATUS_AVAILABLE;
                case RESERVED -> STATUS_RESERVED;
                case UNDER_MAINTENANCE -> STATUS_MAINTENANCE;
            };
        }
        if (status instanceof K2636474BookingStatus bookingStatus) {
            return switch (bookingStatus) {
                case CONFIRMED -> PRIMARY;
                case CANCELLED -> STATUS_CANCELLED;
                case COMPLETED -> STATUS_MAINTENANCE;
            };
        }
        return STATUS_MAINTENANCE;
    }

    /**
     * Returns the strip colour used on room cards for a room type.
     *
     * @param type room type
     * @return colour
     */
    public static Color roomTypeColor(K2636474RoomType type) {
        return switch (type) {
            case STANDARD -> STANDARD_STRIP;
            case DELUXE -> DELUXE_STRIP;
            case SUITE -> SUITE_STRIP;
            case FAMILY_SUITE -> FAMILY_SUITE_STRIP;
        };
    }

    /**
     * Formats a colour as #RRGGBB for FlatLaf style strings.
     *
     * @param color colour
     * @return hex string
     */
    public static String hex(Color color) {
        return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
    }

    // ----- Dialog helpers -----

    /**
     * Makes Esc close a dialog and Enter press its default button.
     *
     * @param dialog        dialog to configure
     * @param defaultButton button triggered by Enter, or null
     */
    public static void installDialogKeys(JDialog dialog, JButton defaultButton) {
        dialog.getRootPane().setDefaultButton(defaultButton);
        dialog.getRootPane().registerKeyboardAction(event -> dialog.dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    /**
     * Shows an error dialog. Messages of the application's own exceptions are written
     * for users and shown as-is; anything unexpected gets a generic message so no
     * technical detail or stack trace ever reaches the user.
     *
     * @param parent parent component for positioning
     * @param error  the exception that was caught
     */
    public static void showError(Component parent, RuntimeException error) {
        boolean userFacing = error instanceof K2636474BookingException
                || error instanceof K2636474ValidationException
                || error instanceof K2636474NotFoundException
                || error instanceof K2636474AuthenticationException;
        String message = userFacing ? error.getMessage() : "Something went wrong. Please try again.";
        JOptionPane.showMessageDialog(parent, message, "Cannot complete action", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Shows an information dialog with a success icon.
     *
     * @param parent  parent component for positioning
     * @param title   dialog title
     * @param message message
     */
    public static void showSuccess(Component parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE,
                icon("check", 32, SUCCESS));
    }
}
