package smart_inspect;

import java.io.*;
import java.net.*;

// -----------------------------------------------------------
// InspectServer
// this is the server program for Smart Inspect
// run this first, then launch the GUI client
// it listens on port 8189, creates tables if they do not exist,
//   and gives each connecting inspector their own thread via ClientHandler
// -----------------------------------------------------------
public class InspectServer {

    // the port number all GUI clients connect to
    public static final int PORT = 8189;

    public static void main(String[] args) {

        System.out.println("Smart Inspect Server starting on port " + PORT + "...");

        // create database tables if they do not already exist
        DatabaseHelper db = new DatabaseHelper();
        db.createTables();

        // seed each table from its txt file if the table is currently empty
        db.seedFromFiles();

        // record that the server has started
        ServerLogger.log("SERVER STARTED");

        // start the expiry checker background thread before accepting any clients
        // it checks license expiry dates immediately, then every 24 hours
        ExpiryCheckerTask expiryTask = new ExpiryCheckerTask();
        Thread expiryThread = new Thread(expiryTask, "ExpiryChecker");
        expiryThread.start();

        // try-with-resources closes the serverSocket automatically if something goes wrong
        try (var s = new ServerSocket(PORT)) {

            System.out.println("Server is ready. Waiting for inspectors to connect...");

            int clientCount = 1;

            // keep accepting new clients forever until the server process is stopped
            while (true) {

                // accept() pauses here until a client connects
                Socket incoming = s.accept();
                System.out.println("Inspector client connected: " + clientCount);

                // create a handler for this client and run it in its own thread
                Runnable r = new ClientHandler(incoming);
                var t = new Thread(r);
                t.start();

                clientCount++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
