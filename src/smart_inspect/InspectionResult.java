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
public class InspectionResult {
    
    private Violation violation;
    private String notes;
    private Establishment establishment;
    private Inspector inspector;
    private LocalDate inspectionDate;

    public InspectionResult(String notes, Establishment establishment, Inspector inspector, String inspectionDate) {
        this.notes = notes;
        this.establishment = establishment;
        this.inspector = inspector;
        this.inspectionDate = LocalDate.parse(inspectionDate);
    }

    public Violation getViolation() {
        return violation;
    }

    public void setViolation(Violation violation) {
        this.violation = violation;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Establishment getEstablishment() {
        return establishment;
    }

    public void setEstablishment(Establishment establishment) {
        this.establishment = establishment;
    }

    public Inspector getInspector() {
        return inspector;
    }

    public void setInspector(Inspector inspector) {
        this.inspector = inspector;
    }

    public LocalDate getInspectionDate() {
        return inspectionDate;
    }

    public void setInspectionDate(LocalDate inspectionDate) {
        this.inspectionDate = inspectionDate;
    }

    @Override
    public String toString() {
        return "Inspection{"
                + "Date=" + inspectionDate
                + ", Inspector=" + inspector.getName()
                + ", Establishment=" + establishment.getName()
                + ", Establishment License Number=" + establishment.getLicense().getLicenseNumber()
                + ", Notes=" + notes
                + (violation != null ? ", Violation=" + violation.getName() : "")
                + "}";
    }
    
    
    
    
}
