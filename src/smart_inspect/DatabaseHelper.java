package smart_inspect;

import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

// -----------------------------------------------------------
// DatabaseHelper
// handles all communication with the MySQL smart_inspect_db database
// every method opens its own connection using try-with-resources
//   and closes it automatically when done
// PreparedStatement is used for all queries that take parameters
//   to prevent SQL injection
// -----------------------------------------------------------
public class DatabaseHelper {

    // database connection details
    private static final String URL      = "jdbc:mysql://localhost:3306/smart_inspect_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Jjanna4634";

    // -----------------------------------------------------------
    // createTables
    // creates all four tables if they do not already exist
    // safe to call every time the server starts — IF NOT EXISTS
    //   means it will not fail or overwrite data if tables are already there
    // returns : nothing
    // -----------------------------------------------------------
    public void createTables() {

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             Statement stat = conn.createStatement()) {

            if (conn != null) {
                System.out.println("Connected to database.");
            }

            // inspectors table — records must be inserted manually before the demo
            stat.executeUpdate("CREATE TABLE IF NOT EXISTS Inspectors (" +
                "ID INT NOT NULL, " +
                "Name VARCHAR(100) NOT NULL, " +
                "Password VARCHAR(100) NOT NULL, " +
                "PRIMARY KEY (ID))");

            // establishments table — records must be inserted manually before the demo
            stat.executeUpdate("CREATE TABLE IF NOT EXISTS Establishments (" +
                "LicenseNumber INT NOT NULL, " +
                "Name VARCHAR(200), " +
                "IssueDate VARCHAR(10), " +
                "ExpirationDate VARCHAR(10), " +
                "Longitude INT, " +
                "Latitude INT, " +
                "OwnerName VARCHAR(100), " +
                "PRIMARY KEY (LicenseNumber))");

            // violations table — records must be inserted manually before the demo
            stat.executeUpdate("CREATE TABLE IF NOT EXISTS Violations (" +
                "ID INT NOT NULL, " +
                "Name VARCHAR(200), " +
                "PenaltyPrice INT, " +
                "PRIMARY KEY (ID))");

            // inspection history table — populated by the Java program at runtime
            stat.executeUpdate("CREATE TABLE IF NOT EXISTS InspectionHistory (" +
                "HistoryID INT NOT NULL AUTO_INCREMENT, " +
                "InspectorName VARCHAR(100), " +
                "EstablishmentLicenseNumber INT, " +
                "InspectionDate VARCHAR(10), " +
                "Notes VARCHAR(500), " +
                "ViolationName VARCHAR(200), " +
                "PRIMARY KEY (HistoryID))");

            System.out.println("All tables are ready.");

        } catch (Exception e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------
    // checkInspectorLogin
    // checks if an inspector with this exact name, ID, and password exists in Inspectors
    // name     : the inspector's name typed at the login screen
    // id       : the inspector's ID number typed at the login screen
    // password : the password typed at the login screen
    // returns : true if a matching row is found, false otherwise
    // -----------------------------------------------------------
    public boolean checkInspectorLogin(String name, int id, String password) {

        String query = "SELECT * FROM Inspectors WHERE Name = ? AND ID = ? AND Password = ?";

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement pstat = conn.prepareStatement(query)) {

            if (conn != null) {
                System.out.println("Connected to database.");
            }

            // fill in the three ? placeholders — positions start at 1
            pstat.setString(1, name);
            pstat.setInt(2, id);
            pstat.setString(3, password);

            ResultSet rs = pstat.executeQuery();

            // rs.next() returns true if at least one matching row was found
            boolean found = rs.next();
            rs.close();
            return found;

        } catch (Exception e) {
            System.out.println("Database error during login check: " + e.getMessage());
            return false;
        }
    }

