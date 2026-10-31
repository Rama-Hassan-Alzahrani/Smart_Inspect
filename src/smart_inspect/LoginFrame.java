package smart_inspect;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import javax.swing.*;

// -----------------------------------------------------------
// LoginFrame
// the first window the inspector sees when the application starts
// connects to the InspectServer on startup, then lets the inspector
//   enter their name and ID to log in
// on success it opens MainMenuFrame and closes itself
// -----------------------------------------------------------
public class LoginFrame extends JFrame implements ActionListener {

    // input fields
    private JTextField nameField;
    private JTextField idField;
    private JPasswordField passwordField;

    // button
    private JButton loginButton;

    // shows error or status messages below the button
    private JLabel statusLabel;

    // the open connection to InspectServer — passed along to all future frames
    private Socket socket;
    private Scanner serverIn;
    private PrintWriter serverOut;

    // the inspector's name is saved here after login succeeds
    private String inspectorName;

    // -----------------------------------------------------------
    // constructor
    // builds the login window and connects to the server immediately
    // -----------------------------------------------------------
    public LoginFrame() {

        // window setup
        setTitle("Smart Inspect — Login");
        setSize(440, 460);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        // colored header panel at the top of every frame — dark blue, white text
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(30, 90, 160));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        JLabel headerLabel = new JLabel("Smart Inspect — Municipal Inspection System");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 15));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);
        add(headerPanel, BorderLayout.NORTH);

        // form panel — 8 rows: each input has a hint label on the row below it
        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));

        formPanel.add(new JLabel("Inspector Name:"));
        nameField = new JTextField();
        nameField.setFont(new Font("Arial", Font.PLAIN, 13));
        formPanel.add(nameField);

        formPanel.add(new JLabel(""));       // empty — hint goes in right column
        JLabel nameHint = new JLabel("Letters only — no numbers or symbols");
        nameHint.setFont(new Font("Arial", Font.ITALIC, 11));
        nameHint.setForeground(Color.GRAY);
        formPanel.add(nameHint);

        formPanel.add(new JLabel("Inspector ID:"));
        idField = new JTextField();
        idField.setFont(new Font("Arial", Font.PLAIN, 13));
        formPanel.add(idField);

        formPanel.add(new JLabel(""));
        JLabel idHint = new JLabel("Numbers only");
        idHint.setFont(new Font("Arial", Font.ITALIC, 11));
        idHint.setForeground(Color.GRAY);
        formPanel.add(idHint);

        formPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 13));
        passwordField.setEchoChar('*');
        formPanel.add(passwordField);

        formPanel.add(new JLabel(""));
        JLabel passwordHint = new JLabel("Your assigned password");
        passwordHint.setFont(new Font("Arial", Font.ITALIC, 11));
        passwordHint.setForeground(Color.GRAY);
        formPanel.add(passwordHint);

        formPanel.add(new JLabel(""));
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.PLAIN, 13));
        loginButton.setPreferredSize(new Dimension(160, 28));
        loginButton.addActionListener(this);
        formPanel.add(loginButton);

        formPanel.add(new JLabel(""));
        statusLabel = new JLabel("Connecting to server...");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(Color.BLUE);
        formPanel.add(statusLabel);

        add(formPanel, BorderLayout.CENTER);

        // connect to the server before showing the window
        connectToServer();
    }

    // -----------------------------------------------------------
    // connectToServer
    // opens a socket to InspectServer and reads the welcome message
    // called once when the frame is constructed
    // returns : nothing
    // -----------------------------------------------------------
    public void connectToServer() {

        try {
            // connect to the server running on the same machine
            socket    = new Socket("127.0.0.1", InspectServer.PORT);
            serverIn  = new Scanner(socket.getInputStream(), StandardCharsets.UTF_8);
            serverOut = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);

            // read the welcome message the server sends on connect
            String welcome = serverIn.nextLine();
            System.out.println("Server: " + welcome);

            statusLabel.setText("Connected. Please log in.");
            statusLabel.setForeground(new Color(0, 128, 0));

        } catch (UnknownHostException e) {
            statusLabel.setText("Error: server host not found.");
            statusLabel.setForeground(Color.RED);
            loginButton.setEnabled(false);

        } catch (ConnectException e) {
            statusLabel.setText("Error: no server running on port " + InspectServer.PORT);
            statusLabel.setForeground(Color.RED);
            loginButton.setEnabled(false);

        } catch (IOException e) {
            statusLabel.setText("Connection error: " + e.getMessage());
            statusLabel.setForeground(Color.RED);
            loginButton.setEnabled(false);
        }
    }

    // -----------------------------------------------------------
    // actionPerformed
    // called whenever a button in this frame is clicked
    // e : the event that was fired — used to identify which button
    // returns : nothing
    // -----------------------------------------------------------
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            doLogin();
        }
    }

    // -----------------------------------------------------------
    // doLogin
    // sends the LOGIN command to the server and handles the response
    // opens MainMenuFrame on success, shows an error message on failure
    // returns : nothing
    // -----------------------------------------------------------
    public void doLogin() {

        String name     = nameField.getText().trim();
        String idStr    = idField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        // validate that all fields are filled in
        if (name.isEmpty() || idStr.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter name, ID, and password.");
            statusLabel.setForeground(Color.RED);
            return;
        }

        // validate that the name contains letters only — no digits or symbols
        for (int i = 0; i < name.length(); i++) {
            if (!Character.isLetter(name.charAt(i))) {
                statusLabel.setText("Name must contain letters only.");
                statusLabel.setForeground(Color.RED);
                return;
            }
        }

        // validate that the ID field contains only digits
        try {
            Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            statusLabel.setText("ID must be a number.");
            statusLabel.setForeground(Color.RED);
            return;
        }

        // send the login command to the server — name, ID, and password included
        serverOut.println("LOGIN:" + name + ":" + idStr + ":" + password);

        // read the server's response
        String response = serverIn.nextLine();
        System.out.println("Server: " + response);

        if (response.equals("LOGIN_OK")) {
            // save the name and open the main menu
            inspectorName = name;
            MainMenuFrame menu = new MainMenuFrame(socket, serverIn, serverOut, inspectorName);
            menu.setVisible(true);

            // close this login window
            this.dispose();

        } else {
            // LOGIN_FAIL:reason — show the reason to the inspector
            if (response.contains(":")) {
                statusLabel.setText(response.split(":", 2)[1]);
            } else {
                statusLabel.setText("Login failed. Check your name and ID.");
            }
            statusLabel.setForeground(Color.RED);
        }
    }

    // -----------------------------------------------------------
    // main — entry point for the entire application
    // starts the GUI on the Event Dispatch Thread
    // -----------------------------------------------------------
    public static void main(String[] args) {
        LoginFrame login = new LoginFrame();
        login.setVisible(true);
    }
}
