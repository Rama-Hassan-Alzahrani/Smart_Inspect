package smart_inspect;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.Scanner;
import javax.swing.*;

// -----------------------------------------------------------
// HistoryFrame
// shows all past inspection records for a specific establishment
// sends the HISTORY command to the server when it opens and
//   displays the results in a scrollable text area
// -----------------------------------------------------------
public class HistoryFrame extends JFrame implements ActionListener {

    // open connection passed in from SearchFrame
    private Scanner serverIn;
    private PrintWriter serverOut;

    // the license number of the establishment whose history we are viewing
    private int licenseNumber;

    // reference to SearchFrame — shown again when this window closes
    private SearchFrame searchFrame;

    // displays the inspection history entries
    private JTextArea historyArea;

    // button to go back
    private JButton backButton;

    // -----------------------------------------------------------
    // constructor
    // socket        : the open socket from LoginFrame
    // serverIn      : the Scanner reading from the server
    // serverOut     : the PrintWriter writing to the server
    // licenseNumber : license number of the establishment to view history for
    // searchFrame   : reference to SearchFrame — shown again when this closes
    // -----------------------------------------------------------
    public HistoryFrame(Socket socket, Scanner serverIn, PrintWriter serverOut,
                        int licenseNumber, SearchFrame searchFrame) {

        this.serverIn      = serverIn;
        this.serverOut     = serverOut;
        this.licenseNumber = licenseNumber;
        this.searchFrame   = searchFrame;

        // window setup
        setTitle("Smart Inspect — History for License " + licenseNumber);
        setSize(580, 440);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        setLayout(new BorderLayout(5, 5));

        // colored header panel — same style as all other frames
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(30, 90, 160));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        JLabel headerLabel = new JLabel(
                "Inspection History — License Number: " + licenseNumber);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 15));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);
        add(headerPanel, BorderLayout.NORTH);

        // text area to display history entries — monospaced for aligned output
        historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        historyArea.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
        JScrollPane scrollPane = new JScrollPane(historyArea);
        add(scrollPane, BorderLayout.CENTER);

        // bottom panel with the back button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        backButton = new JButton("Back to Search");
        backButton.setFont(new Font("Arial", Font.PLAIN, 13));
        backButton.setPreferredSize(new Dimension(150, 28));
        backButton.addActionListener(this);
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // handle window close button same as pressing back
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                goBack();
            }
        });

        // load and display history as soon as the frame opens
        loadHistory();
    }

    // -----------------------------------------------------------
    // loadHistory
    // sends the HISTORY command to the server and parses the response
    // each entry separated by ~ is shown on its own line in the text area
    // returns : nothing
    // -----------------------------------------------------------
    public void loadHistory() {

        serverOut.println("HISTORY:" + licenseNumber);

        String response = serverIn.nextLine();
        System.out.println("Server: " + response);

        if (!response.startsWith("HISTORY:")) {
            historyArea.setText("Unexpected response from server.");
            return;
        }

        // remove the "HISTORY:" prefix
        String data = response.substring("HISTORY:".length());

        // if the server returned the no-history message just display it
        if (!data.contains("~")) {
            historyArea.setText(data);
            return;
        }

        // data format: "entry1~entry2~entry3~"
        // split on ~ and print each entry on its own line with a separator
        String[] entries = data.split("~");

        StringBuilder display = new StringBuilder();

        for (int i = 0; i < entries.length; i++) {
            if (!entries[i].isEmpty()) {
                display.append("--- Record ").append(i + 1).append(" ---\n");
                display.append(entries[i]).append("\n\n");
            }
        }

        if (display.length() == 0) {
            historyArea.setText("No history found for this establishment.");
        } else {
            historyArea.setText(display.toString());
        }
    }

    // -----------------------------------------------------------
    // actionPerformed
    // routes button clicks to the correct handler method
    // e : the event that was fired
    // returns : nothing
    // -----------------------------------------------------------
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == backButton) {
            goBack();
        }
    }

    // -----------------------------------------------------------
    // goBack
    // closes this frame and shows SearchFrame again
    // returns : nothing
    // -----------------------------------------------------------
    public void goBack() {
        searchFrame.setVisible(true);
        this.dispose();
    }
}
