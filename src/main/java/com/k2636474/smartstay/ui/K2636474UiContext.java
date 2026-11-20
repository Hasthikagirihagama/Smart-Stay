package com.k2636474.smartstay.ui;

import com.k2636474.smartstay.model.K2636474Guest;
import com.k2636474.smartstay.model.K2636474User;
import com.k2636474.smartstay.service.K2636474AuthService;
import com.k2636474.smartstay.service.K2636474BookingService;
import com.k2636474.smartstay.service.K2636474InvoiceService;
import com.k2636474.smartstay.service.K2636474RoomService;

import java.awt.Window;

/**
 * Everything a screen needs, handed down from the main frame: the services (the UI
 * only ever talks to services), the window for dialogs, and the toast for feedback.
 */
public class K2636474UiContext {

    private final K2636474AuthService authService;
    private final K2636474RoomService roomService;
    private final K2636474BookingService bookingService;
    private final K2636474InvoiceService invoiceService;
    private final Window window;
    private K2636474Toast toast;

    /**
     * Creates the context.
     *
     * @param authService    authentication service
     * @param roomService    room service
     * @param bookingService booking service
     * @param invoiceService invoice service
     * @param window         main window, parent for dialogs
     */
    public K2636474UiContext(K2636474AuthService authService, K2636474RoomService roomService,
                             K2636474BookingService bookingService, K2636474InvoiceService invoiceService,
                             Window window) {
        this.authService = authService;
        this.roomService = roomService;
        this.bookingService = bookingService;
        this.invoiceService = invoiceService;
        this.window = window;
    }

    /**
     * Shows a non-blocking success message in the current dashboard.
     *
     * @param message message to show
     */
    public void toast(String message) {
        if (toast != null) {
            toast.showMessage(message);
        }
    }

    /**
     * Sets which toast receives messages (the visible dashboard's).
     *
     * @param toast the toast component
     */
    public void setToast(K2636474Toast toast) {
        this.toast = toast;
    }

    /**
     * Returns the logged-in user.
     *
     * @return current user, or null
     */
    public K2636474User getCurrentUser() {
        return authService.getCurrentUser();
    }

    /**
     * Returns the logged-in guest.
     *
     * @return current user as a guest, or null if the user is not a guest
     */
    public K2636474Guest getCurrentGuest() {
        return authService.getCurrentUser() instanceof K2636474Guest guest ? guest : null;
    }

    /**
     * Returns the authentication service.
     *
     * @return auth service
     */
    public K2636474AuthService getAuthService() {
        return authService;
    }

    /**
     * Returns the room service.
     *
     * @return room service
     */
    public K2636474RoomService getRoomService() {
        return roomService;
    }

    /**
     * Returns the booking service.
     *
     * @return booking service
     */
    public K2636474BookingService getBookingService() {
        return bookingService;
    }

    /**
     * Returns the invoice service.
     *
     * @return invoice service
     */
    public K2636474InvoiceService getInvoiceService() {
        return invoiceService;
    }

    /**
     * Returns the main window, used as the parent of dialogs.
     *
     * @return main window
     */
    public Window getWindow() {
        return window;
    }
}
