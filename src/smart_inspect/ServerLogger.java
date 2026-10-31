package smart_inspect;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// -----------------------------------------------------------
// ServerLogger
// writes timestamped log entries to WARNING.txt
// called from InspectServer, ClientHandler, and ExpiryCheckerTask
//   to record server startup, login events, and expired licenses
// WARNING.txt is opened in append mode each call so entries
//   accumulate across server restarts
// -----------------------------------------------------------
public class ServerLogger {

    // the file all log entries are written to
    private static final String LOG_FILE = "WARNING.txt";

    // timestamp format — produces YYYY-MM-DD HH:mm:ss
    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // -----------------------------------------------------------
    // log
    // appends one timestamped line to WARNING.txt
    // message : the text to record after the timestamp
    // returns : nothing
    // -----------------------------------------------------------
    public static void log(String message) {

        // format the current date and time as YYYY-MM-DD HH:mm:ss
        String timestamp = LocalDateTime.now().format(FORMATTER);

        // open WARNING.txt in append mode — creates the file if it does not exist yet
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            bw.write("[" + timestamp + "] " + message);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("ServerLogger error: " + e.getMessage());
        }
    }
}
