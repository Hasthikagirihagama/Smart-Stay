package com.k2636474.smartstay.app;

import com.k2636474.smartstay.pricing.K2636474InvoiceCalculator;
import com.k2636474.smartstay.pricing.K2636474LongStayDiscountPolicy;
import com.k2636474.smartstay.service.K2636474AuthService;
import com.k2636474.smartstay.service.K2636474BookingService;
import com.k2636474.smartstay.service.K2636474DataStore;
import com.k2636474.smartstay.service.K2636474InvoiceService;
import com.k2636474.smartstay.service.K2636474RoomService;
import com.k2636474.smartstay.ui.K2636474MainFrame;
import com.k2636474.smartstay.ui.K2636474UiTheme;

import javax.swing.SwingUtilities;
import java.time.Clock;
import java.time.LocalDate;

/**
 * Entry point: wires the services together (constructor injection), loads the demo
 * data and opens the main window on the Swing event thread.
 */
public final class K2636474SmartStayApp {

    private K2636474SmartStayApp() {
    }

    /**
     * Starts SmartStay.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        Clock clock = Clock.systemDefaultZone();
        K2636474DataStore dataStore = K2636474DataStore.getInstance();
        K2636474AuthService authService = new K2636474AuthService(dataStore);
        K2636474RoomService roomService = new K2636474RoomService(dataStore, clock);
        K2636474InvoiceService invoiceService = new K2636474InvoiceService(dataStore,
                new K2636474InvoiceCalculator(new K2636474LongStayDiscountPolicy()), clock);
        K2636474BookingService bookingService = new K2636474BookingService(dataStore, roomService,
                invoiceService, clock);

        new K2636474DataSeeder(authService, roomService, bookingService).seed(LocalDate.now(clock));

        SwingUtilities.invokeLater(() -> {
            K2636474UiTheme.install();
            new K2636474MainFrame(authService, roomService, bookingService, invoiceService).setVisible(true);
        });
    }
}
