/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smart_inspect;

// import java.util.ArrayList;
// import org.junit.After;
// import org.junit.AfterClass;
// import org.junit.Before;
// import org.junit.BeforeClass;
// import org.junit.Test;
// import static org.junit.Assert.*;

// /**
//  *
//  * @author RamaHassan
//  */
 public class Smart_InspectTest {

//     public Smart_InspectTest() {
//     }

//     @BeforeClass
//     public static void setUpClass() {
//     }

//     @AfterClass
//     public static void tearDownClass() {
//     }

//     @Before
//     public void setUp() {
//     }

//     @After
//     public void tearDown() {
//     }

// //    /**
// //     * Test of readHistory method, of class Smart_Inspect.
// //     */
// //    @Test
// //    public void testReadHistory() throws Exception {
// //        System.out.println("readHistory");
// //        Establishment establishment = null;
// //        Smart_Inspect instance = new Smart_Inspect();
// //        instance.readHistory(establishment);
// //        // TODO review the generated test code and remove the default call to fail.
// //        fail("The test case is a prototype.");
// //    }
// //
// //    

//     /**
//      * Test of calculateDistance method, of class Smart_Inspect. test1
//      */
//     @Test
//     public void testCalculateDistance1() {
//         System.out.println("testCalculateDistance1");
//         Establishment e = new Establishment(1, "Place", "2025-01-01", "2026-01-01", 1000, 2000, "Owner");
//         Inspector inspector = new Inspector("Rama", 11);
//         inspector.setLocation(1000, 2000);

//         // Expected result 
//         int expectedResult = 0; //sqrt of(0+0)

//         Smart_Inspect instance = new Smart_Inspect();

//         // Calling the method 
//         int actualresult = instance.calculateDistance(e, inspector);

//         // Comparing the expected value with the actual result
//         assertEquals(expectedResult, actualresult);
//     }

//     //calculate distance test2
//     @Test
//     public void testCalculateDistance2() {
//         System.out.println("testCalculateDistance2");
//         Establishment e = new Establishment(1, "Place", "2025-01-01", "2026-01-01", 3, 4, "Owner");
//         Inspector inspector = new Inspector("Rama", 11);
//         inspector.setLocation(0, 0);

//         // Expected result 
//         int expectedResult = 5;  //sqr root of (16+9)

//         Smart_Inspect instance = new Smart_Inspect();

//         // Calling the method
//         int actualresult = instance.calculateDistance(e, inspector);

//         // Comparing the expected value with the actual result
//         assertEquals(expectedResult, actualresult);
//     }

//     //calculate distance test3
//     @Test
//     public void testCalculateDistance3() {
//         System.out.println("testCalculateDistance3");
//         Smart_Inspect instance = new Smart_Inspect();

//         Establishment e = new Establishment(1, "Beach", "2025-01-01", "2026-01-01", 5000, 10000, "Owner");
//         Inspector inspector = new Inspector("Rama", 11);
//         inspector.setLocation(1000, 2000);
//         // Expected result 
//         int expectedresult = 8944; //sqrt of (16,000,000 + 64,000,000)
//         // Calling the method
//         int actualresult = instance.calculateDistance(e, inspector);
//         // Comparing the expected value with the actual result
//         assertEquals(expectedresult, actualresult);
//     }

//     /**
//      * Test of findEstablishmentByLicenseNumber method, of class Smart_Inspect.
//      *
//      * @throws java.lang.Exception
//      */
//     //test1 valid license num
//     @Test
//     public void testFindEstablishmentbylicensenumber1() throws Exception { // found
//         System.out.println("testFindEstablishmentbylicensenumber1");
//         Smart_Inspect.establishments = new ArrayList<>();
//         Establishment e = new Establishment(10, "MintCafe", "2025-01-01", "2026-01-01", 1000, 2000, "Lina");
//         Smart_Inspect.establishments.add(e);

//         // Expected result 
//         String expectedresult = "MintCafe";

//         Smart_Inspect instance = new Smart_Inspect();

//         // Calling the method
//         Establishment actualresult = instance.findEstablishmentByLicenseNumber(10);
//         assertNotNull(actualresult);

//         // Comparing the expected value with the actual result
//         assertEquals(expectedresult, actualresult.getName());
//     }

//     //findEstablishmentByLicenseNumber test2 invalid license num
//     @Test
//     public void testFindEstablishmentbyLicensenumber2() throws Exception { // not found
//         System.out.println("testFindEstablishmentbyLicensenumber2");
//         Smart_Inspect.establishments = new ArrayList<>();
//         Establishment e = new Establishment(5, "BlueMarina", "2025-01-01", "2026-01-01", 3000, 4000, "Hassan");
//         Smart_Inspect.establishments.add(e);

//         // Expected result
//         String expectedResult = "BlueMarina";

//         Smart_Inspect instance = new Smart_Inspect();

//         // Calling the method
//         Establishment actualResult = instance.findEstablishmentByLicenseNumber(99);

//         // Comparing expected and actual
//         //assertEquals(expectedResult, actualResult);
//         assertNull(actualResult);
//     }

//     //findEstablishmentByLicenseNumber test3 empty arraylist
//     @Test
//     public void testFindEstablishmentbylicensenumber3() throws Exception {
//         System.out.println("testFindEstablishmentbylicensenumber3");
//         Smart_Inspect.establishments = new ArrayList<>(); // empty list

//         // Expected result: 
//         String expectedResult = null;

//         Smart_Inspect instance = new Smart_Inspect();

//         // Calling the method
//         Establishment actualResult = instance.findEstablishmentByLicenseNumber(10);

//         // Comparing expected with actual
//         assertEquals(expectedResult, actualResult);
//     }

// //    /**
// //     * Test of showMenu1 method, of class Smart_Inspect.
// //     */
// //    @Test
// //    public void testShowMenu1() {
// //        System.out.println("showMenu1");
// //        Smart_Inspect.showMenu1();
// //        // TODO review the generated test code and remove the default call to fail.
// //        fail("The test case is a prototype.");
// //    }
// //
// //    /**
// //     * Test of showMenu2 method, of class Smart_Inspect.
// //     */
// //    @Test
// //    public void testShowMenu2() {
// //        System.out.println("showMenu2");
// //        Smart_Inspect.showMenu2();
// //        // TODO review the generated test code and remove the default call to fail.
// //        fail("The test case is a prototype.");
// //    }
// //
// //    /**
// //     * Test of handleMenu2 method, of class Smart_Inspect.
// //     */
// //    @Test
// //    public void testHandleMenu2() throws Exception {
// //        System.out.println("handleMenu2");
// //        Establishment establishment = null;
// //        Inspector inspector = null;
// //        Smart_Inspect instance = new Smart_Inspect();
// //        instance.handleMenu2(establishment, inspector);
// //        // TODO review the generated test code and remove the default call to fail.
// //        fail("The test case is a prototype.");
// //    }
// //
// //    /**
// //     * Test of createInspectionResult method, of class Smart_Inspect.
// //     */
//     @Test
//     public void testCreateInspectionResult1() throws Exception { // is the result created correctly NO VIOLATION
//         System.out.println("createInspectionResult1");

//         // create temp establishment
//         Establishment establishment = new Establishment(4, "PearlCoast Spa", "2025-05-19", "2026-05-19", 5510, 7741, "Reem");
//         // create temp inspector
//         Inspector inspector = new Inspector("ahmad", 123);

//         InspectionResult result = new InspectionResult("Test notes", establishment, inspector, "2025-12-03");

//         assertEquals("Test notes", result.getNotes());
//         assertEquals(establishment, result.getEstablishment());
//         assertEquals(inspector, result.getInspector());
//     }

//     @Test
//     public void testCreateInspectionResult2() throws Exception { // is the violation status updated correctly
//         System.out.println("createInspectionResult2");
//         // create temp establishment
//         Establishment establishment = new Establishment(5, "FreshTide", "2025-06-03", "2026-06-03", 1129, 6632, "Yousef");
//         // create temp violation
//         Violation violation = new Violation(3, "HealthCodeBreach", 850);

//         // Simulate what happens when violation is added
//         establishment.setIsViolating(true);
//         establishment.setViolationDate("2025-12-03");
//         establishment.setViolationType(violation);

//         assertTrue(establishment.isIsViolating());
//         assertEquals(violation, establishment.getViolationType());
//     }

//     /**
//      * Test of getViolation method, of class Smart_Inspect.
//      */
//     @Test
//     public void testGetViolation1() {
//         System.out.println("getViolation1");
//         Smart_Inspect.violations = new ArrayList<>();
//         Violation v1 = new Violation(0, "NoSafetyEquipment", 500);
//         Violation v2 = new Violation(1, "ImproperWasteDisposal", 750);
//         Violation v3 = new Violation(2, "UnauthorizedConstruction", 1000);
//         Smart_Inspect.violations.add(v1);
//         Smart_Inspect.violations.add(v2);
//         Smart_Inspect.violations.add(v3);

//         //Declaring an ID to be tested
//         int chosenViolationID = 0;
//         Smart_Inspect instance = new Smart_Inspect();

//         //Expected result
//         Violation expResult = v1;

//         //Calling the method fot testing
//         Violation result = instance.getViolation(chosenViolationID);
//         assertEquals(expResult, result);

//     }

//     @Test
//     public void testGetViolation2() {
//         System.out.println("getViolation2");
//         Smart_Inspect.violations = new ArrayList<>();
//         Violation v1 = new Violation(0, "NoSafetyEquipment", 500);
//         Violation v2 = new Violation(1, "ImproperWasteDisposal", 750);
//         Violation v3 = new Violation(2, "UnauthorizedConstruction", 1000);
//         Smart_Inspect.violations.add(v1);
//         Smart_Inspect.violations.add(v2);
//         Smart_Inspect.violations.add(v3);

//         //Declaring an ID to be tested
//         int chosenViolationID = 0;
//         Smart_Inspect instance = new Smart_Inspect();

//         //Expected result
//         Violation expResult = v1;

//         //Calling the method fot testing
//         Violation result = instance.getViolation(chosenViolationID);
//         assertEquals(expResult, result);

//     }

//     @Test
//     public void testGetViolation3() {
//         System.out.println("getViolation3");
//         Smart_Inspect.violations = new ArrayList<>();
//         Violation v1 = new Violation(0, "NoSafetyEquipment", 500);
//         Violation v2 = new Violation(1, "ImproperWasteDisposal", 750);
//         Violation v3 = new Violation(2, "UnauthorizedConstruction", 1000);
//         Smart_Inspect.violations.add(v1);
//         Smart_Inspect.violations.add(v2);
//         Smart_Inspect.violations.add(v3);

//         //Declaring an ID to be tested
//         int chosenViolationID = 4;
//         Smart_Inspect instance = new Smart_Inspect();

//         //Expected result
//         Violation expResult = null;

//         //Calling the method fot testing
//         Violation result = instance.getViolation(chosenViolationID);
//         assertEquals(expResult, result);

//     }
// //
// //    /**
// //     * Test of writeHistory method, of class Smart_Inspect.
// //     */
// //    @Test
// //    public void testWriteHistory() throws Exception {
// //        System.out.println("writeHistory");
// //        InspectionResult inspectionResult = null;
// //        Smart_Inspect instance = new Smart_Inspect();
// //        instance.writeHistory(inspectionResult);
// //        // TODO review the generated test code and remove the default call to fail.
// //        fail("The test case is a prototype.");
// //    }
// //
// //    /**
// //     * Test of showViolationsList method, of class Smart_Inspect.
// //     */
// //    @Test
// //    public void testShowViolationsList() {
// //        System.out.println("showViolationsList");
// //        Smart_Inspect.showViolationsList();
// //        // TODO review the generated test code and remove the default call to fail.
// //        fail("The test case is a prototype.");
// //    }
// //
// //    /**
// //     * Test of addingObjectToArray method, of class Smart_Inspect.
// //     */
// //    @Test
// //    public void testAddingObjectToArray() throws Exception {
// //        System.out.println("addingObjectToArray");
// //        Object object = null;
// //        Smart_Inspect instance = new Smart_Inspect();
// //        instance.addingObjectToArray(object);
// //        // TODO review the generated test code and remove the default call to fail.
// //        fail("The test case is a prototype.");
// //    }
// //
// //    /**
// //     * Test of findClosestEstablishment method, of class Smart_Inspect.
// //     */

//     @Test
//     public void testFindClosestEstablishment1() {
//         System.out.println("findClosestEstablishment1");
//         Smart_Inspect.establishments = new ArrayList<>();
//         Establishment e1 = new Establishment(8, "PalmCove Retreat", "2025-09-07", "2026-09-07", 3911, 2094, "Khalid");
//         Establishment e2 = new Establishment(2, "GreenBay Retreat", "2025-02-14", "2026-02-14", 3940, 1128, "Ahmad");
//         Smart_Inspect.establishments.add(e2);

//         //Declaring an inspector with his location
//         Inspector inspector = new Inspector("mohammad", 1234);
//         inspector.setLocation(579, 98);
//         Smart_Inspect instance = new Smart_Inspect();

//         //Expected Result
//         Establishment expResult = e2;

//         //Calling the method for testing
//         Establishment result = instance.findClosestEstablishment(inspector);
//         assertEquals(expResult, result);

//     }

//     @Test
//     public void testFindClosestEstablishment2() { // list is empty
//         System.out.println("findClosestEstablishment2");
//         Smart_Inspect.establishments = new ArrayList<>();
// //        Establishment e1 = new Establishment(8, "PalmCove Retreat", "2025-09-07", "2026-09-07", 3911, 2094, "Khalid");
// //        Establishment e2 = new Establishment(2, "GreenBay Retreat", "2025-02-14", "2026-02-14", 3940, 1128, "Ahmad");
// //        Smart_Inspect.establishments.add(e2);

//         //Declaring an inspector with his location
//         Inspector inspector = new Inspector("mohammad", 1234);
//         inspector.setLocation(579, 98);
//         Smart_Inspect instance = new Smart_Inspect();

//         //Expected Result
//         //Establishment expResult = e1;

//         //Calling the method for testing
//         Establishment result = instance.findClosestEstablishment(inspector);
//         assertNull(result);

//     }

// //
// //    /**
// //     * Test of identifyInspector method, of class Smart_Inspect.
// //     */
// //    @Test
// //    public void testIdentifyInspector() {
// //        System.out.println("identifyInspector");
// //        Inspector inspector = null;
// //        Smart_Inspect instance = new Smart_Inspect();
// //        instance.identifyInspector(inspector);
// //        // TODO review the generated test code and remove the default call to fail.
// //        fail("The test case is a prototype.");
// //    }
// //    
}
