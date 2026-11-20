package com.k2636474.smartstay.uitest;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JToolTip;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import java.awt.AWTException;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.image.BufferedImage;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Window;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Drives the real Swing UI like a user would: finds components on screen, clicks
 * buttons on the event dispatch thread, waits for modal dialogs and captures what
 * is on screen with {@link Robot}. Test code only; it never changes app behaviour.
 */
final class K2636474UiDriver {

    /** Time given to painting and animations after each action before reading or capturing. */
    static final long SETTLE_MILLIS = 700;
    private static final long TIMEOUT_MILLIS = 15_000;
    private static final long POLL_MILLIS = 100;

    private final Robot robot;
    private final Path screenshotDir;

    /**
     * Creates a driver that writes screenshots to a folder.
     *
     * @param screenshotDir folder for PNG files
     * @throws AWTException if the platform does not allow screen capture
     */
    K2636474UiDriver(Path screenshotDir) throws AWTException {
        this.robot = new Robot();
        this.screenshotDir = screenshotDir;
    }

    // ---- Event dispatch thread ------------------------------------------------------------

    static <T> T onEdt(Callable<T> task) {
        if (SwingUtilities.isEventDispatchThread()) {
            try {
                return task.call();
            } catch (RuntimeException error) {
                throw error;
            } catch (Exception error) {
                throw new IllegalStateException(error);
            }
        }
        AtomicReference<T> result = new AtomicReference<>();
        AtomicReference<Throwable> failure = new AtomicReference<>();
        try {
            SwingUtilities.invokeAndWait(() -> {
                try {
                    result.set(task.call());
                } catch (Throwable error) {
                    failure.set(error);
                }
            });
        } catch (Exception error) {
            throw new IllegalStateException(error);
        }
        if (failure.get() != null) {
            throw failure.get() instanceof RuntimeException runtime ? runtime : new IllegalStateException(failure.get());
        }
        return result.get();
    }

    static void runOnEdt(Runnable task) {
        onEdt(() -> {
            task.run();
            return null;
        });
    }

    /** Clicks a button whose action finishes without opening a modal dialog. */
    static void click(AbstractButton button) {
        runOnEdt(button::doClick);
    }

    /** Clicks a button that opens a modal dialog, which would block a synchronous call. */
    static void clickAsync(AbstractButton button) {
        SwingUtilities.invokeLater(button::doClick);
    }

    void settle() {
        sleep(SETTLE_MILLIS);
        runOnEdt(() -> { });
    }