    // -----------------------------------------------------------
    // getEstablishmentByLicense
    // looks up one establishment in the Establishments table by its license number
    // licenseNumber : the number the inspector typed into the search box
    // returns : a pipe-separated string of establishment details,
    //           or null if no match was found
    // -----------------------------------------------------------
    public String getEstablishmentByLicense(int licenseNumber) {

        String query = "SELECT * FROM Establishments WHERE LicenseNumber = ?";

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement pstat = conn.prepareStatement(query)) {

            pstat.setInt(1, licenseNumber);

            ResultSet rs = pstat.executeQuery();

            if (rs.next()) {

                // build a single string — fields separated by | so the client can split them
                String result = "Name="           + rs.getString("Name")
                              + "|Owner="         + rs.getString("OwnerName")
                              + "|IssueDate="     + rs.getString("IssueDate")
                              + "|ExpirationDate="+ rs.getString("ExpirationDate")
                              + "|Longitude="     + rs.getInt("Longitude")
                              + "|Latitude="      + rs.getInt("Latitude");

                rs.close();
                return result;
            }

            rs.close();
            return null;    // no establishment found with this license number

        } catch (Exception e) {
            System.out.println("Database error during establishment search: " + e.getMessage());
            return null;
        }
    }

    // -----------------------------------------------------------
    // getAllViolations
    // returns every row in the Violations table as one formatted string
    // used by the GUI to populate the violation dropdown
    // returns : a ~ separated string where each entry is "ID,Name,PenaltyPrice"
    //           returns empty string if the table is empty or there is an error
    // -----------------------------------------------------------
    public String getAllViolations() {

        String query = "SELECT * FROM Violations";
        String result = "";

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             Statement stat = conn.createStatement();
             ResultSet rs = stat.executeQuery(query)) {

            // build one string with all violations separated by ~
            while (rs.next()) {
                result = result
                       + rs.getInt("ID") + ","
                       + rs.getString("Name") + ","
                       + rs.getInt("PenaltyPrice")
                       + "~";
            }

        } catch (Exception e) {
            System.out.println("Database error while loading violations: " + e.getMessage());
        }

        return result;
    }

    // -----------------------------------------------------------
    // saveInspectionResult
    // inserts one completed inspection into the InspectionHistory table
    // inspectorName : name of the inspector who performed the inspection
    // licenseNum    : license number of the establishment that was inspected
    // date          : inspection date in YYYY-MM-DD format
    // notes         : the inspector's written notes about the visit
    // violationName : name of the violation found, or empty string if none
    // returns : nothing
    // -----------------------------------------------------------
    public void saveInspectionResult(String inspectorName, int licenseNum, String date, String notes, String violationName) {

        String insert = "INSERT INTO InspectionHistory " +
                        "(InspectorName, EstablishmentLicenseNumber, InspectionDate, Notes, ViolationName) " +
                        "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement pstat = conn.prepareStatement(insert)) {

            pstat.setString(1, inspectorName);
            pstat.setInt(2, licenseNum);
            pstat.setString(3, date);
            pstat.setString(4, notes);

            // store NULL in the database if there was no violation
            if (violationName.isEmpty()) {
                pstat.setNull(5, java.sql.Types.VARCHAR);
            } else {
                pstat.setString(5, violationName);
            }

            pstat.executeUpdate();
            System.out.println("Inspection result saved to database.");

        } catch (Exception e) {
            System.out.println("Database error while saving inspection: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------
    // getHistoryForEstablishment
    // retrieves all inspection records for one establishment from InspectionHistory
    // licenseNumber : license number of the establishment to look up
    // returns : all records as one string — entries separated by ~
    //           returns "No history found." if no records exist
    // -----------------------------------------------------------
    public String getHistoryForEstablishment(int licenseNumber) {

        String query = "SELECT * FROM InspectionHistory WHERE EstablishmentLicenseNumber = ?";
        String result = "";

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement pstat = conn.prepareStatement(query)) {

            pstat.setInt(1, licenseNumber);

            ResultSet rs = pstat.executeQuery();

            // loop through all matching records and build the result string
            while (rs.next()) {

                String line = "Date=" + rs.getString("InspectionDate")
                            + " | Inspector=" + rs.getString("InspectorName")
                            + " | Notes=" + rs.getString("Notes");

                // only add the violation part if one was recorded
                if (rs.getString("ViolationName") != null) {
                    line = line + " | Violation=" + rs.getString("ViolationName");
                }

                // ~ separates entries so the client can split them
                result = result + line + "~";
            }

            rs.close();

        } catch (Exception e) {
            System.out.println("Database error while loading history: " + e.getMessage());
        }

        if (result.equals("")) {
            return "No history found for this establishment.";
        }

        return result;
    }

    // -----------------------------------------------------------
    // getExpiredEstablishments
    // queries all establishments and returns those whose license has expired
    // an establishment is expired if its ExpirationDate is before today's date
    // used by ExpiryCheckerTask to print server-side warnings at startup
    //   and every 24 hours while the server is running
    // returns : an ArrayList of strings — each string is one expired establishment
    //           formatted as "Name (expired: YYYY-MM-DD)"
    //           returns an empty list if no establishments have expired
    // -----------------------------------------------------------
    // -----------------------------------------------------------
    // getInspectionStats
    // runs three queries on InspectionHistory in a single connection
    //   and returns the results as one formatted string
    // the three stats are:
    //   total      — total number of rows in InspectionHistory
    //   mostCommon — the ViolationName that appears most often (NULL rows excluded)
    //   thisMonth  — number of inspections whose date starts with the current YYYY-MM
    // returns : "total=X~mostCommon=Y~thisMonth=Z"
    //           mostCommon is "None" if no violations have been recorded
    // -----------------------------------------------------------
    public String getInspectionStats() {

        int total       = 0;
        String mostCommon = "None";
        int thisMonth   = 0;

        // get the current year-month prefix once — e.g. "2026-05"
        // used in the LIKE query to count inspections in the current month
        String monthPrefix = LocalDate.now().toString().substring(0, 7);

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             Statement stat = conn.createStatement()) {

            if (conn != null) {
                System.out.println("Connected to database.");
            }

            // query 1 — total number of inspections ever recorded
            ResultSet rs1 = stat.executeQuery("SELECT COUNT(*) FROM InspectionHistory");
            if (rs1.next()) {
                total = rs1.getInt(1);
            }
            rs1.close();

            // query 2 — violation name that appears the most times
            // only rows where ViolationName is not NULL are considered
            // LIMIT 1 gives us only the top result
            ResultSet rs2 = stat.executeQuery(
                "SELECT ViolationName, COUNT(*) AS cnt FROM InspectionHistory " +
                "WHERE ViolationName IS NOT NULL " +
                "GROUP BY ViolationName ORDER BY cnt DESC LIMIT 1");
            if (rs2.next()) {
                mostCommon = rs2.getString("ViolationName");
            }
            rs2.close();

            // query 3 — inspections recorded in the current calendar month
            // monthPrefix comes from LocalDate (not user input) so string concat is safe here
            ResultSet rs3 = stat.executeQuery(
                "SELECT COUNT(*) FROM InspectionHistory " +
                "WHERE InspectionDate LIKE '" + monthPrefix + "%'");
            if (rs3.next()) {
                thisMonth = rs3.getInt(1);
            }
            rs3.close();

        } catch (Exception e) {
            System.out.println("Database error while getting stats: " + e.getMessage());
        }

        return "total=" + total + "~mostCommon=" + mostCommon + "~thisMonth=" + thisMonth;
    }

    public ArrayList<String> getExpiredEstablishments() {

        ArrayList<String> expired = new ArrayList<String>();

        // today's date in YYYY-MM-DD format — same format stored in the database
        String today = LocalDate.now().toString();

        String query = "SELECT Name, ExpirationDate FROM Establishments";

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             Statement stat = conn.createStatement();
             ResultSet rs = stat.executeQuery(query)) {

            while (rs.next()) {
                String name    = rs.getString("Name");
                String expDate = rs.getString("ExpirationDate");

                // string comparison works correctly here because both dates
                //   are in YYYY-MM-DD format, which sorts alphabetically the same as chronologically
                if (expDate != null && expDate.compareTo(today) < 0) {
                    expired.add(name + " (expired: " + expDate + ")");
                }
            }

        } catch (Exception e) {
            System.out.println("Database error while checking expiry dates: " + e.getMessage());
        }

        return expired;
    }

    // -----------------------------------------------------------
    // seedFromFiles
    // checks each table — if it is empty, reads the matching txt file
    //   and inserts every row using a PreparedStatement
    // safe to call every startup — only inserts when COUNT(*) returns 0
    // file paths are relative to the project root (where the server runs from)
    // returns : nothing
    // -----------------------------------------------------------
    public void seedFromFiles() {

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             Statement stat = conn.createStatement()) {

            if (conn != null) {
                System.out.println("Connected to database for seeding.");
            }

            // --- check and seed Inspectors table ---
            // format: ID, Name, Password
            ResultSet rsCount = stat.executeQuery("SELECT COUNT(*) FROM Inspectors");
            rsCount.next();
            int inspectorCount = rsCount.getInt(1);
            rsCount.close();

            if (inspectorCount == 0) {
                System.out.println("Seeding Inspectors from Inspectors.txt...");

                String insert = "INSERT INTO Inspectors (ID, Name, Password) VALUES (?, ?, ?)";
                PreparedStatement pstat = conn.prepareStatement(insert);

                // open Inspectors.txt and read every line
                try (BufferedReader buffRead = new BufferedReader(new FileReader("Inspectors.txt"))) {

                    while (buffRead.ready()) {
                        String line = buffRead.readLine().trim();

                        // skip blank lines
                        if (line.isEmpty()) {
                            continue;
                        }

                        // split on ", " — each field is ID, Name, Password
                        String[] parts  = line.split(", ");
                        int id          = Integer.parseInt(parts[0].trim());
                        String name     = parts[1].trim();
                        String password = parts[2].trim();

                        // fill the three placeholders and insert the row
                        pstat.setInt(1, id);
                        pstat.setString(2, name);
                        pstat.setString(3, password);
                        pstat.executeUpdate();
                    }
                }

                pstat.close();
                System.out.println("Inspectors seeded successfully.");

            } else {
                System.out.println("Inspectors table already has data — skipping.");
            }

            // --- check and seed Establishments table ---
            // format: LicenseNumber, Name, IssueDate, ExpirationDate, Longitude, Latitude, OwnerName
            rsCount = stat.executeQuery("SELECT COUNT(*) FROM Establishments");
            rsCount.next();
            int estCount = rsCount.getInt(1);
            rsCount.close();

            if (estCount == 0) {
                System.out.println("Seeding Establishments from Establishments.txt...");

                String insert = "INSERT INTO Establishments " +
                    "(LicenseNumber, Name, IssueDate, ExpirationDate, Longitude, Latitude, OwnerName) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement pstat = conn.prepareStatement(insert);

                try (BufferedReader buffRead = new BufferedReader(new FileReader("Establishments.txt"))) {

                    while (buffRead.ready()) {
                        String line = buffRead.readLine().trim();

                        // skip blank lines
                        if (line.isEmpty()) {
                            continue;
                        }

                        // limit 7 so names with spaces (e.g. "OceanWave Resort") stay intact
                        String[] parts  = line.split(", ", 7);
                        int licNum      = Integer.parseInt(parts[0].trim());
                        String name     = parts[1].trim();
                        String issDate  = parts[2].trim();
                        String expDate  = parts[3].trim();
                        int longitude   = Integer.parseInt(parts[4].trim());
                        int latitude    = Integer.parseInt(parts[5].trim());
                        String owner    = parts[6].trim();

                        // fill the seven placeholders and insert the row
                        pstat.setInt(1, licNum);
                        pstat.setString(2, name);
                        pstat.setString(3, issDate);
                        pstat.setString(4, expDate);
                        pstat.setInt(5, longitude);
                        pstat.setInt(6, latitude);
                        pstat.setString(7, owner);
                        pstat.executeUpdate();
                    }
                }

                pstat.close();
                System.out.println("Establishments seeded successfully.");

            } else {
                System.out.println("Establishments table already has data — skipping.");
            }

            // --- check and seed Violations table ---
            // format: ID,Name,PenaltyPrice  (commas only — no spaces)
            rsCount = stat.executeQuery("SELECT COUNT(*) FROM Violations");
            rsCount.next();
            int violCount = rsCount.getInt(1);
            rsCount.close();

            if (violCount == 0) {
                System.out.println("Seeding Violations from Violations.txt...");

                String insert = "INSERT INTO Violations (ID, Name, PenaltyPrice) VALUES (?, ?, ?)";
                PreparedStatement pstat = conn.prepareStatement(insert);

                // open Violations.txt and read every line
                try (BufferedReader buffRead = new BufferedReader(new FileReader("Violations.txt"))) {

                    while (buffRead.ready()) {
                        String line = buffRead.readLine().trim();

                        // skip blank lines
                        if (line.isEmpty()) {
                            continue;
                        }

                        // split on comma — no spaces in this file
                        String[] parts = line.split(",");
                        int id         = Integer.parseInt(parts[0].trim());
                        String name    = parts[1].trim();
                        int price      = Integer.parseInt(parts[2].trim());

                        // fill the three placeholders and insert the row
                        pstat.setInt(1, id);
                        pstat.setString(2, name);
                        pstat.setInt(3, price);
                        pstat.executeUpdate();
                    }
                }

                pstat.close();
                System.out.println("Violations seeded successfully.");

            } else {
                System.out.println("Violations table already has data — skipping.");
            }

        } catch (Exception e) {
            System.out.println("Error during seeding: " + e.getMessage());
        }
    }
}
