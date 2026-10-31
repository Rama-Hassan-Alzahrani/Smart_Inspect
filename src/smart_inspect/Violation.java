/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smart_inspect;

//import java.time.LocalDate;

/**
 *
 * @author joud
 */
public class Violation {
    
    private int ID;
    private String name;
    private int penaltyPrice;
    

    public Violation(int ID, String name, int penaltyPrice) {
        this.ID = ID;
        this.name = name;
        this.penaltyPrice = penaltyPrice;
    }
    
     public Violation() {
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPenaltyPrice() {
        return penaltyPrice;
    }

    public void setPenaltyPrice(int penaltyPrice) {
        this.penaltyPrice = penaltyPrice;
    }

    
    
}