    static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException error) {
            Thread.currentThread().interrupt();
        }
    }

    static <T> T waitFor(Supplier<T> probe, String what) {
        long deadline = System.currentTimeMillis() + TIMEOUT_MILLIS;
        while (System.currentTimeMillis() < deadline) {
            T value = probe.get();
            if (value != null) {
                return value;
            }
            sleep(POLL_MILLIS);
        }
        throw new IllegalStateException("Timed out waiting for " + what);
    }

    // ---- Finding components ---------------------------------------------------------------

    /** Reads a private field, used to reach a panel's own components without changing the app. */
    static Object field(Object target, String name) {
        for (Class<?> type = target.getClass(); type != null; type = type.getSuperclass()) {
            try {
                Field field = type.getDeclaredField(name);
                field.setAccessible(true);
                return field.get(target);
            } catch (NoSuchFieldException ignored) {
                // Keep looking in the superclass.
            } catch (IllegalAccessException error) {
                throw new IllegalStateException(error);
            }
        }
        throw new IllegalArgumentException(target.getClass().getSimpleName() + " has no field " + name);
    }

    /** Every showing component of a type under a root (null root = every window). */
    static <T extends Component> List<T> findAll(Container root, Class<T> type, Predicate<? super T> filter) {
        return onEdt(() -> {
            List<T> found = new ArrayList<>();
            if (root == null) {
                for (Window window : Window.getWindows()) {
                    collect(window, type, filter, found, true);
                }
            } else {
                collect(root, type, filter, found, true);
            }
            return found;
        });
    }

    static <T extends Component> T first(Container root, Class<T> type, Predicate<? super T> filter) {
        List<T> found = findAll(root, type, filter);
        return found.isEmpty() ? null : found.get(0);
    }

    static <T extends Component> T find(Container root, Class<T> type, Predicate<? super T> filter) {
        return waitFor(() -> first(root, type, filter), "a showing " + type.getSimpleName());
    }

    static JButton button(Container root, String text) {
        return waitFor(() -> first(root, JButton.class, button -> text.equals(button.getText())),
                "button \"" + text + "\"");
    }

    private static <T extends Component> void collect(Component component, Class<T> type,
                                                      Predicate<? super T> filter, List<T> found,
                                                      boolean showingOnly) {
        if (showingOnly && !component.isShowing()) {
            return;
        }
        if (type.isInstance(component) && filter.test(type.cast(component))) {
            found.add(type.cast(component));
        }
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                collect(child, type, filter, found, showingOnly);
            }
        }
    }

    /** Texts of every showing label under a root, in layout order. */
    static List<String> labelTexts(Container root) {
        return findAll(root, JLabel.class, label -> true).stream()
                .map(JLabel::getText).filter(text -> text != null && !text.isBlank()).map(K2636474UiDriver::plain)
                .toList();
    }

    // ---- Dialogs ---------------------------------------------------------------------------

    static JDialog dialogNow(String titlePart) {
        return onEdt(() -> {
            JDialog match = null;
            for (Window window : Window.getWindows()) {
                if (window instanceof JDialog dialog && dialog.isShowing() && dialog.getTitle() != null
                        && dialog.getTitle().contains(titlePart)) {
                    match = dialog;
                }
            }
            return match;
        });
    }

    JDialog dialog(String titlePart) {
        JDialog dialog = waitFor(() -> dialogNow(titlePart), "dialog \"" + titlePart + "\"");
        settle();
        return dialog;
    }

    static String optionMessage(JDialog dialog) {
        return onEdt(() -> plain(String.valueOf(first(dialog, JOptionPane.class, pane -> true).getMessage())));
    }

    /** Presses a button in a dialog and waits for that dialog to close. */
    void press(JDialog dialog, String buttonText) {
        clickAsync(button(dialog, buttonText));
        waitFor(() -> onEdt(() -> dialog.isShowing() ? null : Boolean.TRUE), "dialog to close");
        settle();
    }

    /** Disposes any dialog or popup left open, so one failed test cannot block the next. */
    static void closeStrayWindows() {
        runOnEdt(() -> {
            MenuSelectionManager.defaultManager().clearSelectedPath();
            for (Window window : Window.getWindows()) {
                if (window instanceof JDialog && window.isShowing()) {
                    window.dispose();
                }
            }
        });
    }

    // ---- Tables ----------------------------------------------------------------------------

    /** Rows of a table as the user sees them (rendered text, in view order). */
    static List<List<String>> rows(JTable table) {
        return onEdt(() -> {
            List<List<String>> rows = new ArrayList<>();
            for (int row = 0; row < table.getRowCount(); row++) {
                List<String> cells = new ArrayList<>();
                for (int column = 0; column < table.getColumnCount(); column++) {
                    cells.add(cellText(table, row, column));
                }
                rows.add(cells);
            }
            return rows;
        });
    }

    private static String cellText(JTable table, int row, int column) {
        Component rendered = table.prepareRenderer(table.getCellRenderer(row, column), row, column);
        List<JLabel> labels = new ArrayList<>();
        collect(rendered, JLabel.class, label -> true, labels, false);
        return labels.isEmpty() ? String.valueOf(table.getValueAt(row, column)) : plain(labels.get(0).getText());
    }

    static void selectRow(JTable table, String firstColumnValue) {
        runOnEdt(() -> {
            for (int row = 0; row < table.getRowCount(); row++) {
                if (firstColumnValue.equals(String.valueOf(table.getValueAt(row, 0)))) {
                    table.setRowSelectionInterval(row, row);
                    table.scrollRectToVisible(table.getCellRect(row, 0, true));
                    return;
                }
            }
            throw new IllegalStateException(firstColumnValue + " is not in the table");
        });
    }

    // ---- Mouse, tooltips and screenshots ---------------------------------------------------

    /**
     * Gives a window keyboard focus with a real click on a harmless point, because Swing
     * only shows tooltips in the focused window.
     */
    void focus(Window window, int offsetX, int offsetY) {
        if (onEdt(window::isFocused)) {
            return;
        }
        Point point = onEdt(window::getLocationOnScreen);
        robot.mouseMove(point.x + offsetX, point.y + offsetY);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        waitFor(() -> onEdt(window::isFocused) ? Boolean.TRUE : null, "the app window to get focus");
    }

    /** Moves the pointer over a component and returns the tooltip that appears. */
    String hoverForTooltip(JComponent component) {
        Point centre = onEdt(() -> {
            Point origin = component.getLocationOnScreen();
            origin.translate(component.getWidth() / 2, component.getHeight() / 2);
            return origin;
        });
        robot.mouseMove(centre.x, centre.y);
        // Also tell the tooltip manager directly, in case the desktop ignores synthetic pointer moves.
        runOnEdt(() -> {
            ToolTipManager manager = ToolTipManager.sharedInstance();
            long now = System.currentTimeMillis();
            int x = component.getWidth() / 2;
            int y = component.getHeight() / 2;
            manager.mouseEntered(new MouseEvent(component, MouseEvent.MOUSE_ENTERED, now, 0, x, y, 0, false));
            manager.mouseMoved(new MouseEvent(component, MouseEvent.MOUSE_MOVED, now, 0, x, y, 0, false));
        });
        JToolTip tip = waitFor(() -> first(null, JToolTip.class, tooltip -> true), "tooltip");
        settle();
        return onEdt(tip::getTipText);
    }

    /** Moves the pointer off the app so hover effects do not leak into later screenshots. */
    void parkMouse() {
        Rectangle screen = screenBounds();
        robot.mouseMove(screen.x + screen.width - 2, screen.y + screen.height / 2);
        runOnEdt(() -> ToolTipManager.sharedInstance().mouseExited(
                new MouseEvent(new JLabel(), MouseEvent.MOUSE_EXITED, System.currentTimeMillis(), 0, 0, 0, 0, false)));
    }

    /** Captures the area covered by every showing app window (frame, dialogs, tooltips). */
    void screenshot(String name) {
        List<Rectangle> windows = onEdt(() -> {
            List<Rectangle> bounds = new ArrayList<>();
            for (Window window : Window.getWindows()) {
                if (window.isShowing()) {
                    bounds.add(window.getBounds());
                }
            }
            return bounds;
        });
        if (windows.isEmpty()) {
            return;
        }
        Rectangle area = windows.stream().reduce(Rectangle::union).orElseThrow().intersection(screenBounds());
        BufferedImage capture = robot.createScreenCapture(area);
        // Keep only pixels under the app's own windows; other desktop content (e.g. beside a
        // tooltip that sticks out of the frame) becomes transparent.
        BufferedImage image = new BufferedImage(area.width, area.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        for (Rectangle window : windows) {
            Rectangle visible = window.intersection(area);
            graphics.setClip(visible.x - area.x, visible.y - area.y, visible.width, visible.height);
            graphics.drawImage(capture, 0, 0, null);
        }
        graphics.dispose();
        try {
            ImageIO.write(image, "png", screenshotDir.resolve(name + ".png").toFile());
        } catch (IOException error) {
            throw new IllegalStateException("Could not save screenshot " + name, error);
        }
    }

    private static Rectangle screenBounds() {
        Rectangle bounds = new Rectangle();
        for (GraphicsDevice device : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
            bounds = bounds.union(device.getDefaultConfiguration().getBounds());
        }
        return bounds;
    }

    /** Turns label/dialog HTML into plain single-line text. */
    static String plain(String text) {
        return text.replaceAll("(?i)<br\\s*/?>", " ").replaceAll("<[^>]*>", "").replace('\n', ' ')
                .replaceAll("\\s+", " ").trim();
    }
}
