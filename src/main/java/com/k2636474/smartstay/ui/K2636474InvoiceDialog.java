package com.k2636474.smartstay.ui;

import com.k2636474.smartstay.model.K2636474Invoice;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Shows an invoice as aligned monospaced text with Copy, Save as .txt and Close.
 */
public class K2636474InvoiceDialog extends JDialog {

    private static final int COPIED_FEEDBACK_MILLIS = 1500;
    private final String invoiceText;
    private final K2636474Invoice invoice;

    /**
     * Creates the dialog.
     *
     * @param context UI context
     * @param invoice invoice to show
     */
    public K2636474InvoiceDialog(K2636474UiContext context, K2636474Invoice invoice) {
        super(context.getWindow(), "Invoice " + invoice.getInvoiceId(), ModalityType.APPLICATION_MODAL);
        this.invoice = invoice;
        this.invoiceText = context.getInvoiceService().formatInvoice(invoice);

        JPanel content = new JPanel(new BorderLayout(0, K2636474UiTheme.GAP));
        content.setBackground(K2636474UiTheme.BACKGROUND);
        content.setBorder(BorderFactory.createEmptyBorder(K2636474UiTheme.PAGE_PADDING, K2636474UiTheme.PAGE_PADDING,
                K2636474UiTheme.GAP + 4, K2636474UiTheme.PAGE_PADDING));
        content.add(K2636474UiTheme.pageTitle("Invoice " + invoice.getInvoiceId()), BorderLayout.NORTH);

        JTextArea textArea = new JTextArea(invoiceText);
        textArea.setEditable(false);
        textArea.setFont(K2636474UiTheme.monospaced(13));
        textArea.setForeground(K2636474UiTheme.TEXT);
        textArea.setBackground(K2636474UiTheme.SURFACE);
        textArea.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        textArea.setCaretPosition(0);
        K2636474CardPanel card = new K2636474CardPanel(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        card.add(K2636474UiTheme.scroll(textArea));
        content.add(card, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, K2636474UiTheme.GRID + 4, 0));
        buttons.setOpaque(false);
        JButton copyButton = K2636474UiTheme.secondaryButton("Copy", "copy");
        copyButton.setToolTipText("Copy the invoice text to the clipboard");
        copyButton.addActionListener(event -> copyToClipboard(copyButton));
        JButton saveButton = K2636474UiTheme.secondaryButton("Save as .txt", "save");
        saveButton.setToolTipText("Save the invoice as a text file");
        saveButton.addActionListener(event -> saveToFile());
        JButton closeButton = K2636474UiTheme.primaryButton("Close", null);
        closeButton.setToolTipText("Close (Esc)");
        closeButton.addActionListener(event -> dispose());
        buttons.add(copyButton);
        buttons.add(saveButton);
        buttons.add(closeButton);
        content.add(buttons, BorderLayout.SOUTH);
        setContentPane(content);

        K2636474UiTheme.installDialogKeys(this, closeButton);
        setSize(680, 760);
        setLocationRelativeTo(context.getWindow());
    }

    private void copyToClipboard(JButton copyButton) {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(invoiceText), null);
        copyButton.setText("Copied");
        Timer reset = new Timer(COPIED_FEEDBACK_MILLIS, event -> copyButton.setText("Copy"));
        reset.setRepeats(false);
        reset.start();
    }

    private void saveToFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save invoice");
        chooser.setFileFilter(new FileNameExtensionFilter("Text files (*.txt)", "txt"));
        chooser.setSelectedFile(new File(invoice.getInvoiceId() + ".txt"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".txt")) {
            file = new File(file.getParentFile(), file.getName() + ".txt");
        }
        if (file.exists() && JOptionPane.showConfirmDialog(this, file.getName() + " already exists. Replace it?",
                "Replace file", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            Files.writeString(file.toPath(), invoiceText, StandardCharsets.UTF_8);
            K2636474UiTheme.showSuccess(this, "Invoice saved", "Saved to " + file.getAbsolutePath());
        } catch (IOException error) {
            JOptionPane.showMessageDialog(this, "The invoice could not be saved: " + error.getMessage(),
                    "Save failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
