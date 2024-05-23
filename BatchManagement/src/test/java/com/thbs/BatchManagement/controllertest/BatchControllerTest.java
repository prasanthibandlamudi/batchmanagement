package com.thbs.BatchManagement.controllertest;
 
import com.thbs.BatchManagement.controller.BatchController;

import com.thbs.BatchManagement.entity.Batch;
import com.thbs.BatchManagement.entity.EmployeeDTO;
import com.thbs.BatchManagement.exceptionhandler.BatchNotFoundException;
import com.thbs.BatchManagement.exceptionhandler.DuplicateEmployeeException;
import com.thbs.BatchManagement.repository.BatchRepository;
import com.thbs.BatchManagement.service.BatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
 
@ExtendWith(MockitoExtension.class)
public class BatchControllerTest {
 
    @Mock
    private BatchService batchService;
 
    @Mock
    private BatchRepository batchRepository;
 
    @InjectMocks
    private BatchController batchController;
 
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
 
    
    @Test
    public void testCreateBatch() {
        Batch batch = new Batch();
        batch.setBatchName("Test Batch");
 
        when(batchService.createBatch(batch)).thenReturn(ResponseEntity.status(HttpStatus.CREATED).body("Batch created successfully"));
 
        ResponseEntity<?> response = batchController.createBatch(batch);
 
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Batch created successfully", response.getBody());
    }
    
    
    @Test
    public void testAddEmployeesToExistingBatches() {
        // Mock data
        Long batchId = 1L;
        List<EmployeeDTO> employees = new ArrayList<>();
        employees.add(new EmployeeDTO((long) 1));
 
        // Mock addEmployeesToExistingBatches method
        doNothing().when(batchService).addEmployeesToExistingBatches(anyLong(), anyList());
 
        // Perform the addEmployeesToBatch operation
        String result = batchController.addEmployeesToBatch(batchId, employees);
 
        // Verify the result
        assertEquals("Employees added to batch successfully", result);
 
        // Verify that batchService.addEmployeesToExistingBatches was called with the correct arguments
        verify(batchService).addEmployeesToExistingBatches(batchId, employees);
    }
    
    
    @Test
    public void testAddEmployeesToExistingBatchesBulkUpload() throws IOException, BatchNotFoundException, DuplicateEmployeeException {
        // Mock data
        Long batchId = 1L;
        MultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "test data".getBytes());
        List<EmployeeDTO> employees = new ArrayList<>();
        employees.add(new EmployeeDTO((long) 1));
 
        // Mock parseExcel method
        when(batchService.parseExcel(file)).thenReturn(employees);
 
        // Mock addEmployeesToExistingBatchesFromExcel method
        doNothing().when(batchService).addEmployeesToExistingBatchesFromExcel(anyLong(), anyList());
 
        // Perform the addEmployeesToExistingBatchBulkUpload operation
        String result = batchController.addEmployeesToExistingBatchBulkUpload(batchId, file);
 
        // Verify the result
        assertEquals("Employees added to batch successfully", result);
 
