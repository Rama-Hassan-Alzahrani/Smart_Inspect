package smart_inspect;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

// -----------------------------------------------------------
// ClientHandler
// handles the full session with one connected inspector client
// implements Runnable so InspectServer can run it in its own thread
// one instance of this class is created for each client that connects
//
// command protocol:
//   Client sends                           Server responds
//   LOGIN:Name:ID                    ->    LOGIN_OK  or  LOGIN_FAIL:reason
//   VIOLATIONS                       ->    VIOLATIONS:id,name,price~id,name,price~...
//   SEARCH:licenseNumber             ->    RESULT:Name=X|Owner=X|...  or  NOT_FOUND
//   INSPECT:licNum|date|notes|viol   ->    SAVED  or  ERROR:reason
//   STATS                            ->    STATS:total=X~mostCommon=Y~thisMonth=Z
//   HISTORY:licenseNumber            ->    HISTORY:line1~line2~...
//   QUIT                             ->    BYE
// -----------------------------------------------------------
public class ClientHandler implements Runnable {

    // the socket for this specific inspector — assigned at construction
    private Socket incoming;

    // the inspector's name is stored here after a successful LOGIN
    // it is included when saving inspection results to the database
    private String loggedInInspectorName;

    // -----------------------------------------------------------
    // constructor
    // incomingSocket : the socket returned by serverSocket.accept()
    // -----------------------------------------------------------
    public ClientHandler(Socket incomingSocket) {
        incoming = incomingSocket;
        loggedInInspectorName = "";
    }

