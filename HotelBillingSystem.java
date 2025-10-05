import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

public class HotelBillingSystem extends JFrame {
    private CardLayout cardLayout = new CardLayout();
    private JPanel cards = new JPanel(cardLayout);

    // Login components
    private JTextField userField;
    private JPasswordField passField;

    // Billing components (declare as fields so accessible in handlers)
    private JTextField nameField, contactField, foodField, drinkField;
    private JTextArea notesArea;
    private JComboBox<String> roomCombo, paymentCombo;
    private JSpinner nightsSpinner, roomsSpinner;
    private JCheckBox breakfastCB, wifiCB, laundryCB;
    private JTextArea billArea;

    // Room rates map
    private static final Map<String, Integer> ROOM_RATES = new LinkedHashMap<>();
    static {
        ROOM_RATES.put("Single (₹1,000/night)", 1000);
        ROOM_RATES.put("Double (₹1,800/night)", 1800);
        ROOM_RATES.put("Deluxe (₹3,000/night)", 3000);
        ROOM_RATES.put("Suite (₹5,000/night)", 5000);
    }

    public HotelBillingSystem() {
        setTitle("Hotel Billing System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // start maximized
        setMinimumSize(new Dimension(900, 600));
        setLayout(new BorderLayout());

        cards.add(createLoginPanel(), "login");
        cards.add(createBillingPanel(), "billing");

        add(cards, BorderLayout.CENTER);
        cardLayout.show(cards, "login");
    }

    // ---------- Login Panel ----------
    private JPanel createLoginPanel() {
        GradientPanel loginPanel = new GradientPanel(new Color(20, 40, 80), new Color(60, 90, 120));
        loginPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);

        JLabel title = new JLabel("Welcome to Hotel Billing");
        title.setFont(new Font("SansSerif", Font.BOLD, 36));
        title.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        loginPanel.add(title, gbc);

        gbc.gridwidth = 1;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(userLabel.getFont().deriveFont(16f));
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        loginPanel.add(userLabel, gbc);

        userField = new JTextField(16);
        gbc.gridx = 1; gbc.gridy = 1; gbc.anchor = GridBagConstraints.WEST;
        loginPanel.add(userField, gbc);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.WHITE);
        passLabel.setFont(passLabel.getFont().deriveFont(16f));
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        loginPanel.add(passLabel, gbc);

        passField = new JPasswordField(16);
        gbc.gridx = 1; gbc.gridy = 2; gbc.anchor = GridBagConstraints.WEST;
        loginPanel.add(passField, gbc);

        JButton loginBtn = new JButton("Login");
        loginBtn.setPreferredSize(new Dimension(140, 36));
        JButton exitBtn = new JButton("Exit");
        exitBtn.setPreferredSize(new Dimension(140, 36));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btns.setOpaque(false);
        btns.add(loginBtn);
        btns.add(exitBtn);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        loginPanel.add(btns, gbc);

