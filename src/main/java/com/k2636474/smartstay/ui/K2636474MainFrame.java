package com.k2636474.smartstay.ui;

import com.k2636474.smartstay.model.K2636474Guest;
import com.k2636474.smartstay.model.K2636474User;
import com.k2636474.smartstay.model.enums.K2636474UserRole;
import com.k2636474.smartstay.service.K2636474AuthService;
import com.k2636474.smartstay.service.K2636474BookingService;
import com.k2636474.smartstay.service.K2636474InvoiceService;
import com.k2636474.smartstay.service.K2636474RoomService;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.Dimension;

/**
 * The application window. A {@link CardLayout} switches between the login,
 * registration and dashboard screens; dashboards are rebuilt on each login so they
 * always belong to the current user.
 */
public class K2636474MainFrame extends JFrame {

    private static final String LOGIN_CARD = "login";
    private static final String REGISTER_CARD = "register";
    private static final String DASHBOARD_CARD = "dashboard";
    private static final Dimension MINIMUM_SIZE = new Dimension(1200, 760);

    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);
    private final K2636474UiContext context;
    private final K2636474LoginPanel loginPanel;
    private final K2636474RegisterPanel registerPanel;
    private K2636474DashboardShell dashboard;

    /**
     * Creates the main window.
     *
     * @param authService    authentication service
     * @param roomService    room service
     * @param bookingService booking service
     * @param invoiceService invoice service
     */
    public K2636474MainFrame(K2636474AuthService authService, K2636474RoomService roomService,
                             K2636474BookingService bookingService, K2636474InvoiceService invoiceService) {
        super("SmartStay — Hotel Booking");
        context = new K2636474UiContext(authService, roomService, bookingService, invoiceService, this);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setIconImages(K2636474UiTheme.appIcons());
        setMinimumSize(MINIMUM_SIZE);
        setSize(new Dimension(1280, 800));
        setLocationRelativeTo(null);

        loginPanel = new K2636474LoginPanel(context, this::showDashboard, this::showRegister);
        registerPanel = new K2636474RegisterPanel(context, this::onRegistered, () -> showLogin(null, null));
        root.add(loginPanel, LOGIN_CARD);
        root.add(registerPanel, REGISTER_CARD);
        setContentPane(root);
        showLogin(null, null);
    }

    /**
     * Shows the login screen.
     *
     * @param message message to show above the form, or null
     * @param loginId login to pre-fill, or null
     */
    public void showLogin(String message, String loginId) {
        loginPanel.reset(message, loginId);
        cards.show(root, LOGIN_CARD);
    }

    /**
     * Shows the registration screen with an empty form.
     */
    public void showRegister() {
        registerPanel.reset();
        cards.show(root, REGISTER_CARD);
    }

    private void onRegistered(K2636474Guest guest) {
        showLogin("Account created. Welcome, " + guest.getFullName() + " — please log in.", guest.getEmail());
    }

    private void showDashboard(K2636474User user) {
        if (dashboard != null) {
            root.remove(dashboard);
        }
        dashboard = user.getRole() == K2636474UserRole.ADMIN
                ? new K2636474AdminDashboard(context, this::logout)
                : new K2636474GuestDashboard(context, this::logout);
        root.add(dashboard, DASHBOARD_CARD);
        cards.show(root, DASHBOARD_CARD);
    }

    private void logout() {
        context.getAuthService().logout();
        context.setToast(null);
        showLogin("You have been logged out.", null);
    }
}
