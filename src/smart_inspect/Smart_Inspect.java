/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smart_inspect;

import java.io.*;
import java.util.*;
import java.util.ArrayList;

/**
 *
 * @author joud
 */
////---------------------------------------------------------------------
//joud
// need to have options for exit, and message for exit
// welcome message show menu
//menu 1
// 0 : search for establishment by licnese number
// enter establishment license number
// show menu 2
// 1 : search establishment using location
// {search by location : inspectors enters their location, and then we compute and search to find the closest establishments, }
// get the inspectors location
// calculate to find the nearest establihsment
// show all close establisments with their ID and let user chose which establishment they want
// show menu 2
// 2 : exit
//menu 2
// 0 : write inspection result
//    public InspectionResult(Violation violation, String notes, Establishment establishment, Inspector inspector, LocalDate inspectionDate) {
// show the user the format of writing an inspection
// ask the user if there is a violation
//yes : 
// show the form for creating a violation
// chnage the status
// store the inspection result and if there is a violation in the history file
// then show menu again
// 1 : view establishment history (from file)
// read the history file and check for the ID
// if the id is the same as the establishment ID then we print the whole line
// then show menu again
// 2 : view establishment details (from system)
// print the details of the system using the establishment object
// then show menu again
// 3 : go back to menu 1
// 4 : exit
// method that shows menu 1 (rama)
// method that shows menu 2 (rama)
// method that reads history file to print the histroy based on establishment's id (dima)
// method to calculate and find the nearest establishment using location (dima)
// method that finds the establishment using license number (dima)
// method that prints the establishment details from the estalishment object (rama)
public class Smart_Inspect {

    /**
     * @param args the command line arguments
     */
    // class data fields
    private static Scanner inspectorInput;
    public static ArrayList<Establishment> establishments;
    public static ArrayList<Violation> violations;

    private static File historyFile;
    private static File establishmentsFile;
    private static File violationsFile;
    
    private static PrintWriter historyWriter;

    //private static Inspector inspector = null; // The inspector currently using the program //i dont think this needs to be here
    // constructor
    public Smart_Inspect() {

    }

    public static void main(String[] args) throws FileNotFoundException {

        // -------------------------------------------------------------------
        // GUI is now the entry point — launch LoginFrame directly.
        // The console menus below are kept but commented out.
        // To restore the console app, uncomment everything below and comment
        // out the LoginFrame line.
        // -------------------------------------------------------------------
        LoginFrame login = new LoginFrame();
        login.setVisible(true);

//        // objects needed :
//        inspectorInput = new Scanner(System.in);
//        establishments = new ArrayList<>();
//        violations = new ArrayList<>();
//        historyFile = new File("History.txt");
//        establishmentsFile = new File("Establishments.txt");
//        violationsFile = new File("Violations.txt");
//        historyWriter = new PrintWriter("History.txt");
//
//        Smart_Inspect smartInspect = new Smart_Inspect();
//        Inspector inspector = new Inspector();
//        Establishment establishment;
//
//        // Creating the establishments objects and adding them to the arraylist:
//        //smartInspect.addingEstablishmentsToArray();
//        smartInspect.addingObjectToArray(new Establishment());
//
//        // Creating the violations objects and adding them to the arraylist:
//        //smartInspect.addingViolationsToArray();
//        smartInspect.addingObjectToArray(new Violation());
//
//        smartInspect.identifyInspector(inspector);
//
//        boolean systemIsRunning = true;
//
//        while (systemIsRunning) {
//
//            // print the menu
//            showMenu1();
//
//            int choice = inspectorInput.nextInt();
//
//            // switch to get the users's choice
//            if (choice == 0) {
//                    // --- Search by license ---
//                    System.out.print("Enter establishment license number (0–15): ");
//                    int licenseNumber = inspectorInput.nextInt();
//
//                    // Call search for establishment by license method here:
//                    establishment = smartInspect.findEstablishmentByLicenseNumber(licenseNumber);
//                    System.out.println("The Establisment found is :" + establishment.getName());
//
//                    if (establishment != null) {
//                        // Go to menu 2 if establishment is found
//                        smartInspect.handleMenu2(establishment, inspector);
//                    } else {
//                        System.out.println("No establishment found with this license.");
//                    }
//
//            } else if (choice == 1) {
//                    // --- Search for establishment closest to the inspector ---
//                    System.out.print("Enter your latitude (4 digits): ");
//                    int InspectorLatitude = inspectorInput.nextInt();
//
//                    System.out.print("Enter your longitude (4 digits): ");
//                    int InspectorLongitude = inspectorInput.nextInt();
//
//                    inspector.setLocation(InspectorLongitude, InspectorLatitude);
//
//                    establishment = smartInspect.findClosestEstablishment(inspector);
//
//                    System.out.println("The closest Establisment found is :" + establishment.getName());
//
//                    if (establishment != null) {
//                        smartInspect.handleMenu2(establishment, inspector);
//                    } else {
//                        System.out.println("No nearby establishments found.");
//                    }
//
//            } else if (choice == 2) {
//                    System.out.println("Exiting system... Goodbye!");
//                    systemIsRunning = false;
//
//            } else {
//                    System.out.println("Invalid choice, try again.");
//            }
//        }
//
//        historyWriter.flush();
//        historyWriter.close();

    }

