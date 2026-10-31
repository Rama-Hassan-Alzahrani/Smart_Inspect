package smart_inspect;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.Scanner;
import javax.swing.*;

// -----------------------------------------------------------
// SearchFrame
// lets the inspector type a license number and search for an establishment
// shows the establishment details when found
// offers buttons to write an inspection or view history for the found establishment
// -----------------------------------------------------------
public class SearchFrame extends JFrame implements ActionListener {

    // open connection passed in from MainMenuFrame
    private Socket socket;
    private Scanner serverIn;
    private PrintWriter serverOut;

    // inspector name — passed along to InspectionFormFrame
    private String inspectorName;

    // reference to MainMenuFrame so we can show it again when this frame closes
    private MainMenuFrame mainMenu;

    // the license number of the establishment that was found
    // used when opening InspectionFormFrame or HistoryFrame
    private int foundLicenseNumber;

    // input and buttons
    private JTextField licenseField;
    private JButton searchButton;
    private JButton inspectButton;
    private JButton historyButton;
    private JButton backButton;

    // shows search results or status messages
    private JTextArea resultsArea;

    // -----------------------------------------------------------
    // constructor
    // socket       : the open socket from LoginFrame
    // serverIn     : the Scanner reading from the server
    // serverOut    : the PrintWriter writing to the server
    // inspectorName: name of the logged-in inspector
    // mainMenu     : reference to MainMenuFrame — shown again when this frame closes
    // -----------------------------------------------------------
    public SearchFrame(Socket socket, Scanner serverIn, PrintWriter serverOut,
                       String inspectorName, MainMenuFrame mainMenu) {

        this.socket        = socket;
        this.serverIn      = serverIn;
        this.serverOut     = serverOut;
        this.inspectorName = inspectorName;
        this.mainMenu      = mainMenu;
        this.foundLicenseNumber = -1;   // -1 means no establishment found yet

        // window setup
        setTitle("Smart Inspect — Search Establishment");
        setSize(520, 460);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout(5, 5));

        // colored header — wraps together with the search bar in one NORTH panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(30, 90, 160));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        JLabel headerLabel = new JLabel("Search Establishment by License Number");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 15));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);

