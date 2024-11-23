package edu.uiowa.cs.warp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;

class ReliabilityVisualizationTest {

    private WarpInterface mockWarp;
    private ReliabilityVisualization reliabilityVisualization;

    @BeforeEach
    void setUp() {
        //Mock the WarpInterface for testing
        mockWarp = new MockWarpInterface();
        
        //Initialize ReliabilityVisualization with the mock
        reliabilityVisualization = new ReliabilityVisualization(mockWarp);
    }

    @Test
    void testConstructor() {
        //Verify if the constructor properly initializes the class
        assertNotNull(reliabilityVisualization);
    }

    @Test
    void testCreateTitle() {
        //Test the title creation
        String expectedTitle = "Reliability Analysis for graph TestGraph\n";
        String actualTitle = reliabilityVisualization.createTitle();
        
        assertEquals(expectedTitle, actualTitle);
    }

    @Test
    void testCreateHeader() {
        //Test the header creation, ensuring the header contains expected values
        Description header = reliabilityVisualization.createHeader();
        
        assertTrue(header.toString().contains("Scheduler Name: Priority"));
        assertTrue(header.toString().contains("M: 0.9"));
        assertTrue(header.toString().contains("E2E: 0.99"));
    }

    @Test
    void testCreateColumnHeader() {
        //Test if the column headers are correctly created
        String[] expectedColumnHeaders = {"F0:A", "F0:B", "F0:C", "F1:C", "F1:B", "F1:A"};
        String[] actualColumnHeaders = reliabilityVisualization.createColumnHeader();
        
        assertArrayEquals(expectedColumnHeaders, actualColumnHeaders);
    }

    @Test
    void testCreateVisualizationData() {
        //Test that visualization data is generated correctly
        String[][] visualizationData = reliabilityVisualization.createVisualizationData();
        
        //Check if the data is a 2D array with expected size
        assertNotNull(visualizationData);
        assertTrue(visualizationData.length > 0);
        assertTrue(visualizationData[0].length > 0);
        
        //Check a specific value in the matrix
        assertEquals("1.0", visualizationData[0][0]);
    }

    @Test
    void testDisplayVisualization() {
        // Test if the displayVisualization method returns a valid GuiVisualization object
        GuiVisualization guiVisualization = reliabilityVisualization.displayVisualization();
        
        assertNotNull(guiVisualization);
        }
    
    @Test
    void testEmptyVisualizationData() {
        //Where no faults are present or no data exists
        mockWarp = new MockWarpInterfaceWithNoData();
        reliabilityVisualization = new ReliabilityVisualization(mockWarp);

        String[][] visualizationData = reliabilityVisualization.createVisualizationData();

        assertNotNull(visualizationData);
        assertEquals(0, visualizationData.length);
    }

    @Test
    void testColumnHeaderCount() {
        //Test the number of columns in the header matches expected value
        String[] columnHeaders = reliabilityVisualization.createColumnHeader();
        int expectedColumnCount = 6; 
        assertEquals(expectedColumnCount, columnHeaders.length);
    }

    @Test
    void testNumFaultsReturnValue() {
        //Simulate getNumFaults returning a specific value (e.g., 5 faults)
        mockWarp = new MockWarpInterfaceWithFaults(5);
        reliabilityVisualization = new ReliabilityVisualization(mockWarp);
        
        //Check if number of faults returned is correct
        assertEquals(5, reliabilityVisualization.getNumFaults());
    }

    @Test
    void testErrorHandlingInVisualization() {
        //Simulate an error by passing invalid or incomplete data
        try {
            mockWarp = new MockWarpInterfaceWithError();
            reliabilityVisualization = new ReliabilityVisualization(mockWarp);
            reliabilityVisualization.createVisualizationData();
            fail("Expected exception not thrown");
        } catch (Exception e) {
            assertTrue(e instanceof RuntimeException); 
        }
    }
}
