package smart_inspect;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.Scanner;
import javax.swing.*;

// -----------------------------------------------------------
// MainMenuFrame
// the main navigation screen shown after a successful login
// displays the inspector's name and two action buttons:
//   search by license number, and disconnect / exit
// passes the open server connection along to SearchFrame when needed
// -----------------------------------------------------------
public class MainMenuFrame extends JFrame implements ActionListener {

    // the open connection to InspectServer — kept alive for the whole session
    private Socket socket;
    private Scanner serverIn;
    private PrintWriter serverOut;

    // the name of the inspector who is logged in — shown in the header
    private String inspectorName;

    // navigation buttons
    private JButton searchButton;
    private JButton statsButton;
    private JButton weatherButton;
    private JButton exitButton;

    // -----------------------------------------------------------
    // constructor
    // socket       : the open socket from LoginFrame
    // serverIn     : the Scanner reading from the server
    // serverOut    : the PrintWriter writing to the server
    // inspectorName: name of the inspector who logged in
    // -----------------------------------------------------------
    public MainMenuFrame(Socket socket, Scanner serverIn, PrintWriter serverOut, String inspectorName) {

        this.socket        = socket;
        this.serverIn      = serverIn;
        this.serverOut     = serverOut;
        this.inspectorName = inspectorName;

        // window setup
        setTitle("Smart Inspect — Main Menu");
        setSize(440, 370);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        // colored header panel — same style used in every frame
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(30, 90, 160));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        JLabel headerLabel = new JLabel("Smart Inspect — Main Menu");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 15));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);
        add(headerPanel, BorderLayout.NORTH);

        // centre panel — welcome message and navigation buttons
        JPanel centerPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel welcomeLabel = new JLabel("Welcome, " + inspectorName + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        centerPanel.add(welcomeLabel);

        JLabel instructionLabel = new JLabel("What would you like to do?", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        centerPanel.add(instructionLabel);

        searchButton = new JButton("Search Establishment by License Number");
        searchButton.setFont(new Font("Arial", Font.PLAIN, 13));
        searchButton.setPreferredSize(new Dimension(300, 28));
        searchButton.addActionListener(this);
        centerPanel.add(searchButton);

        statsButton = new JButton("View Inspection Statistics");
        statsButton.setFont(new Font("Arial", Font.PLAIN, 13));
        statsButton.setPreferredSize(new Dimension(300, 28));
        statsButton.addActionListener(this);
        centerPanel.add(statsButton);

        weatherButton = new JButton("Check Weather for Inspection");
        weatherButton.setFont(new Font("Arial", Font.PLAIN, 13));
        weatherButton.setPreferredSize(new Dimension(300, 28));
        weatherButton.addActionListener(this);
        centerPanel.add(weatherButton);

        exitButton = new JButton("Disconnect and Exit");
        exitButton.setFont(new Font("Arial", Font.PLAIN, 13));
        exitButton.setPreferredSize(new Dimension(300, 28));
        exitButton.addActionListener(this);
        centerPanel.add(exitButton);

        add(centerPanel, BorderLayout.CENTER);
    }

    // -----------------------------------------------------------
    // actionPerformed
    // routes button clicks to the correct handler method
    // e : the event that was fired
    // returns : nothing
    // -----------------------------------------------------------
    public void actionPerformed(ActionEvent e) {

    if (e.getSource() == searchButton) {
        openSearchFrame();
    } else if (e.getSource() == statsButton) {
        openStatsFrame();
    } else if (e.getSource() == weatherButton) {  
        checkWeatherAction();                      
    } else if (e.getSource() == exitButton) {
        doExit();
    }
    }

    // -----------------------------------------------------------
    // openSearchFrame
    // opens the SearchFrame and keeps this window open so the inspector
    //   can come back to the main menu after finishing a search
    // returns : nothing
    // -----------------------------------------------------------
    public void openSearchFrame() {
        SearchFrame search = new SearchFrame(socket, serverIn, serverOut, inspectorName, this);
        search.setVisible(true);

        // hide this window while searching — SearchFrame will show it again on close
        this.setVisible(false);
    }

    // -----------------------------------------------------------
    // openStatsFrame
    // opens StatsFrame which fetches and displays inspection statistics
    // hides this window while stats are being viewed
    // returns : nothing
    // -----------------------------------------------------------
    public void openStatsFrame() {
        StatsFrame stats = new StatsFrame(socket, serverIn, serverOut, this);
        stats.setVisible(true);

        // hide this window while stats are being viewed — StatsFrame shows it again on close
        this.setVisible(false);
    }

    // -----------------------------------------------------------
    // doExit
    // sends QUIT to the server, closes the socket, and exits the program
    // returns : nothing
    // -----------------------------------------------------------
    public void doExit() {

        try {
            // let the server know we are disconnecting cleanly
            serverOut.println("QUIT");

            // read the BYE acknowledgement
            if (serverIn.hasNextLine()) {
                String bye = serverIn.nextLine();
                System.out.println("Server: " + bye);
            }

            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.exit(0);
    }
    // -----------------------------------------------------------
    // checkWeatherAction
    // opens a dialog to check weather for a city
    // uses Visual Crossing Weather API (Lecture 3 & 4 concepts)
    // demonstrates URL/URLConnection/HTTP GET
    // returns : nothing
    // -----------------------------------------------------------
    private void checkWeatherAction() {
        String location = JOptionPane.showInputDialog(
            this,
            "Enter city name to check weather:\n(Examples: Jeddah, Riyadh, Mecca, Dammam, Medina)",
            "Weather Check for Inspection",
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (location != null && !location.trim().isEmpty()) {
            // Create loading dialog
            JDialog loadingDialog = new JDialog(this, "Fetching Weather...", true);
            JLabel loadingLabel = new JLabel("  Connecting to weather service...  ", JLabel.CENTER);
            loadingLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            loadingDialog.add(loadingLabel);
            loadingDialog.setSize(300, 100);
            loadingDialog.setLocationRelativeTo(this);
            
            // Fetch weather in background thread (prevents UI freezing)
            Thread weatherThread = new Thread(() -> {
                String weatherReport = WeatherHelper.checkWeather(location.trim());
                
                // Update UI on event dispatch thread
                SwingUtilities.invokeLater(() -> {
                    loadingDialog.dispose();
                    
                    // Display result in scrollable text area
                    JTextArea textArea = new JTextArea(weatherReport);
                    textArea.setEditable(false);
                    textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                    textArea.setLineWrap(false);
                    
                    JScrollPane scrollPane = new JScrollPane(textArea);
                    scrollPane.setPreferredSize(new java.awt.Dimension(520, 420));
                    
                    JOptionPane.showMessageDialog(
                        this,
                        scrollPane,
                        "Weather Report - " + location,
                        JOptionPane.INFORMATION_MESSAGE
                    );
                });
            });
            
            weatherThread.start();
            loadingDialog.setVisible(true);
        }
    }
}  