        // Hard-coded credentials: admin / admin123
        loginBtn.addActionListener(e -> {
            String user = userField.getText().trim();
            String pass = new String(passField.getPassword());
            if (user.equals("admin") && pass.equals("admin123")) {
                // clear fields then show billing
                userField.setText("");
                passField.setText("");
                cardLayout.show(cards, "billing");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.\nTry admin / admin123", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        exitBtn.addActionListener(e -> System.exit(0));

        return loginPanel;
    }

    // ---------- Billing Panel ----------
    private JPanel createBillingPanel() {
        GradientPanel root = new GradientPanel(new Color(245, 245, 250), new Color(220, 230, 250));
        root.setLayout(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        JLabel heading = new JLabel("Hotel Billing Dashboard");
        heading.setFont(new Font("SansSerif", Font.BOLD, 26));
        heading.setBorder(new EmptyBorder(6, 6, 6, 6));
        topBar.add(heading, BorderLayout.WEST);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            clearAllInputs();
            cardLayout.show(cards, "login");
        });
        topBar.add(logoutBtn, BorderLayout.EAST);

        root.add(topBar, BorderLayout.NORTH);

        // Center split: left form, right bill area
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.45);
        split.setDividerSize(6);

        // Left form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new CompoundBorder(new TitledBorder("Customer & Booking Details"), new EmptyBorder(8,8,8,8)));
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Customer name
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        formPanel.add(new JLabel("Customer Name:"), gbc);
        nameField = new JTextField();
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.7;
        formPanel.add(nameField, gbc);

        // Contact
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        formPanel.add(new JLabel("Contact No:"), gbc);
        contactField = new JTextField();
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.7;
        formPanel.add(contactField, gbc);

        // Room type
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        formPanel.add(new JLabel("Room Type:"), gbc);
        roomCombo = new JComboBox<>(ROOM_RATES.keySet().toArray(new String[0]));
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.7;
        formPanel.add(roomCombo, gbc);

        // Rooms spinner
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        formPanel.add(new JLabel("No. of Rooms:"), gbc);
        roomsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0.7;
        formPanel.add(roomsSpinner, gbc);

        // Nights spinner
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.3;
        formPanel.add(new JLabel("No. of Nights:"), gbc);
        nightsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 365, 1));
        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 0.7;
        formPanel.add(nightsSpinner, gbc);

        // Extras checkboxes
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0.3;
        formPanel.add(new JLabel("Extras:"), gbc);
        JPanel extrasPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        extrasPanel.setOpaque(false);
        breakfastCB = new JCheckBox("Breakfast (₹200/room/night)");
        wifiCB = new JCheckBox("WiFi (₹100/room/night)");
        laundryCB = new JCheckBox("Laundry (₹150/room/stay)");
        extrasPanel.add(breakfastCB);
        extrasPanel.add(wifiCB);
        extrasPanel.add(laundryCB);
        gbc.gridx = 1; gbc.gridy = 5; gbc.weightx = 0.7;
        formPanel.add(extrasPanel, gbc);

        // Food & drinks fields
        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0.3;
        formPanel.add(new JLabel("Food Charges (₹):"), gbc);
        foodField = new JTextField("0");
        gbc.gridx = 1; gbc.gridy = 6; gbc.weightx = 0.7;
        formPanel.add(foodField, gbc);

        gbc.gridx = 0; gbc.gridy = 7; gbc.weightx = 0.3;
        formPanel.add(new JLabel("Drinks Charges (₹):"), gbc);
        drinkField = new JTextField("0");
        gbc.gridx = 1; gbc.gridy = 7; gbc.weightx = 0.7;
        formPanel.add(drinkField, gbc);

        // Payment dropdown
        gbc.gridx = 0; gbc.gridy = 8; gbc.weightx = 0.3;
        formPanel.add(new JLabel("Payment Method:"), gbc);
        paymentCombo = new JComboBox<>(new String[]{"Cash", "Card", "UPI"});
        gbc.gridx = 1; gbc.gridy = 8; gbc.weightx = 0.7;
        formPanel.add(paymentCombo, gbc);

        // Notes text area
        gbc.gridx = 0; gbc.gridy = 9; gbc.weightx = 0.3; gbc.anchor = GridBagConstraints.NORTH;
        formPanel.add(new JLabel("Notes:"), gbc);
        notesArea = new JTextArea(5, 20);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        gbc.gridx = 1; gbc.gridy = 9; gbc.weightx = 0.7; gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(notesScroll, gbc);

        // Buttons: Generate, Clear, Save
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        JButton generateBtn = new JButton("Generate Bill");
        JButton clearBtn = new JButton("Clear");
        JButton saveBtn = new JButton("Save Bill (TXT)");
        btnPanel.add(generateBtn);
        btnPanel.add(clearBtn);
        btnPanel.add(saveBtn);

        gbc.gridx = 0; gbc.gridy = 10; gbc.gridwidth = 2; gbc.weightx = 1;
        formPanel.add(btnPanel, gbc);

        // Right panel: Bill area
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(new CompoundBorder(new TitledBorder("Bill Preview"), new EmptyBorder(6,6,6,6)));
        rightPanel.setOpaque(false);
        billArea = new JTextArea();
        billArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        billArea.setEditable(false);
        JScrollPane billScroll = new JScrollPane(billArea);
        rightPanel.add(billScroll, BorderLayout.CENTER);

        // Attach parts to split pane
        split.setLeftComponent(formPanel);
        split.setRightComponent(rightPanel);

        root.add(split, BorderLayout.CENTER);

        // Button actions
        generateBtn.addActionListener(e -> generateBill());
        clearBtn.addActionListener(e -> clearAllInputs());
        saveBtn.addActionListener(e -> {
            String text = billArea.getText();
            if (text == null || text.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "No bill to save. Generate bill first.", "Nothing to Save", JOptionPane.WARNING_MESSAGE);
                return;
            }
            saveBillToFile(text);
        });

        return root;
    }

    // ---------- Bill Logic ----------
    private void generateBill() {
        try {
            String name = nameField.getText().trim();
            String contact = contactField.getText().trim();
            String roomKey = (String) roomCombo.getSelectedItem();
            int roomRate = ROOM_RATES.get(roomKey);
            int rooms = (Integer) roomsSpinner.getValue();
            int nights = (Integer) nightsSpinner.getValue();

            double food = parsePositiveDouble(foodField.getText().trim());
            double drinks = parsePositiveDouble(drinkField.getText().trim());

            // Extras
            double breakfastCost = breakfastCB.isSelected() ? 200.0 * rooms * nights : 0.0;
            double wifiCost = wifiCB.isSelected() ? 100.0 * rooms * nights : 0.0;
            double laundryCost = laundryCB.isSelected() ? 150.0 * rooms : 0.0;

            double roomTotal = roomRate * rooms * nights;
            double extrasTotal = breakfastCost + wifiCost + laundryCost;
            double subtotal = roomTotal + extrasTotal + food + drinks;

            double gst = roundTwoDecimals(subtotal * 0.18); // 18% GST
            double total = roundTwoDecimals(subtotal + gst);

            // Format bill
            StringBuilder sb = new StringBuilder();
            sb.append("********** HOTEL BILL **********\n\n");
            sb.append("Invoice : ").append(generateInvoiceId()).append("\n");
            sb.append("Date    : ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n\n");
            sb.append("Customer: ").append(name.isEmpty() ? "N/A" : name).append("\n");
            sb.append("Contact : ").append(contact.isEmpty() ? "N/A" : contact).append("\n\n");

            sb.append(String.format("%-30s %10s\n", "Item", "Amount (₹)"));
            sb.append("------------------------------------------------\n");
            sb.append(String.format("%-30s %10.2f\n", roomKey + " x" + rooms + " x" + nights, (double)roomTotal));
            if (breakfastCost > 0) sb.append(String.format("%-30s %10.2f\n", "Breakfast", breakfastCost));
            if (wifiCost > 0) sb.append(String.format("%-30s %10.2f\n", "WiFi", wifiCost));
            if (laundryCost > 0) sb.append(String.format("%-30s %10.2f\n", "Laundry", laundryCost));
            if (food > 0) sb.append(String.format("%-30s %10.2f\n", "Food Charges", food));
            if (drinks > 0) sb.append(String.format("%-30s %10.2f\n", "Drinks Charges", drinks));
            sb.append("------------------------------------------------\n");
            sb.append(String.format("%-30s %10.2f\n", "Subtotal", subtotal));
            sb.append(String.format("%-30s %10.2f\n", "GST (18%)", gst));
            sb.append("------------------------------------------------\n");
            sb.append(String.format("%-30s %10.2f\n", "Total Payable", total));
            sb.append("\nPayment Method: ").append(paymentCombo.getSelectedItem()).append("\n");
            String notes = notesArea.getText().trim();
            if (!notes.isEmpty()) {
                sb.append("\nNotes:\n");
                sb.append(notes).append("\n");
            }
            sb.append("\nThank you for staying with us!\n");
            sb.append("********************************\n");

            billArea.setText(sb.toString());
            billArea.setCaretPosition(0);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter numeric values for food/drinks.\nExample: 250 or 0", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error generating bill: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveBillToFile(String text) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save bill as...");
        chooser.setSelectedFile(new File("hotel_bill.txt"));
        int res = chooser.showSaveDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            try (FileWriter fw = new FileWriter(f)) {
                fw.write(text);
                JOptionPane.showMessageDialog(this, "Bill saved to: " + f.getAbsolutePath(), "Saved", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Failed to save file: " + ex.getMessage(), "IO Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearAllInputs() {
        nameField.setText("");
        contactField.setText("");
        roomCombo.setSelectedIndex(0);
        roomsSpinner.setValue(1);
        nightsSpinner.setValue(1);
        breakfastCB.setSelected(false);
        wifiCB.setSelected(false);
        laundryCB.setSelected(false);
        foodField.setText("0");
        drinkField.setText("0");
        paymentCombo.setSelectedIndex(0);
        notesArea.setText("");
        billArea.setText("");
    }

    private String generateInvoiceId() {
        return "INV" + System.currentTimeMillis();
    }

    private double parsePositiveDouble(String s) throws NumberFormatException {
        if (s == null || s.isEmpty()) return 0.0;
        double v = Double.parseDouble(s.replaceAll("[^0-9.]", ""));
        if (v < 0) throw new NumberFormatException("Negative value not allowed");
        return roundTwoDecimals(v);
    }

    private double roundTwoDecimals(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    // ---------- Custom gradient panel ----------
    private static class GradientPanel extends JPanel {
        private final Color c1;
        private final Color c2;
        public GradientPanel(Color c1, Color c2) {
            this.c1 = c1; this.c2 = c2;
            setOpaque(true);
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            int w = getWidth(), h = getHeight();
            GradientPaint gp = new GradientPaint(0, 0, c1, w, h, c2);
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, h);
            g2.dispose();
        }
    }

    // ---------- Main ----------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Optional: set system look and feel for native appearance
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            HotelBillingSystem app = new HotelBillingSystem();
            app.setVisible(true);
        });
    }
}