    // -----------------------------------------------------------
    // run
    // called automatically when the thread starts
    // reads commands from the client line by line and dispatches them
    // returns : nothing
    // -----------------------------------------------------------
    @Override
    public void run() {

        // try-with-resources so all streams close cleanly when the session ends
        try (InputStream inStream   = incoming.getInputStream();
             OutputStream outStream = incoming.getOutputStream();
             var in  = new Scanner(inStream, StandardCharsets.UTF_8);
             var out = new PrintWriter(outStream, true, StandardCharsets.UTF_8)) {

            // send welcome message so the client knows the connection is live
            out.println("CONNECTED:Smart Inspect Server Ready");

            boolean done = false;

            // keep reading commands until the client says QUIT or disconnects
            while (!done && in.hasNextLine()) {

                String command = in.nextLine().trim();
                System.out.println("[" + loggedInInspectorName + "] Received: " + command);

                if (command.startsWith("LOGIN:")) {
                    handleLogin(command, out);

                } else if (command.equals("VIOLATIONS")) {
                    handleViolations(out);

                } else if (command.startsWith("SEARCH:")) {
                    handleSearch(command, out);

                } else if (command.startsWith("INSPECT:")) {
                    handleInspection(command, out);

                } else if (command.equals("STATS")) {
                    handleStats(out);

                } else if (command.startsWith("HISTORY:")) {
                    handleHistory(command, out);

                } else if (command.equals("QUIT")) {
                    out.println("BYE");
                    done = true;

                } else {
                    out.println("ERROR:Unknown command");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // -----------------------------------------------------------
    // handleLogin
    // checks the inspector's name, ID, and password against the Inspectors table
    // stores the name in loggedInInspectorName on success so it can be
    //   included in saved inspection records
    // command format : LOGIN:Name:ID:Password
    // out : writer to send the response back to the client
    // returns : nothing
    // -----------------------------------------------------------
    private void handleLogin(String command, PrintWriter out) {

        // split "LOGIN:Joud:123:pass123" into ["LOGIN", "Joud", "123", "pass123"]
        String[] parts = command.split(":");

        if (parts.length < 4) {
            out.println("LOGIN_FAIL:Bad format — expected LOGIN:Name:ID:Password");
            return;
        }

        String name     = parts[1];
        String idStr    = parts[2];
        String password = parts[3];

        try {
            int id = Integer.parseInt(idStr);

            DatabaseHelper db = new DatabaseHelper();
            boolean valid = db.checkInspectorLogin(name, id, password);

            if (valid) {
                // remember the inspector's name for the rest of this session
                loggedInInspectorName = name;
                out.println("LOGIN_OK");
                ServerLogger.log("INSPECTOR CONNECTED — " + name + " (ID: " + id + ")");
            } else {
                out.println("LOGIN_FAIL:Inspector not found — check name, ID, and password");
                ServerLogger.log("FAILED LOGIN ATTEMPT — Name: " + name + ", ID: " + id);
            }

        } catch (NumberFormatException e) {
            out.println("LOGIN_FAIL:ID must be a number");
            ServerLogger.log("FAILED LOGIN ATTEMPT — Name: " + name + ", ID: " + idStr);
        }
    }

    // -----------------------------------------------------------
    // handleViolations
    // fetches all violations from the database and sends them to the client
    // used by the GUI to populate the violation dropdown in InspectionFormFrame
    // out : writer to send the violation list back to the client
    // returns : nothing
    // -----------------------------------------------------------
    private void handleViolations(PrintWriter out) {

        DatabaseHelper db = new DatabaseHelper();
        String violations = db.getAllViolations();

        // send empty string if no violations found — client handles the empty case
        out.println("VIOLATIONS:" + violations);
    }

    // -----------------------------------------------------------
    // handleSearch
    // looks up an establishment by license number and sends details back
    // command format : SEARCH:licenseNumber
    // out : writer to send the result back to the client
    // returns : nothing
    // -----------------------------------------------------------
    private void handleSearch(String command, PrintWriter out) {

        String[] parts = command.split(":");

        if (parts.length < 2) {
            out.println("ERROR:Bad format — expected SEARCH:licenseNumber");
            return;
        }

        try {
            int licenseNumber = Integer.parseInt(parts[1]);

            DatabaseHelper db = new DatabaseHelper();
            String result = db.getEstablishmentByLicense(licenseNumber);

            if (result != null) {
                out.println("RESULT:" + result);
            } else {
                out.println("NOT_FOUND");
            }

        } catch (NumberFormatException e) {
            out.println("ERROR:License number must be a number");
        }
    }

    // -----------------------------------------------------------
    // handleInspection
    // saves a completed inspection to the database
    // the inspector's name comes from loggedInInspectorName (set at LOGIN)
    // command format : INSPECT:licNum|date|notes|violationName
    //   send empty string for violationName if there is no violation
    // out : writer to send the response back to the client
    // returns : nothing
    // -----------------------------------------------------------
    private void handleInspection(String command, PrintWriter out) {

        // remove the "INSPECT:" prefix then split the rest by |
        // -1 limit keeps trailing empty strings so an empty violationName is preserved
        String data  = command.substring("INSPECT:".length());
        String[] parts = data.split("\\|", -1);

        if (parts.length < 4) {
            out.println("ERROR:Bad format — expected INSPECT:licNum|date|notes|violationName");
            return;
        }

        String licenseNumStr = parts[0];
        String date          = parts[1];
        String notes         = parts[2];
        String violationName = parts[3];

        try {
            int licenseNum = Integer.parseInt(licenseNumStr);

            // save to the database — inspector name comes from the session
            DatabaseHelper db = new DatabaseHelper();
            db.saveInspectionResult(loggedInInspectorName, licenseNum, date, notes, violationName);

            out.println("SAVED");

        } catch (NumberFormatException e) {
            out.println("ERROR:License number must be a number");
        }
    }

    // -----------------------------------------------------------
    // handleStats
    // asks DatabaseHelper for inspection statistics and sends them to the client
    // responds with a single formatted string the client can parse and display
    // out : writer to send the stats back to the client
    // returns : nothing
    // -----------------------------------------------------------
    private void handleStats(PrintWriter out) {

        DatabaseHelper db = new DatabaseHelper();
        String stats = db.getInspectionStats();

        out.println("STATS:" + stats);
    }

    // -----------------------------------------------------------
    // handleHistory
    // retrieves all past inspections for one establishment and sends them back
    // command format : HISTORY:licenseNumber
    // out : writer to send the history back to the client
    // returns : nothing
    // -----------------------------------------------------------
    private void handleHistory(String command, PrintWriter out) {

        String[] parts = command.split(":");

        if (parts.length < 2) {
            out.println("ERROR:Bad format — expected HISTORY:licenseNumber");
            return;
        }

        try {
            int licenseNumber = Integer.parseInt(parts[1]);

            DatabaseHelper db = new DatabaseHelper();
            String history = db.getHistoryForEstablishment(licenseNumber);

            out.println("HISTORY:" + history);

        } catch (NumberFormatException e) {
            out.println("ERROR:License number must be a number");
        }
    }
}