    //read from history file
    public void readHistory(Establishment establishment) throws FileNotFoundException { // done

        // historyWriter.println(inspectionResult.toString());
        int licenseNumber = establishment.getLicense().getLicenseNumber();
        try (Scanner readingHistoryFile = new Scanner(historyFile)) {
            int flag = 0;

            while (readingHistoryFile.hasNextLine()) {
                String line = readingHistoryFile.nextLine();

                // Check if this line contains the license number somewhere
                if (line.contains("Establishment License Number=" + licenseNumber)) {
                    System.out.println(line);
                    flag++;
                }

//            if (readingHistoryFile.next().equals(Integer.toString(licenceNumber))) {
//                //result = result + "\n" + scan.nextLine();
//                System.out.println(readingHistoryFile.nextLine());
//                flag++;
//            }
            }
            if (flag == 0) {
                System.out.println("This establishment has no history.");
            }
        }
    }

    //calculate distance
    public int calculateDistance(Establishment establishment, Inspector inspector) { // done
        int dx = establishment.getLocation().getLongitude() - inspector.getLocation().getLongitude();
        int dy = establishment.getLocation().getLatitude() - inspector.getLocation().getLatitude();
        return (int) Math.sqrt(dx * dx + dy * dy);
    }

    //find establishment by licence number
    public Establishment findEstablishmentByLicenseNumber(int licenseNumber) { // done

        for (Establishment establishment : establishments) {
            if (establishment.getLicense().getLicenseNumber() == licenseNumber) {
                return establishment;
            }
        }
        return null;
    }

    public static void showMenu1() { // done
        System.out.println("----- MAIN MENU -----");
        System.out.println("0 - Search Establishment by License");
        System.out.println("1 - Search Establishment by Location");
        System.out.println("2 - Exit Application");
        System.out.print("Enter your choice: ");
    }

    public static void showMenu2() { // done
        System.out.println("\n------ Establishment Menu ------");
        System.out.println("0 - Write inspection result");
        System.out.println("1 - View establishment history");
        System.out.println("2 - View establishment details");
        System.out.println("3 - Return to Main Menu");
        System.out.print("Enter choice: ");

    }

    public void handleMenu2(Establishment establishment, Inspector inspector) throws FileNotFoundException { // done

        // File history, File violationsFile, ArrayList<Violation> Violations
        boolean runningMenu2 = true;

        while (runningMenu2) {
            showMenu2(); 
            int choice = inspectorInput.nextInt();

            if (choice == 0) {// done
                    // --- Write inspection result ---
                    createInspectionResult(establishment, inspector);
                    
            }
            else if (choice == 1) { // done
                    // --- View history ---

                    // Call method here:
                    readHistory(establishment);
                    
            }
            else if (choice == 2) { // done
                    // Call  print method:
                    System.out.println(establishment.toString());
                    
            }
            else if (choice == 3) {// done
                    // Return to Menu 1
                    runningMenu2 = false;
                    
            }
            else { // done
                    System.out.println("Invalid choice. Try again.");
                    
            }
        }
    }

    public void createInspectionResult(Establishment establishment, Inspector inspector) throws FileNotFoundException { // done

        System.out.println("PLease enter date using this format (YYYY-MM-DD) :");
        String date = inspectorInput.next();

        System.out.println("Please enter Inspection Notes :");
        String notes = inspectorInput.nextLine();
        notes = inspectorInput.nextLine();

        InspectionResult inspectionResult = new InspectionResult(notes, establishment, inspector, date);

        System.out.println("Would you like to add a violation ? \n[0] : yes [1] : no");
        int choice = inspectorInput.nextInt();

        if (choice == 0) { // if yes show violation list and take user choice
            showViolationsList();
            System.out.println("Which violation does the establishment have? : ");
            int chosenViolationID = inspectorInput.nextInt();
            Violation violation = getViolation(chosenViolationID);
            if (violation != null) {

                // update the establishment information    
                establishment.setIsViolating(true);
                establishment.setViolationDate(date);
                establishment.setViolationType(violation);

                inspectionResult.setViolation(violation);
            } else {
                 if(establishment.isIsViolating()==true){
                    establishment.setIsViolating(false);
                }
                
                System.out.println("Violation not found, ID entered is wrong, Violation not recorded");
            }
        }

        writeHistory(inspectionResult);

    }

    public Violation getViolation(int chosenViolationID) { // done
        for (Violation violation : violations) {
            if (violation.getID() == chosenViolationID) {
                return violation;
            }
        }
        return null;
    }

    public void writeHistory(InspectionResult inspectionResult) throws FileNotFoundException { // done

        historyWriter.println(inspectionResult.toString());
        historyWriter.flush();
    }

