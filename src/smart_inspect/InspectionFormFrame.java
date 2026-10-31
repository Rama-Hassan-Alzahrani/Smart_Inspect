package smart_inspect;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.Scanner;
import javax.swing.*;

// -----------------------------------------------------------
// InspectionFormFrame
// lets the inspector fill in and submit an inspection result
// the violation list is loaded from the database via the server
//   and shown in a dropdown — the inspector picks one or picks "No Violation"
// on submit the form sends the INSPECT command to the server
// -----------------------------------------------------------
public class InspectionFormFrame extends JFrame implements ActionListener {

    // open connection passed in from SearchFrame
    private Scanner serverIn;
    private PrintWriter serverOut;

    // the inspector's name — displayed on the form and included in the saved record
    private String inspectorName;

    // the license number of the establishment being inspected
    private int licenseNumber;

    // reference to SearchFrame so we can go back to it after submitting
    private SearchFrame searchFrame;

    // form input components
    private JTextField dateField;
    private JTextArea notesArea;
    private JComboBox<String> violationCombo;

    // buttons
    private JButton submitButton;
    private JButton cancelButton;

    // shows status messages after submission
    private JLabel statusLabel;

    // -----------------------------------------------------------
    // constructor
    // socket        : the open socket from LoginFrame
    // serverIn      : the Scanner reading from the server
    // serverOut     : the PrintWriter writing to the server
    // inspectorName : name of the logged-in inspector
    // licenseNumber : license number of the establishment being inspected
    // searchFrame   : reference to SearchFrame — shown again when this closes
    // -----------------------------------------------------------
    public InspectionFormFrame(Socket socket, Scanner serverIn, PrintWriter serverOut,
                               String inspectorName, int licenseNumber, SearchFrame searchFrame) {

        this.serverIn      = serverIn;
        this.serverOut     = serverOut;
        this.inspectorName = inspectorName;
        this.licenseNumber = licenseNumber;
        this.searchFrame   = searchFrame;

        // window setup
        setTitle("Smart Inspect — Write Inspection (License: " + licenseNumber + ")");
        setSize(500, 460);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        // colored header panel — same style as all other frames
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(30, 90, 160));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        JLabel headerLabel = new JLabel("Write Inspection Result — License: " + licenseNumber);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 15));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);
        add(headerPanel, BorderLayout.NORTH);

        // form panel — 8 rows to accommodate fields and hint labels
        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 10, 25));

        JLabel estLabel = new JLabel("Establishment License:");
        estLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        formPanel.add(estLabel);
        JLabel estValue = new JLabel(String.valueOf(licenseNumber));
        estValue.setFont(new Font("Arial", Font.PLAIN, 13));
        formPanel.add(estValue);

        JLabel inspLabel = new JLabel("Inspector:");
        inspLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        formPanel.add(inspLabel);
        JLabel inspValue = new JLabel(this.inspectorName);
        inspValue.setFont(new Font("Arial", Font.PLAIN, 13));
        formPanel.add(inspValue);

        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        formPanel.add(dateLabel);
        dateField = new JTextField();
        dateField.setFont(new Font("Arial", Font.PLAIN, 13));
        formPanel.add(dateField);

        formPanel.add(new JLabel(""));
        JLabel dateHint = new JLabel("Format: YYYY-MM-DD (e.g. 2026-04-30)");
        dateHint.setFont(new Font("Arial", Font.ITALIC, 11));
        dateHint.setForeground(Color.GRAY);
        formPanel.add(dateHint);

        JLabel notesLabel = new JLabel("Notes:");
        notesLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        formPanel.add(notesLabel);
        notesArea = new JTextArea(3, 20);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setFont(new Font("Arial", Font.PLAIN, 13));
        JScrollPane notesScroll = new JScrollPane(notesArea);
        formPanel.add(notesScroll);

        formPanel.add(new JLabel(""));
        JLabel notesHint = new JLabel("At least 10 characters — no | or ~ characters");
        notesHint.setFont(new Font("Arial", Font.ITALIC, 11));
        notesHint.setForeground(Color.GRAY);
        formPanel.add(notesHint);

        JLabel violLabel = new JLabel("Violation:");
        violLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        formPanel.add(violLabel);
        violationCombo = new JComboBox<String>();
        violationCombo.setFont(new Font("Arial", Font.PLAIN, 13));
        violationCombo.addItem("No Violation");     // first option — no violation found
        formPanel.add(violationCombo);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(Color.RED);
        formPanel.add(statusLabel);
        formPanel.add(new JLabel(""));

        add(formPanel, BorderLayout.CENTER);

        // button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        submitButton = new JButton("Submit Inspection");
        submitButton.setFont(new Font("Arial", Font.PLAIN, 13));
        submitButton.setPreferredSize(new Dimension(160, 28));
        submitButton.addActionListener(this);
        buttonPanel.add(submitButton);

        cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 13));
        cancelButton.setPreferredSize(new Dimension(100, 28));
        cancelButton.addActionListener(this);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // handle window close button same as cancel
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                doCancel();
            }
        });

        // load the violations list from the server as soon as the frame opens
        loadViolations();
    }

    // -----------------------------------------------------------
    // loadViolations
    // sends the VIOLATIONS command to the server and populates the dropdown
    // each entry in the dropdown shows the violation name and penalty
    // returns : nothing
    // -----------------------------------------------------------
    public void loadViolations() {

        serverOut.println("VIOLATIONS");
        String response = serverIn.nextLine();
        System.out.println("Server: " + response);

        if (!response.startsWith("VIOLATIONS:")) {
            // could not load violations — the combo box will only show "No Violation"
            return;
        }

        // remove the "VIOLATIONS:" prefix
        String data = response.substring("VIOLATIONS:".length());

        if (data.isEmpty()) {
            // no violations in the database — just keep "No Violation"
            return;
        }

        // data format: "id1,name1,price1~id2,name2,price2~"
        String[] entries = data.split("~");

        for (String entry : entries) {
            if (entry.isEmpty()) {
                continue;
            }

            String[] parts = entry.split(",");

            if (parts.length >= 3) {
                // show: "ViolationName (Penalty: 500 SAR)"
                String displayText = parts[1] + " (Penalty: " + parts[2] + " SAR)";
                violationCombo.addItem(displayText);
            }
        }
    }

    // -----------------------------------------------------------
    // actionPerformed
    // routes button clicks to the correct handler method
    // e : the event that was fired
    // returns : nothing
    // -----------------------------------------------------------
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == submitButton) {
            doSubmit();
        } else if (e.getSource() == cancelButton) {
            doCancel();
        }
    }

    // -----------------------------------------------------------
    // doSubmit
    // validates the form, builds the INSPECT command, and sends it to the server
    // shows a success or error message and goes back to SearchFrame on success
    // returns : nothing
    // -----------------------------------------------------------
    public void doSubmit() {

        String date  = dateField.getText().trim();
        String notes = notesArea.getText().trim();

        // validate that the required fields are filled in
        if (date.isEmpty()) {
            statusLabel.setText("Please enter the inspection date.");
            return;
        }

        if (notes.isEmpty()) {
            statusLabel.setText("Please enter inspection notes.");
            return;
        }

        // notes must be at least 10 characters so they are meaningful
        if (notes.length() < 10) {
            statusLabel.setText("Notes must be at least 10 characters.");
            return;
        }

        // | and ~ are used as separators in the server protocol — they would corrupt the data
        if (notes.contains("|") || notes.contains("~")) {
            statusLabel.setText("Notes cannot contain the characters | or ~");
            return;
        }

        // validate date format YYYY-MM-DD — check length, dashes, and numeric parts
        if (date.length() != 10 || date.charAt(4) != '-' || date.charAt(7) != '-') {
            statusLabel.setText("Date must be in YYYY-MM-DD format.");
            return;
        }

        // extract the year, month, and day parts and verify they are all digits
        String yearStr  = date.substring(0, 4);
        String monthStr = date.substring(5, 7);
        String dayStr   = date.substring(8, 10);

        try {
            int year  = Integer.parseInt(yearStr);
            int month = Integer.parseInt(monthStr);
            int day   = Integer.parseInt(dayStr);

            if (year < 2000 || year > 2100) {
                statusLabel.setText("Year must be between 2000 and 2100.");
                return;
            }

            if (month < 1 || month > 12) {
                statusLabel.setText("Month must be between 01 and 12.");
                return;
            }

            if (day < 1 || day > 31) {
                statusLabel.setText("Day must be between 01 and 31.");
                return;
            }

        } catch (NumberFormatException e) {
            statusLabel.setText("Date must contain numbers only (YYYY-MM-DD).");
            return;
        }

        // get the selected violation name
        // if "No Violation" is selected, send an empty string
        String selectedItem = (String) violationCombo.getSelectedItem();
        String violationName = "";

        if (!selectedItem.equals("No Violation")) {
            // the display text is "Name (Penalty: X SAR)" — extract just the name
            int parenIndex = selectedItem.indexOf(" (Penalty:");
            if (parenIndex > 0) {
                violationName = selectedItem.substring(0, parenIndex);
            } else {
                violationName = selectedItem;
            }
        }

        // build the INSPECT command — fields separated by |
        String command = "INSPECT:" + licenseNumber
                       + "|" + date
                       + "|" + notes
                       + "|" + violationName;

        serverOut.println(command);

        // read the server's response
        String response = serverIn.nextLine();
        System.out.println("Server: " + response);

        if (response.equals("SAVED")) {
            JOptionPane.showMessageDialog(this,
                    "Inspection saved successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            // go back to search frame after a successful save
            doCancel();

        } else {
            // ERROR:reason
            if (response.contains(":")) {
                statusLabel.setText("Error: " + response.split(":", 2)[1]);
            } else {
                statusLabel.setText("Server error. Please try again.");
            }
        }
    }

    // -----------------------------------------------------------
    // doCancel
    // closes this frame and shows SearchFrame again
    // returns : nothing
    // -----------------------------------------------------------
    public void doCancel() {
        searchFrame.setVisible(true);
        this.dispose();
    }
}
