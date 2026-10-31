package smart_inspect;

import java.util.ArrayList;

// -----------------------------------------------------------
// ExpiryCheckerTask
// a background task that runs on the server in its own thread
// it checks the database every 24 hours and prints a warning
//   for every establishment whose license has expired
// this demonstrates meaningful use of threading — the task runs
//   independently of the client-handler threads and continues
//   checking even when no inspectors are connected
// implements Runnable so it can be passed to a Thread object
// -----------------------------------------------------------
public class ExpiryCheckerTask implements Runnable {

    // how long to sleep between checks — 24 hours in milliseconds
    private static final long SLEEP_MS = 24L * 60 * 60 * 1000;

    // -----------------------------------------------------------
    // run
    // called automatically when the thread starts
    // loops forever: checks expired licenses, prints warnings, then sleeps
    // returns : nothing (runs until the server process is stopped)
    // -----------------------------------------------------------
    @Override
    public void run() {

        System.out.println("[ExpiryChecker] License expiry checker started.");

        while (true) {

            // run the expiry check
            checkExpiredLicenses();

            // sleep for 24 hours before checking again
            try {
                Thread.sleep(SLEEP_MS);
            } catch (InterruptedException e) {
                // empty catch — if interrupted we just continue and check again
            }
        }
    }

    // -----------------------------------------------------------
    // checkExpiredLicenses
    // asks DatabaseHelper for the list of expired establishments
    // prints a warning line to the server console for each one found
    // if no establishments have expired, prints a confirmation message
    // returns : nothing
    // -----------------------------------------------------------
    public void checkExpiredLicenses() {

        System.out.println("[ExpiryChecker] Checking establishment license expiry dates...");

        DatabaseHelper db = new DatabaseHelper();

        // getExpiredEstablishments compares ExpirationDate strings directly to today's date.
        // this works correctly because ExpirationDate is stored in YYYY-MM-DD format in the
        // database — that format sorts alphabetically in the same order as chronologically,
        // so a simple string compareTo gives the correct earlier/later result.
        ArrayList<String> expired = db.getExpiredEstablishments();

        if (expired.isEmpty()) {

            // no expired licenses — all establishments are within their validity period
            System.out.println("[ExpiryChecker] All establishment licenses are current.");

        } else {

            // print one warning line for each expired establishment and write to WARNING.txt
            System.out.println("[ExpiryChecker] WARNING — " + expired.size() + " expired license(s) found:");

            for (int i = 0; i < expired.size(); i++) {
                System.out.println("[ExpiryChecker]   ! " + expired.get(i));

                // expired.get(i) is already formatted as "Name (expired: YYYY-MM-DD)"
                ServerLogger.log("WARNING — " + expired.get(i));
            }
        }
    }
}