    public static void showViolationsList() { // done

        System.out.println("\n------ Violations List ------");

        for (Violation violation : violations) {
            System.out.println(violation.getID() + " - " + violation.getName() + " - " + violation.getPenaltyPrice());
        }
    }


//    public void addingEstablishmentsToArray() throws FileNotFoundException { // Done
//
//        Scanner establishmentDataReader = new Scanner(establishmentsFile);
//
//        // starting reading the establisment file :
//        while (establishmentDataReader.hasNext()) {
//
//            // putting current line in a string    
//            String currentLine = establishmentDataReader.nextLine();
//
//            // splitting the commands in the string
//            String[] splitLine = currentLine.split(",");
//
//            // for-loop to trim the strings in the array
//            for (int i = 0; i < splitLine.length; i++) {
//                splitLine[i] = splitLine[i].trim();
//            }// end of for loop
//
//            // 0 BeachHouse 2025-03-10 2026-03-10 1023 8472 Mohamad
//            Integer ID = Integer.parseInt(splitLine[0]);
//            String establishmentName = splitLine[1];
//            String issueDate = splitLine[2];
//            String expirationDate = splitLine[3];
//            Integer Longitude = Integer.parseInt(splitLine[4]);
//            Integer Latitude = Integer.parseInt(splitLine[5]);
//            String ownerName = splitLine[6];
//            Establishment establishment = new Establishment(ID, establishmentName, issueDate, expirationDate, Longitude, Latitude, ownerName);
//
//            establishments.add(establishment);
//                    
//        }
//    }
//
//    public void addingViolationsToArray() throws FileNotFoundException { //done
//
//        Scanner violationsDataReader = new Scanner(violationsFile);
//
//        // starting reading the violation file :
//        while (violationsDataReader.hasNext()) {
//
//            // putting current line in a string    
//            String currentLine = violationsDataReader.nextLine();
//
//            // splitting the commands in the string
//            String[] splitLine = currentLine.split(",");
//
//            // for-loop to trim the strings in the array
//            for (int i = 0; i < splitLine.length; i++) {
//                splitLine[i] = splitLine[i].trim();
//            }// end of for loop
//
//            // 4,WaterContaminationRisk,1200
//            Integer ID = Integer.parseInt(splitLine[0]);
//            String violationName = splitLine[1];
//            int penaltyPrice = Integer.parseInt(splitLine[2]);
//
//            Violation violation = new Violation(ID, violationName, penaltyPrice);
//
//            violations.add(violation);
//        }
//    }
    
     public void addingObjectToArray(Object object) throws FileNotFoundException {
        Scanner fileReader = null;

        if (object instanceof Violation) {
            fileReader = new Scanner(violationsFile);
        } else if (object instanceof Establishment) {
            fileReader = new Scanner(establishmentsFile);
        }

        // starting reading the violation file :
        while (fileReader.hasNext()) {

            // putting current line in a string    
            String currentLine = fileReader.nextLine();

            // splitting the commands in the string
            String[] splitLine = currentLine.split(",");

            // for-loop to trim the strings in the array
            for (int i = 0; i < splitLine.length; i++) {
                splitLine[i] = splitLine[i].trim();
            }// end of for loop

            if (object instanceof Violation) {
                // 4,WaterContaminationRisk,1200
            Integer ID = Integer.parseInt(splitLine[0]);
            String violationName = splitLine[1];
            int penaltyPrice = Integer.parseInt(splitLine[2]);

            Violation violation = new Violation(ID, violationName, penaltyPrice);

            violations.add(violation);
            
            } else if (object instanceof Establishment) {
                // 0 BeachHouse 2025-03-10 2026-03-10 1023 8472 Mohamad
            Integer ID = Integer.parseInt(splitLine[0]);
            String establishmentName = splitLine[1];
            String issueDate = splitLine[2];
            String expirationDate = splitLine[3];
            Integer Longitude = Integer.parseInt(splitLine[4]);
            Integer Latitude = Integer.parseInt(splitLine[5]);
            String ownerName = splitLine[6];
            Establishment establishment = new Establishment(ID, establishmentName, issueDate, expirationDate, Longitude, Latitude, ownerName);

            establishments.add(establishment);

            }

        }
    }

    public Establishment findClosestEstablishment(Inspector inspector) { // Done

        Establishment closestEstablishment = null;
        int minDistanceSq = 99999; // temporary distance that will never be the minimum

        for (Establishment establishment : establishments) {

            int distanceBetweenEstablishmentAndInspector = calculateDistance(establishment, inspector);

            if (distanceBetweenEstablishmentAndInspector < minDistanceSq) {
                minDistanceSq = distanceBetweenEstablishmentAndInspector;
                closestEstablishment = establishment;
            }
        }

        return closestEstablishment;

    }

    public void identifyInspector(Inspector inspector) { // Done
        // identify the inspector : (simple login)
        System.out.println("Please Enter your name: ");
        String insspectorName = inspectorInput.next();
        System.out.println("Please Enter your ID: ");
        int insspectorID = inspectorInput.nextInt();
        inspector.setName(insspectorName);
        inspector.setID(insspectorID);
    }

    

}