        // search bar with input field, hint label, and search button
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 12, 0, 12));
        JLabel licenseLabel = new JLabel("License Number:");
        licenseLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        searchPanel.add(licenseLabel);
        licenseField = new JTextField(8);
        licenseField.setFont(new Font("Arial", Font.PLAIN, 13));
        searchPanel.add(licenseField);
        JLabel licenseHint = new JLabel("(0 – 15)");
        licenseHint.setFont(new Font("Arial", Font.ITALIC, 11));
        licenseHint.setForeground(Color.GRAY);
        searchPanel.add(licenseHint);
        searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.PLAIN, 13));
        searchButton.setPreferredSize(new Dimension(90, 28));
        searchButton.addActionListener(this);
        searchPanel.add(searchButton);

        // wrapper puts header and search bar together in NORTH
        JPanel northWrapper = new JPanel(new BorderLayout());
        northWrapper.add(headerPanel, BorderLayout.NORTH);
        northWrapper.add(searchPanel, BorderLayout.SOUTH);
        add(northWrapper, BorderLayout.NORTH);

        // results area — scrollable, monospaced font for aligned output
        resultsArea = new JTextArea(10, 40);
        resultsArea.setEditable(false);
        resultsArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        resultsArea.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
        JScrollPane scrollPane = new JScrollPane(resultsArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Establishment Details"));
        add(scrollPane, BorderLayout.CENTER);

        // bottom action buttons — disabled until a valid search result is shown
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        inspectButton = new JButton("Write Inspection");
        inspectButton.setFont(new Font("Arial", Font.PLAIN, 13));
        inspectButton.setPreferredSize(new Dimension(150, 28));
        inspectButton.addActionListener(this);
        inspectButton.setEnabled(false);
        actionPanel.add(inspectButton);

        historyButton = new JButton("View History");
        historyButton.setFont(new Font("Arial", Font.PLAIN, 13));
        historyButton.setPreferredSize(new Dimension(120, 28));
        historyButton.addActionListener(this);
        historyButton.setEnabled(false);
        actionPanel.add(historyButton);

        backButton = new JButton("Back to Main Menu");
        backButton.setFont(new Font("Arial", Font.PLAIN, 13));
        backButton.setPreferredSize(new Dimension(160, 28));
        backButton.addActionListener(this);
        actionPanel.add(backButton);

        add(actionPanel, BorderLayout.SOUTH);

        // handle the window close button the same as pressing back
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                goBack();
            }
        });
    }

    // -----------------------------------------------------------
    // actionPerformed
    // routes button clicks to the correct handler method
    // e : the event that was fired
    // returns : nothing
    // -----------------------------------------------------------
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == searchButton) {
            doSearch();
        } else if (e.getSource() == inspectButton) {
            openInspectionForm();
        } else if (e.getSource() == historyButton) {
            openHistoryFrame();
        } else if (e.getSource() == backButton) {
            goBack();
        }
    }

    // -----------------------------------------------------------
    // doSearch
    // sends the SEARCH command to the server and displays the result
    // enables or disables the action buttons based on whether something was found
    // returns : nothing
    // -----------------------------------------------------------
    public void doSearch() {

        String licenseStr = licenseField.getText().trim();

        if (licenseStr.isEmpty()) {
            resultsArea.setText("Please enter a license number.");
            return;
        }

        // validate input is a number and within the valid range
        try {
            int licenseNum = Integer.parseInt(licenseStr);

            if (licenseNum < 0 || licenseNum > 15) {
                resultsArea.setText("License number must be between 0 and 15.");
                inspectButton.setEnabled(false);
                historyButton.setEnabled(false);
                return;
            }

        } catch (NumberFormatException e) {
            resultsArea.setText("License number must be a number.");
            return;
        }

        // send the search command to the server
        serverOut.println("SEARCH:" + licenseStr);

        // read the server's response
        String response = serverIn.nextLine();
        System.out.println("Server: " + response);

        if (response.equals("NOT_FOUND")) {
            resultsArea.setText("No establishment found with license number: " + licenseStr);
            foundLicenseNumber = -1;
            inspectButton.setEnabled(false);
            historyButton.setEnabled(false);
            return;
        }

        if (response.startsWith("RESULT:")) {

            // parse the pipe-separated fields
            String data = response.substring("RESULT:".length());
            String[] fields = data.split("\\|");

            // build a readable display string
            StringBuilder display = new StringBuilder();
            for (String field : fields) {
                // replace = with ": " for a nicer display
                display.append(field.replace("=", ": ")).append("\n");
            }

            resultsArea.setText(display.toString());

            // save the license number so InspectionFormFrame and HistoryFrame can use it
            foundLicenseNumber = Integer.parseInt(licenseStr);

            // enable the action buttons now that we have a valid establishment
            inspectButton.setEnabled(true);
            historyButton.setEnabled(true);

        } else {
            // unexpected response
            resultsArea.setText("Unexpected response from server: " + response);
            inspectButton.setEnabled(false);
            historyButton.setEnabled(false);
        }
    }

    // -----------------------------------------------------------
    // openInspectionForm
    // opens InspectionFormFrame for the currently found establishment
    // returns : nothing
    // -----------------------------------------------------------
    public void openInspectionForm() {
        InspectionFormFrame form = new InspectionFormFrame(
                socket, serverIn, serverOut, inspectorName, foundLicenseNumber, this);
        form.setVisible(true);

        // hide this frame while the inspector fills in the form
        this.setVisible(false);
    }

    // -----------------------------------------------------------
    // openHistoryFrame
    // opens HistoryFrame showing past inspections for the found establishment
    // returns : nothing
    // -----------------------------------------------------------
    public void openHistoryFrame() {
        HistoryFrame history = new HistoryFrame(
                socket, serverIn, serverOut, foundLicenseNumber, this);
        history.setVisible(true);

        // hide this frame while history is being viewed
        this.setVisible(false);
    }

    // -----------------------------------------------------------
    // goBack
    // closes this window and shows the main menu again
    // returns : nothing
    // -----------------------------------------------------------
    public void goBack() {
        mainMenu.setVisible(true);
        this.dispose();
    }
}
