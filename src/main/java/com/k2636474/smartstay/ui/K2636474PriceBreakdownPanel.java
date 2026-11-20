package com.k2636474.smartstay.ui;

import com.k2636474.smartstay.model.K2636474Invoice;
import com.k2636474.smartstay.model.room.K2636474RoomPackage;
import com.k2636474.smartstay.util.K2636474Format;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

/**
 * Receipt-style price card: one line per charge (label left, amount right), the
 * discount in green, and the final payable amount large under a gold divider.
 * It only displays an invoice produced by the invoice calculator; it never does maths.
 */
public class K2636474PriceBreakdownPanel extends K2636474CardPanel {

    private final JPanel lines = new JPanel();
    private final String discountPolicyDescription;

    /**
     * Creates an empty breakdown card.
     *
     * @param discountPolicyDescription text explaining the discount, shown when none applies
     */
    public K2636474PriceBreakdownPanel(String discountPolicyDescription) {
        super(new BorderLayout(0, K2636474UiTheme.GAP));
        this.discountPolicyDescription = discountPolicyDescription;
        setBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22));
        add(K2636474UiTheme.sectionTitle("Price breakdown"), BorderLayout.NORTH);
        lines.setOpaque(false);
        lines.setLayout(new BoxLayout(lines, BoxLayout.Y_AXIS));
        add(lines, BorderLayout.CENTER);
    }

    /**
     * Shows every line of an invoice.
     *
     * @param invoice invoice (or preview) to display
     */
    public void showInvoice(K2636474Invoice invoice) {
        lines.removeAll();
        K2636474RoomPackage roomPackage = invoice.getBooking().getRoom().getRoomPackage();
        addLine("Room charge", invoice.getNights() + " night" + (invoice.getNights() == 1 ? "" : "s") + " × "
                + K2636474Format.lkr(roomPackage.getDailyRate()), K2636474Format.lkr(invoice.getBaseCharge()),
                K2636474UiTheme.TEXT);
        String extraDetail = invoice.getExtraGuests() == 0 ? "None — within base capacity"
                : invoice.getExtraGuests() + " × " + K2636474Format.lkr(roomPackage.getExtraGuestCharge())
                + " × " + invoice.getNights() + " nights";
        addLine("Extra guests", extraDetail, K2636474Format.lkr(invoice.getExtraGuestCharge()), K2636474UiTheme.TEXT);
        if (invoice.hasDiscount()) {
            addLine("Long-stay discount", K2636474Format.percent(invoice.getDiscountRate()) + " off room charge",
                    "− " + K2636474Format.lkr(invoice.getDiscountAmount()), K2636474UiTheme.SUCCESS);
        } else {
            addLine("Discount", discountPolicyDescription, "None", K2636474UiTheme.TEXT_SECONDARY);
        }
        addLine("Tax", K2636474Format.percent(invoice.getTaxRate()) + " of "
                + K2636474Format.lkr(invoice.getTaxableAmount()), K2636474Format.lkr(invoice.getTaxAmount()),
                K2636474UiTheme.TEXT);
        addDivider(K2636474UiTheme.BORDER, 1);
        addLine("Total for the stay", null, K2636474Format.lkr(invoice.getGrossTotal()), K2636474UiTheme.TEXT);
        String advanceLabel = invoice.getAdvanceDeducted() > 0 ? "Advance paid" : "Advance";
        addLine(advanceLabel, invoice.getAdvanceDeducted() > 0 ? "Recorded when booking is confirmed"
                : "Refunded / not deducted", "− " + K2636474Format.lkr(invoice.getAdvanceDeducted()),
                K2636474UiTheme.TEXT);
        lines.add(Box.createVerticalStrut(K2636474UiTheme.GRID));
        addDivider(K2636474UiTheme.ACCENT, 2);
        lines.add(Box.createVerticalStrut(K2636474UiTheme.GRID));
        addFinalLine(invoice);
        refreshLayout();
    }

    /**
     * Replaces the breakdown with a message, e.g. when the dates are invalid.
     *
     * @param message message to show
     */
    public void showMessage(String message) {
        lines.removeAll();
        JLabel label = K2636474UiTheme.label("<html><div style='width:240px'>" + message + "</div></html>",
                K2636474UiTheme.font(Font.PLAIN, K2636474UiTheme.BODY_SIZE), K2636474UiTheme.ERROR);
        label.setIcon(K2636474UiTheme.icon("info", 16, K2636474UiTheme.ERROR));
        label.setAlignmentX(LEFT_ALIGNMENT);
        lines.add(label);
        refreshLayout();
    }

    private void addLine(String label, String detail, String amount, Color amountColor) {
        JPanel row = new JPanel(new BorderLayout(K2636474UiTheme.GAP, 0));
        row.setOpaque(false);
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(K2636474UiTheme.label(label, K2636474UiTheme.font(Font.PLAIN, K2636474UiTheme.BODY_SIZE),
                amountColor == K2636474UiTheme.SUCCESS ? K2636474UiTheme.SUCCESS : K2636474UiTheme.TEXT));
        if (detail != null) {
            left.add(K2636474UiTheme.caption(detail));
        }
        row.add(left, BorderLayout.CENTER);
        JLabel amountLabel = K2636474UiTheme.label(amount, K2636474UiTheme.semibold(K2636474UiTheme.BODY_SIZE),
                amountColor);
        amountLabel.setVerticalAlignment(JLabel.TOP);
        row.add(amountLabel, BorderLayout.EAST);
        addRow(row);
        lines.add(Box.createVerticalStrut(10));
    }

    private void addFinalLine(K2636474Invoice invoice) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        String label = invoice.isRefundDue() ? "Refund due" : "Pay at the hotel";
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(K2636474UiTheme.label(label, K2636474UiTheme.semibold(14), K2636474UiTheme.TEXT));
        left.add(K2636474UiTheme.caption("Final payable"));
        row.add(left, BorderLayout.WEST);
        row.add(K2636474UiTheme.label(K2636474Format.lkr(Math.abs(invoice.getFinalPayable())),
                K2636474UiTheme.font(Font.BOLD, K2636474UiTheme.MONEY_LARGE_SIZE), K2636474UiTheme.PRIMARY),
                BorderLayout.EAST);
        addRow(row);
    }

    private void addDivider(Color color, int thickness) {
        JPanel divider = new JPanel();
        divider.setBackground(color);
        divider.setPreferredSize(new Dimension(1, thickness));
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, thickness));
        addRow(divider);
        lines.add(Box.createVerticalStrut(10));
    }

    private void addRow(JComponent row) {
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, row.getPreferredSize().height));
        lines.add(row);
    }

    private void refreshLayout() {
        lines.revalidate();
        lines.repaint();
    }
}
