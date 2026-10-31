/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smart_inspect;

import java.time.LocalDate;

        
/**
 *
 * @author joud
 */
public class Establishment {
    
    private String name;   
    private License license;
    private Location location;
    private String ownerName;
    private boolean isViolating;
    private LocalDate violationDate;
    private Violation violationType;

    public Establishment() {
    }

    public Establishment(int licenseNumber, String name, String issueDate, String expirationDate, int Longitude, int Latitude, String ownerName) {
        this.name = name;
        this.license = new License(licenseNumber, issueDate, expirationDate);
        this.location = new Location( Longitude,  Latitude);
        this.ownerName = ownerName;
        this.isViolating = false;
        this.violationDate = null;
        this.violationType = null;
        
    }

    public Violation getViolationType() {
        return violationType;
    }

    public void setViolationType(Violation violationType) {
        this.violationType = violationType;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public License getLicense() {
        return license;
    }

    public void setLicense(License license) {
        this.license = license;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public boolean isViolationStatus() {
        return isViolating;
    }

    public void setViolationStatus(boolean violationStatus) {
        this.isViolating = violationStatus;
    }

    public boolean isIsViolating() {
        return isViolating;
    }

    public void setIsViolating(boolean isViolating) {
        this.isViolating = isViolating;
    }

    public LocalDate getViolationDate() {
        return violationDate;
    }

    public void setViolationDate(String violationDate) {
        this.violationDate = LocalDate.parse(violationDate);
    }
@Override
public String toString() {
    return "----- Establishment Details -----\n" +
           "Name: " + getName() + "\n" +
           "Owner: " + getOwnerName() + "\n" +
           "\n" +
           "License Number: " + getLicense().getLicenseNumber() + "\n" +
           "License Issue Date: " + getLicense().getIssueDate() + "\n" +
           "License Expiration Date: " + getLicense().getExpirationDate() + "\n" +
           "\n" +
           "Location (Longitude, Latitude): " +
               getLocation().getLongitude() + ", " +
               getLocation().getLatitude() + "\n" +
           "\n" +
           "Violation Status: " +
               (isIsViolating()
                ? "Violating — Violation Date: " + getViolationDate()
                : "Clean") + "\n" +
           "----------------------------------------------";
}

    
    
    
    
}
