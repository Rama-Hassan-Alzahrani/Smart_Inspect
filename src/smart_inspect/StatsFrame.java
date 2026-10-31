package smart_inspect;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.Scanner;
import javax.swing.*;

// -----------------------------------------------------------
// StatsFrame
// shows three inspection statistics fetched live from the server:
//   total number of inspections ever recorded
//   most common violation across all inspections
//   number of inspections recorded in the current calendar month
// sends the STATS command to the server on open and displays the result
// -----------------------------------------------------------
public class StatsFrame extends JFrame implements ActionListener {

    // open connection passed in from MainMenuFrame
    private Scanner serverIn;
    private PrintWriter serverOut;

    // reference to MainMenuFrame — shown again when this window closes
    private MainMenuFrame mainMenu;

    // labels that display the three stat values — updated after STATS response
    private JLabel totalLabel;
    private JLabel mostCommonLabel;
    private JLabel thisMonthLabel;

    // close button
    private JButton closeButton;

    // -----------------------------------------------------------
    // constructor
    // socket      : the open socket from LoginFrame (kept for architecture consistency)
    // serverIn    : the Scanner reading from the server
    // serverOut   : the PrintWriter writing to the server
    // mainMenu    : reference to MainMenuFrame — shown again when this closes
    // -----------------------------------------------------------
    public StatsFrame(Socket socket, Scanner serverIn, PrintWriter serverOut,
                      MainMenuFrame mainMenu) {

        this.serverIn  = serverIn;
        this.serverOut = serverOut;
        this.mainMenu  = mainMenu;

        // window setup
        setTitle("Smart Inspect — Inspection Statistics");
        setSize(460, 320);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        // colored header panel — same style as all other frames
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(30, 90, 160));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        JLabel headerLabel = new JLabel("Inspection Statistics");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 15));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);
        add(headerPanel, BorderLayout.NORTH);

        // stats panel — three rows, each showing a label and its value
        JPanel statsPanel = new JPanel(new GridLayout(3, 2, 15, 20));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 20, 40));

        JLabel totalTitle = new JLabel("Total Inspections:");
        totalTitle.setFont(new Font("Arial", Font.BOLD, 13));
        statsPanel.add(totalTitle);

        totalLabel = new JLabel("Loading...");
        totalLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        statsPanel.add(totalLabel);

        JLabel mostCommonTitle = new JLabel("Most Common Violation:");
        mostCommonTitle.setFont(new Font("Arial", Font.BOLD, 13));
        statsPanel.add(mostCommonTitle);

        mostCommonLabel = new JLabel("Loading...");
        mostCommonLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        statsPanel.add(mostCommonLabel);

        JLabel thisMonthTitle = new JLabel("Inspections This Month:");
        thisMonthTitle.setFont(new Font("Arial", Font.BOLD, 13));
        statsPanel.add(thisMonthTitle);

        thisMonthLabel = new JLabel("Loading...");
        thisMonthLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        statsPanel.add(thisMonthLabel);

        add(statsPanel, BorderLayout.CENTER);

        // bottom panel with close button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        closeButton = new JButton("Close");
        closeButton.setFont(new Font("Arial", Font.PLAIN, 13));
        closeButton.setPreferredSize(new Dimension(120, 28));
        closeButton.addActionListener(this);
        bottomPanel.add(closeButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // handle window close button the same as pressing Close
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                doClose();
            }
        });

        // fetch and display the stats as soon as the frame opens
        loadStats();
    }

    // -----------------------------------------------------------
    // loadStats
    // sends the STATS command to the server and parses the response
    // response format : "STATS:total=X~mostCommon=Y~thisMonth=Z"
    // updates the three display labels with the parsed values
    // returns : nothing
    // -----------------------------------------------------------
    public void loadStats() {

        serverOut.println("STATS");
        String response = serverIn.nextLine();
        System.out.println("Server: " + response);

        if (!response.startsWith("STATS:")) {
            totalLabel.setText("Error loading stats.");
            mostCommonLabel.setText("—");
            thisMonthLabel.setText("—");
            return;
        }

        // remove the "STATS:" prefix
        String data = response.substring("STATS:".length());

        // data format: "total=5~mostCommon=FireHazard~thisMonth=2"
        String[] parts = data.split("~");

        for (int i = 0; i < parts.length; i++) {

            String part = parts[i];

            // split each part on = to get the key and value
            int eqIndex = part.indexOf("=");
            if (eqIndex < 0) {
                continue;
            }

            String key   = part.substring(0, eqIndex);
            String value = part.substring(eqIndex + 1);

            if (key.equals("total")) {
                totalLabel.setText(value);

            } else if (key.equals("mostCommon")) {
                mostCommonLabel.setText(value);

            } else if (key.equals("thisMonth")) {
                thisMonthLabel.setText(value);
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
        if (e.getSource() == closeButton) {
            doClose();
        }
    }

    // -----------------------------------------------------------
    // doClose
    // closes this frame and shows MainMenuFrame again
    // returns : nothing
    // -----------------------------------------------------------
    public void doClose() {
        mainMenu.setVisible(true);
        this.dispose();
    }
}
