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
public class License {
    
    private int licenseNumber;
    private LocalDate issueDate;
    private LocalDate expirationDate;

    public License(int licenseNumber,String issueDate, String expirationDate) {
        this.licenseNumber = licenseNumber;
        this.issueDate = LocalDate.parse(issueDate);
        this.expirationDate = LocalDate.parse(expirationDate);
    }


    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public int getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(int licenseNumber) {
        this.licenseNumber = licenseNumber;
    }
    
    
    
    
}