        // Verify that batchService.parseExcel and batchService.addEmployeesToExistingBatchesFromExcel were called with the correct arguments
        verify(batchService).parseExcel(file);
        verify(batchService).addEmployeesToExistingBatchesFromExcel(batchId, employees);
    }
  
    
    @Test
    public void testGetBatchById() {
        // Mock data
        Long batchId = 1L;
        Batch batch = new Batch();
        batch.setBatchId(batchId);
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok(batch);
 
        // Mock getBatchById method
        when(batchService.getBatchById(batchId)).thenReturn(expectedResponse);
 
        // Perform the getBatchById operation
        ResponseEntity<Object> response = batchController.getBatchById(batchId);
 
        // Verify the result
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(batch, response.getBody());
    }
    
    
    @Test
    public void testGetBatchByName() {
        // Mock data
        String batchName = "Test Batch";
        Batch batch = new Batch();
        batch.setBatchName(batchName);
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok(batch);
 
        // Mock getBatchByName method
        when(batchService.getBatchByName(batchName)).thenReturn(expectedResponse);
 
        // Perform the getBatchByName operation
        ResponseEntity<Object> response = batchController.getBatchByName(batchName);
 
        // Verify the result
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(batch, response.getBody());
    }
    
 
    @Test
    public void testGetBatchDetails() {
        // Mock data
        Long batchId = 1L;
        Map<String, Long> batchDetails = new HashMap<>();
        batchDetails.put("totalEmployees", 10L);
 
        // Mock the behavior of getBatchDetails() method in the BatchService
        when(batchService.getBatchDetails(batchId)).thenReturn(batchDetails);
 
        // Perform the controller method
        Map<String, Long> result = batchController.getBatchDetails(batchId);
 
        // Verify the result
        assertEquals(batchDetails, result);
 
        // Verify that batchService.getBatchDetails was called with the correct argument
        verify(batchService, times(1)).getBatchDetails(batchId);
    }
    
    
    @Test
    public void testGetAllBatches() {
        // Mock data
        List<Batch> batches = new ArrayList<>();
        batches.add(new Batch());
        batches.add(new Batch());
 
        // Mock getAllBatches method
        when(batchService.getAllBatches()).thenReturn(batches);
 
        // Perform the getAllBatches operation
        List<Batch> response = batchController.getAllBatches();
 
        // Verify the result
        assertEquals(2, response.size());
        assertEquals(batches, response);
    }
    
 
    @Test
    public void testGetAllBatchNames() {
        
        List<String> batchNames = Arrays.asList("Batch1", "Batch2");
        Mockito.when(batchService.getAllBatchNames()).thenReturn(batchNames);
 
        
        List<String> result = batchController.getAllBatchNames();
 
        
        assertEquals(batchNames, result);
    }
    
    
    @Test
    public void testGetEmployeesInBatch() {
        Long batchId = 1L;
        List<Long> employeeIds = new ArrayList<>();
        employeeIds.add((long) 1);
        employeeIds.add((long) 2);
 
        // Mock getEmployeesInBatch method
        when(batchService.getEmployeesInBatch(batchId)).thenReturn(employeeIds);
 
        // Perform the getEmployeesInBatch operation
        List<Long> response = batchController.getEmployeesInBatch(batchId);
 
        // Verify the result
        assertEquals(2, response.size());
        assertEquals(employeeIds, response);
    }
    
    
    @Test
    public void testGetEmployeesInBatchByName() {
        String batchName = "TestBatch";
        List<Long> employeeIds = new ArrayList<>();
        employeeIds.add((long) 1);
        employeeIds.add((long) 2);
 
        // Mock getEmployeesInBatchByName method
        when(batchService.getEmployeesInBatchByName(batchName)).thenReturn(employeeIds);
 
        // Perform the getEmployeesInBatchByName operation
        List<Long> response = batchController.getEmployeesInBatchByName(batchName);
 
        // Verify the result
        assertEquals(2, response.size());
        assertEquals(employeeIds, response);
    }
     
    
    @Test
    void testGetAllBatchNamesWithIds() {
        // Mocking service method
        List<Map<String, Object>> result = batchController.getAllBatchNamesWithIds();
 
        // Verifying return type
        assertNotNull(result);
        assertTrue(result.isEmpty());
 
        
        verify(batchService, times(1)).getAllBatchNamesWithIds();
    }
    
    
    @Test
    public void testGetEmployeesInBatchWithDetails() {
        // Mock data
        Long batchId = 1L;
        List<Map<String, Object>> employeesInBatch = Arrays.asList(
                new HashMap<String, Object>() {{ put("employeeId", 1); put("name", "John Doe"); }},
                new HashMap<String, Object>() {{ put("employeeId", 2); put("name", "Jane Smith"); }}
        );
 
        // Mock the behavior of getEmployeesInBatchWithDetails() method in the BatchService
        when(batchService.getEmployeesInBatchWithDetails(batchId)).thenReturn(employeesInBatch);
 
        // Perform the controller method
        List<Map<String, Object>> result = batchController.getEmployeesInBatchWithDetails(batchId);
 
        // Verify the result
        assertEquals(employeesInBatch, result);
 
        // Verify that batchService.getEmployeesInBatchWithDetails was called with the correct argument
        verify(batchService, times(1)).getEmployeesInBatchWithDetails(batchId);
    }
    
    
    @Test
    public void testDeleteBatchById() {
        Long batchId = 1L;
 
        // Perform the deleteBatch operation
        batchController.deleteBatch(batchId);
 
        // Verify that deleteBatchById is called
        verify(batchService, times(1)).deleteBatchById(batchId);
    }
    
    
    @Test
    public void testUpdateBatchController() {
        // Mock data
        Long batchId = 1L;
        Batch batch = new Batch();
        batch.setBatchName("TestBatch");
        batch.setBatchDescription("Description");
        
 
        // Call the method
        String response = batchController.updateBatch(batchId, batch);
 
        // Verify the response
        assertEquals("Batch details updated successfully", response);
 
        // Verify interactions
        verify(batchService, times(1)).updateBatch(batchId, batch);  
    }
    
    
    @Test
    public void testDeleteEmployeesFromBatch() {
        
        Long batchId = 1L;
        List<Long> employeeIds = Arrays.asList(1L, 2L, 3L);
 
        // Mock the batchService behavior
        doNothing().when(batchService).deleteEmployeesFromBatch(batchId, employeeIds);
 
        // Call the controller method
        String result = batchController.deleteEmployeesFromBatch(batchId, employeeIds);
 
        // Verify the result
        assertEquals("Employees deleted from batch successfully", result);
        verify(batchService, times(1)).deleteEmployeesFromBatch(batchId, employeeIds);
    }
    
    
    @Test
    public void testUpdateLearningPlan() {
        // Mock data
        Long batchId = 1L;
 
        // Perform the controller method
        ResponseEntity<String> response = batchController.updateLearningPlan(batchId);
 
        // Verify the response status
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Learning plan enabled for batch with ID: " + batchId, response.getBody());
 
        // Verify that batchService.updateLearningPlan was called with the correct argument
        verify(batchService, times(1)).updateLearningPlan(batchId);
    }
    
       
    @Test
    public void testUpdateBatch() {
        // Mock data
        Long batchId = 1L;
        Batch batch = new Batch();
        batch.setBatchId(batchId);
        batch.setBatchName("New Batch Name");
 
        // Perform the controller method
        String result = batchController.updateBatch(batchId, batch);
 
        // Verify the result
        assertEquals("Batch details updated successfully", result);
 
        // Verify that batchService.updateBatch was called with the correct arguments
        verify(batchService, times(1)).updateBatch(batchId, batch);
    }
    
    
    @Test
    public void testGetMergedEmployeeIds() {
        // Mock data
        List<Map<String, Object>> mergedEmployeeIds = Arrays.asList(
                new HashMap<String, Object>() {{ put("employeeId", 1); }},
                new HashMap<String, Object>() {{ put("employeeId", 2); }},
                new HashMap<String, Object>() {{ put("employeeId", 3); }}
        );
 
        // Mock the behavior of fetchMergedEmployeeIds() method in the BatchService
        when(batchService.fetchMergedEmployeeIds()).thenReturn(mergedEmployeeIds);
 
        // Perform the controller method
        List<Map<String, Object>> result = batchController.getMergedEmployeeIds();
 
        // Verify the result
        assertEquals(mergedEmployeeIds, result);
 
        // Verify that batchService.fetchMergedEmployeeIds was called
        verify(batchService, times(1)).fetchMergedEmployeeIds();
    }
    
    
    @Test
    public void testFindRemainingEmployees() {
        // Mock data
        Long batchId = 1L;
        List<Map<String, Object>> allEmployees = Arrays.asList(
                new HashMap<String, Object>() {{ put("employeeId", 1); }},
                new HashMap<String, Object>() {{ put("employeeId", 2); }},
                new HashMap<String, Object>() {{ put("employeeId", 3); }}
        );
        List<Integer> allEmployeeIds = allEmployees.stream()
                .map(employee -> (Integer) employee.get("employeeId"))
                .collect(Collectors.toList());
        List<Map<String, Object>> remainingEmployees = Arrays.asList(
                new HashMap<String, Object>() {{ put("employeeId", 1); }},
                new HashMap<String, Object>() {{ put("employeeId", 2); }}
        );
 
        // Mock the behavior of getMergedEmployeeIds() method
        when(batchController.getMergedEmployeeIds()).thenReturn(
            allEmployeeIds.stream()
                .map(employeeId -> {
                    Map<String, Object> employeeMap = new HashMap<>();
                    employeeMap.put("employeeId", employeeId);
                    return employeeMap;
                })
                .collect(Collectors.toList())
        );
 
        // Mock the behavior of findRemainingEmployees() method in the BatchService
        when(batchService.findRemainingEmployees(batchId, allEmployeeIds)).thenReturn(remainingEmployees);
 
        // Perform the controller method
        List<Map<String, Object>> result = batchController.findRemainingEmployees(batchId);
 
        // Verify the result
        assertEquals(remainingEmployees, result);
 
        // Verify that batchService.findRemainingEmployees was called with the correct arguments
        verify(batchService, times(1)).findRemainingEmployees(batchId, allEmployeeIds);
    }
    
    
}
 