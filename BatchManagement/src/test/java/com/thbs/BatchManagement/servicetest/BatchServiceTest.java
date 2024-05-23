package com.thbs.BatchManagement.servicetest;
 
import com.thbs.BatchManagement.entity.Batch;
import com.thbs.BatchManagement.exceptionhandler.BatchEmptyException;
import com.thbs.BatchManagement.exceptionhandler.BatchNotFoundException;
import org.springframework.web.client.HttpClientErrorException;
import com.thbs.BatchManagement.exceptionhandler.DuplicateBatchFoundException;
import com.thbs.BatchManagement.exceptionhandler.EmployeeNotFoundException;
import com.thbs.BatchManagement.exceptionhandler.EmptyEmployeesListException;
import com.thbs.BatchManagement.repository.BatchRepository;
import com.thbs.BatchManagement.service.BatchService;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.*;
import com.thbs.BatchManagement.entity.EmployeeDTO;
import com.thbs.BatchManagement.exceptionhandler.BatchSizeExceededException;
import com.thbs.BatchManagement.exceptionhandler.DuplicateEmployeeException;
 
@ExtendWith(MockitoExtension.class)
public class BatchServiceTest {
 
	@Mock
	private BatchRepository batchRepository;
 
	@InjectMocks
	private BatchService batchService;
 
	
	@Mock
    private Workbook mockWorkbook;
	
	private Map<Long, List<Long>> batchEmployeeMap = new HashMap<>();
	
	private Map<Long, List<Long>> deleteEmployeeMap = new HashMap<>();
	
	@Mock
    private RestTemplate restTemplate;
	
	@BeforeEach
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
 
	@Test
    public void testCreateBatch_Success() {
        // Create a new batch
        Batch batch = new Batch();
        batch.setBatchName("Test Batch");
        batch.setBatchDescription("Test Description");
        batch.setStartDate(LocalDate.now());
        batch.setEndDate(LocalDate.now().plusMonths(1));
        batch.setBatchSize(10L);
 
        // Mock the behavior of batchRepository.existsByBatchName() method
        when(batchRepository.existsByBatchName("Test Batch")).thenReturn(false);
 
        // Test the createBatch method
        ResponseEntity<String> response = batchService.createBatch(batch);
 
        // Verify that batchRepository.save() is called once with the provided batch
        verify(batchRepository, times(1)).save(batch);
 
        // Verify the response
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Batch created successfully", response.getBody());
    }
 
	
    @Test
    public void testCreateBatch_DuplicateBatch() {
        // Create a new batch
        Batch batch = new Batch();
        batch.setBatchName("Test Batch");
 
        // Mock the behavior of batchRepository.existsByBatchName() method
        when(batchRepository.existsByBatchName("Test Batch")).thenReturn(true);
 
        // Test the createBatch method with a duplicate batch
        DuplicateBatchFoundException exception = assertThrows(DuplicateBatchFoundException.class, () -> {
            batchService.createBatch(batch);
        });
 
        // Verify that batchRepository.save() is not called
        verify(batchRepository, never()).save(batch);
 
        // Verify the exception message
        assertEquals("Batch already exists", exception.getMessage());
    }
    
    
	@Test
	public void testGetAllBatchNames() {
	        
	        Batch batch1 = new Batch();
	        batch1.setBatchName("Batch1");
	
	        Batch batch2 = new Batch();
	        batch2.setBatchName("Batch2");
	
	        List<Batch> batches = Arrays.asList(batch1, batch2);
	        Mockito.when(batchRepository.findAll()).thenReturn(batches);
	
	        
	        List<String> result = batchService.getAllBatchNames();
	
	       
	        assertEquals(Arrays.asList("Batch1", "Batch2"), result);
	 }
	
	
     @Test
	 public void testGetAllBatchNamesEmpty() {
		List<Batch> batches = new ArrayList<>();
 
		when(batchRepository.findAll()).thenReturn(batches);
 
		Exception exception = assertThrows(BatchEmptyException.class, () -> batchService.getAllBatchNames());
 
		assertEquals("Batches are not created yet", exception.getMessage());
 
		verify(batchRepository, times(1)).findAll();
	 }
 
 
	@Test
	public void testGetBatchById() {
		Long batchId = 1L;
		Batch batch = new Batch();
		batch.setBatchId(batchId);
 
		when(batchRepository.findById(batchId)).thenReturn(Optional.of(batch));
 
		ResponseEntity<Object> responseEntity = batchService.getBatchById(batchId);
 
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals(batch, responseEntity.getBody());
 
		verify(batchRepository, times(1)).findById(batchId);
	}
 
	
	@Test
	public void testGetBatchByIdNotFound() {
		Long batchId = 999L;
 
		when(batchRepository.findById(batchId)).thenReturn(Optional.empty());
 
		Exception exception = assertThrows(BatchNotFoundException.class, () -> batchService.getBatchById(batchId));
 
		assertEquals("Batch not found with id " + batchId, exception.getMessage());
 
		verify(batchRepository, times(1)).findById(batchId);
	}
 
	
	@Test
	public void testGetBatchByName() {
		String batchName = "TestBatch";
		Batch batch = new Batch();
		batch.setBatchName(batchName);
 
		when(batchRepository.findByBatchName(batchName)).thenReturn(Optional.of(batch));
 
		ResponseEntity<Object> responseEntity = batchService.getBatchByName(batchName);
 
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals(batch, responseEntity.getBody());
 
		verify(batchRepository, times(1)).findByBatchName(batchName);
	}
 
	
	@Test
	public void testGetBatchByNameNotFound() {
		String batchName = "NonExistentBatch";
 
		when(batchRepository.findByBatchName(batchName)).thenReturn(Optional.empty());
 
		Exception exception = assertThrows(BatchNotFoundException.class, () -> batchService.getBatchByName(batchName));
 
		assertEquals("Batch not found with name " + batchName, exception.getMessage());
 
		verify(batchRepository, times(1)).findByBatchName(batchName);
	}
 
	
	@Test
	public void testGetAllBatches() {
		List<Batch> batches = new ArrayList<>();
		batches.add(new Batch());
		batches.add(new Batch());
		batches.add(new Batch());
 
		when(batchRepository.findAll()).thenReturn(batches);
 
		List<Batch> result = batchService.getAllBatches();
 
		assertEquals(batches.size(), result.size());
		assertEquals(batches.get(0), result.get(0));
		assertEquals(batches.get(1), result.get(1));
		assertEquals(batches.get(2), result.get(2));
 
		verify(batchRepository, times(1)).findAll();
	}
 
	
	@Test
	public void testGetAllBatchesEmpty() {
		List<Batch> batches = new ArrayList<>();
 
		when(batchRepository.findAll()).thenReturn(batches);
 
		Exception exception = assertThrows(BatchEmptyException.class, () -> batchService.getAllBatches());
 
		assertEquals("Batches are not created yet", exception.getMessage());
 
		verify(batchRepository, times(1)).findAll();
	}
 
	
	@Test
    public void testGetEmployeesInBatch_NotEmptyList() {
        // Mock data
        Long batchId = 1L;
        Batch batch = new Batch();
        batch.setBatchId(batchId);
        List<Long> employeeIds = new ArrayList<>();
        employeeIds.add(1L);
        employeeIds.add(2L);
        batch.setEmployeeId(employeeIds);

        // Mock repository
        when(batchRepository.findById(batchId)).thenReturn(Optional.of(batch));

        // Test the method
        List<Long> result = batchService.getEmployeesInBatch(batchId);

        // Assertions
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0));
        assertEquals(2L, result.get(1));
    }

	
    @Test
    public void testGetEmployeesInBatch_EmptyList() {
        // Mock data
        Long batchId = 1L;
        Batch batch = new Batch();
        batch.setBatchId(batchId);
        batch.setEmployeeId(Collections.emptyList());

        // Mock repository
        when(batchRepository.findById(batchId)).thenReturn(Optional.of(batch));

        // Test the method
        List<Long> result = batchService.getEmployeesInBatch(batchId);

        // Assertions
        assertEquals(0, result.size());
    }

    
    @Test
    public void testGetEmployeesInBatch_BatchNotFound() {
        // Mock data
        Long batchId = 1L;

        // Mock repository
        when(batchRepository.findById(batchId)).thenReturn(Optional.empty());

        // Test and assertion
        assertThrows(BatchNotFoundException.class, () -> batchService.getEmployeesInBatch(batchId));
    }
	
	
	@Test
	public void testGetEmployeesInBatchByName() {
		String batchName = "TestBatch";
 
		Batch batch = new Batch();
		batch.setBatchName(batchName);
		List<Long> employeeIds = new ArrayList<>();
		employeeIds.add((long) 1);
		employeeIds.add((long) 2);
		batch.setEmployeeId(employeeIds);
 
		when(batchRepository.findByBatchName(batchName)).thenReturn(Optional.of(batch));
 
		List<Long> result = batchService.getEmployeesInBatchByName(batchName);
 
		assertEquals(employeeIds, result);
 
		verify(batchRepository, times(1)).findByBatchName(batchName);
	}
 
	
	@Test
	public void testGetEmployeesInBatchByNameBatchEmpty() {
		String batchName = "EmptyBatch";
 
		Batch batch = new Batch();
		batch.setBatchName(batchName);
		batch.setEmployeeId(new ArrayList<>());
 
		when(batchRepository.findByBatchName(batchName)).thenReturn(Optional.of(batch));
 
		Exception exception = assertThrows(BatchEmptyException.class,
				() -> batchService.getEmployeesInBatchByName(batchName));
 
		assertEquals("No employees found in batch with name " + batchName, exception.getMessage());
 
		verify(batchRepository, times(1)).findByBatchName(batchName);
	}
 
	
	@Test
	public void testGetEmployeesInBatchByNameBatchNotFound() {
		String batchName = "NonExistentBatch";
 
		when(batchRepository.findByBatchName(batchName)).thenReturn(Optional.empty());
 
		Exception exception = assertThrows(BatchNotFoundException.class,
				() -> batchService.getEmployeesInBatchByName(batchName));
 
		assertEquals("Batch with name " + batchName + " not found.", exception.getMessage());
 
		verify(batchRepository, times(1)).findByBatchName(batchName);
	}
 
	
	@Test
	void testGetAllBatchNamesWithIdsWhenBatchesNotEmpty() {
	    // Mocking data
	    List<Batch> batches = new ArrayList<>();
	    Batch batch1 = new Batch();
	    batch1.setBatchId(1L);
	    batch1.setBatchName("Batch A");
	    
	    Batch batch2 = new Batch();
	    batch2.setBatchId(2L);
	    batch2.setBatchName("Batch B");
	    
	    batches.add(batch1);
	    batches.add(batch2);

	    // Mocking repository method
	    when(batchRepository.findAll()).thenReturn(batches);

	    // Calling the service method
	    List<Map<String, Object>> result = batchService.getAllBatchNamesWithIds();

	    // Verifying result
	    assertNotNull(result);
	    assertFalse(result.isEmpty());
	    assertEquals(2, result.size());

	    // Verifying content
	    Map<String, Object> batch1Map = result.get(0);
	    assertEquals(1L, batch1Map.get("batchId"));
	    assertEquals("Batch A", batch1Map.get("batchName"));

	    Map<String, Object> batch2Map = result.get(1);
	    assertEquals(2L, batch2Map.get("batchId"));
	    assertEquals("Batch B", batch2Map.get("batchName"));

	    // Verifying repository method invocation
	    verify(batchRepository, times(1)).findAll();
	}
	
	
    @Test
    void testGetAllBatchNamesWithIdsWhenBatchesEmpty() {
        // Mocking repository method to return an empty list
        when(batchRepository.findAll()).thenReturn(new ArrayList<>());
 
        // Calling the service method
        assertThrows(BatchEmptyException.class, () -> {
            batchService.getAllBatchNamesWithIds();
        });
 
        // Verifying repository method invocation
        verify(batchRepository, times(1)).findAll();
    }
    

    @Test
    public void testGetEmployeesInBatchWithDetails_BatchNotFound() {
        // Mock data
        Long batchId = 1L;

        // Mock repository
        when(batchRepository.findById(batchId)).thenReturn(Optional.empty());

        // Test and assertion
        assertThrows(BatchNotFoundException.class, () -> batchService.getEmployeesInBatchWithDetails(batchId));
    }

    
    private Map<String, Object> createEmployeeMap(Long id, String firstName, String lastName) {
        Map<String, Object> employee = new HashMap<>();
        employee.put("employeeId", id);
        employee.put("firstName", firstName);
        employee.put("lastName", lastName);
        return employee;
    }
    
    
    @Test
    public void testGetBatchDetails() {
        // Mock data
        Long batchId = 1L;
        Batch batch = new Batch();
        batch.setBatchId(batchId);
        batch.setEmployeeId(List.of(1L, 2L, 3L));
        batch.setBatchSize(50L);

        // Mock repository
        when(batchRepository.findById(batchId)).thenReturn(Optional.of(batch));

        // Test the method
        Map<String, Long> result = batchService.getBatchDetails(batchId);

        // Assertions
        assertEquals(3L, result.get("employeeCount"));
        assertEquals(50L, result.get("batchSize"));
    }

    
    @Test
    public void testGetBatchDetails_BatchNotFound() {
        // Mock data
        Long batchId = 1L;

        // Mock repository
        when(batchRepository.findById(batchId)).thenReturn(Optional.empty());

        // Test and assertion
        assertThrows(BatchNotFoundException.class, () -> batchService.getBatchDetails(batchId));
    }
    
    
    @Test
    public void testGetBatchesByEmployeeId() {
        // Mock data
        Long employeeId = 1L;
        Batch batch1 = new Batch();
        batch1.setBatchId(1L);
        batch1.setBatchName("Batch A");
        batch1.setEmployeeId(List.of(1L, 2L, 3L));

        Batch batch2 = new Batch();
        batch2.setBatchId(2L);
        batch2.setBatchName("Batch B");
        batch2.setEmployeeId(List.of(1L, 4L, 5L));

        List<Batch> batches = new ArrayList<>();
        batches.add(batch1);
        batches.add(batch2);

        // Mock repository
        when(batchRepository.findAll()).thenReturn(batches);

        // Test the method
        List<Map<String, Object>> result = batchService.getBatchesByEmployeeId(employeeId);

        // Assertions
        assertEquals(2, result.size());

        // Verify content of first batch
        assertEquals(1L, result.get(0).get("batchId"));
        assertEquals("Batch A", result.get(0).get("batchName"));

        // Verify content of second batch
        assertEquals(2L, result.get(1).get("batchId"));
        assertEquals("Batch B", result.get(1).get("batchName"));
    }

    
    @Test
    public void testGetBatchesByEmployeeId_NoBatchesFound() {
        // Mock data
        Long employeeId = 10L; // Assuming there's no batch associated with this employee

        // Mock repository
        when(batchRepository.findAll()).thenReturn(new ArrayList<>());

        // Test the method
        List<Map<String, Object>> result = batchService.getBatchesByEmployeeId(employeeId);

        // Assertions
        assertEquals(0, result.size());
    }


    @Test
    public void testDeleteBatchById_WithEmployeeIds() {
        // Mock data
        Long batchId = 1L;
        
        // Mocking findById to return empty Optional
        when(batchRepository.findById(batchId)).thenReturn(Optional.empty());

        // Test the method
        assertThrows(BatchNotFoundException.class, () -> batchService.deleteBatchById(batchId));

        // Verify findById method invocation
        verify(batchRepository, times(1)).findById(batchId);
    }

    

    @Test 
    public void testDeleteBatchById_BatchNotFound() {
        // Mock data
        Long batchId = 1L;

        // Mock repository
        when(batchRepository.findById(batchId)).thenReturn(Optional.empty());

        // Test and assertion
        assertThrows(BatchNotFoundException.class, () -> batchService.deleteBatchById(batchId));
    }

    
    @Test
    public void testDeleteProgressForBatch_SuccessfulDeletion() {
        // Mock data
        Long batchId = 1L;
        List<Long> employeeIds = List.of(1L, 2L, 3L);

        // Mock restTemplate
        ResponseEntity<String> responseEntity = new ResponseEntity<>("", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(Class.class))).thenReturn(responseEntity);
 
        // Test the method
        assertDoesNotThrow(() -> batchService.deleteProgressForBatch(batchId, employeeIds));

        // Verify restTemplate method invocation
        verify(restTemplate, times(1)).exchange(anyString(), any(HttpMethod.class), any(), any(Class.class));
    }


    @Test 
    public void testDeleteProgressForBatch_UnsuccessfulDeletion() {
        // Mock data
        Long batchId = 1L;
        List<Long> employeeIds = List.of(1L, 2L, 3L);

        // Mock restTemplate
        ResponseEntity<String> responseEntity = new ResponseEntity<>("", HttpStatus.INTERNAL_SERVER_ERROR);
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(Class.class))).thenReturn(responseEntity);

        // Test and assertion
        assertThrows(RuntimeException.class, () -> batchService.deleteProgressForBatch(batchId, employeeIds));

        // Verify restTemplate method invocation
        verify(restTemplate, times(1)).exchange(anyString(), any(HttpMethod.class), any(), any(Class.class));
    }

    
    @Test
    public void testDeleteEmployeesFromBatch_SuccessfulDeletion() {
        // Mock data
        Long batchId = 1L;
        List<Long> employeeIds = List.of(1L, 2L, 3L);

        // Mock repository
        Batch batch = new Batch();
        batch.setBatchId(batchId);
        batch.setEmployeeId(employeeIds);
        when(batchRepository.findById(batchId)).thenReturn(Optional.of(batch));

        // Mock restTemplate
        ResponseEntity<String> responseEntity = new ResponseEntity<>("", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(), any(), any(Class.class))).thenReturn(responseEntity);

        // Test the method
        assertDoesNotThrow(() -> batchService.deleteEmployeesFromBatch(batchId, employeeIds));

        // Verify repository method invocation
        verify(batchRepository, times(1)).findById(batchId);
        verify(batchRepository, times(1)).save(batch);
    }


    @Test
    public void testGetDeleteEmployeeMap() {
        // Mock data
        Long batchId = 1L;
        List<Long> employeeIds = List.of(1L, 2L, 3L);
        Map<Long, List<Long>> deleteEmployeeMap = new HashMap<>();
        deleteEmployeeMap.put(batchId, employeeIds);
        String jsonPayload = "{\"batchId\": 1, \"userIds\": [1, 2, 3]}";

        // Mock restTemplate
        ResponseEntity<String> responseEntity = new ResponseEntity<>("", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), any(), eq(String.class)))
                .thenReturn(responseEntity);

        // Test the method
        String result = batchService.getDeleteEmployeeMap(deleteEmployeeMap);

        // Assertions
        assertNotNull(result);
        assertTrue(result.contains("\"batchId\": 1"));
        assertTrue(result.contains("\"userIds\": [1, 2, 3]"));
    }

    
    @Test
    public void testPostDeleteEmployeeMap_SuccessfulRequest() {
        // Mock data
        String jsonPayload = "{\"batchId\": 1, \"userIds\": [1, 2, 3]}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>("Success", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), any(), eq(String.class)))
                .thenReturn(responseEntity);

        // Test the method
        String result = assertDoesNotThrow(() -> batchService.postDeleteEmployeeMap(jsonPayload));

        // Assertions
        assertNotNull(result);
        assertEquals("Success", result);
    }

    
    @Test
    public void testPostDeleteEmployeeMap_UnsuccessfulRequest() {
        // Mock data
        String jsonPayload = "{\"batchId\": 1, \"userIds\": [1, 2, 3]}";
        HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), any(), eq(String.class)))
                .thenThrow(exception);

        // Test and assertion
        assertThrows(HttpClientErrorException.class, () -> batchService.postDeleteEmployeeMap(jsonPayload));
    }
       
 
	@Test
    public void testUpdateBatch() {
        // Mock data
        Long batchId = 1L;
        Batch batch = new Batch();
        batch.setBatchName("TestBatch");
        batch.setBatchDescription("Description");
        batch.setStartDate(LocalDate.now());
        batch.setEndDate(LocalDate.now());
        batch.setBatchSize(10L);
 
        // Mock behavior of findById
        Batch existingBatch = new Batch();
        existingBatch.setBatchId(batchId);
        existingBatch.setBatchName("ExistingBatch");
        existingBatch.setBatchDescription("Existing Description");
        existingBatch.setStartDate(LocalDate.now());
        existingBatch.setEndDate(LocalDate.now());
        existingBatch.setBatchSize(5L);
        when(batchRepository.findById(batchId)).thenReturn(Optional.of(existingBatch));
 
        // Call the method
        batchService.updateBatch(batchId, batch);
 
        // Verify that batchRepository.save(existingBatch) was called once
        verify(batchRepository, times(1)).save(existingBatch);
 
        // Verify that the existingBatch has been updated with the new values
        assertEquals("TestBatch", existingBatch.getBatchName());
        assertEquals("Description", existingBatch.getBatchDescription());
        
    }
 
	
    @Test
    public void testUpdateBatchNotFound() {
        // Mock data
        Long batchId = 1L;
        Batch batch = new Batch();
        batch.setBatchName("TestBatch");
        batch.setBatchDescription("Description");
        batch.setStartDate(LocalDate.now());
        batch.setEndDate(LocalDate.now());
        batch.setBatchSize(10L);
 
        // Mock behavior of findById
        when(batchRepository.findById(batchId)).thenReturn(Optional.empty());
 
        // Call the method, should throw BatchNotFoundException
        assertThrows(BatchNotFoundException.class, () -> batchService.updateBatch(batchId, batch));
    }
 
    
    @Test
    public void testUpdateLearningPlan_Success() {
        // Mock a batch with ID 1
        Batch batch = new Batch();
        batch.setBatchId(1L);
        batch.setLearningPlan(false);
 
        // Mock the behavior of batchRepository.findById() method
        when(batchRepository.findById(1L)).thenReturn(java.util.Optional.of(batch));
 
        // Call the updateLearningPlan method
        batchService.updateLearningPlan(1L);
 
        // Verify that batchRepository.findById() is called once with the provided ID
        verify(batchRepository, times(1)).findById(1L);
 
        // Verify that batch.setLearningPlan() is called with true
        assertTrue(batch.isLearningPlan());
 
        // Verify that batchRepository.save() is called once with the updated batch
        verify(batchRepository, times(1)).save(batch);
    }
 
    
    @Test
    public void testUpdateLearningPlan_BatchNotFound() {
        // Mock the behavior of batchRepository.findById() method for a non-existent batch
        when(batchRepository.findById(2L)).thenReturn(java.util.Optional.empty());
 
        // Test the updateLearningPlan method with a non-existent batch
        BatchNotFoundException exception = assertThrows(BatchNotFoundException.class, () -> {
            batchService.updateLearningPlan(2L);
        });
 
        // Verify that batchRepository.findById() is called once with the provided ID
        verify(batchRepository, times(1)).findById(2L);
 
        // Verify the exception message
        assertEquals("Batch with id 2 not found.", exception.getMessage());
 
        // Verify that batchRepository.save() is not called
        verify(batchRepository, never()).save(any());
    }
 
    
    @Test
    void addEmployeesToExistingBatchesFromExcel_WhenBatchNotFound_ShouldThrowBatchNotFoundException() {
        // Arrange
        Long batchId = 1L;
        when(batchRepository.findById(batchId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BatchNotFoundException.class, () ->
                batchService.addEmployeesToExistingBatchesFromExcel(batchId, new ArrayList<>()));
    }

    
    @Test
    void addEmployeesToExistingBatchesFromExcel_WhenAllEmployeesExist_ShouldThrowDuplicateEmployeeException() {
        // Arrange
        Long batchId = 1L;
        List<EmployeeDTO> employees = Arrays.asList(
                new EmployeeDTO(1L),
                new EmployeeDTO(2L)
        );
        Batch existingBatch = new Batch();
        existingBatch.setEmployeeId(Arrays.asList(1L, 2L));
        when(batchRepository.findById(batchId)).thenReturn(Optional.of(existingBatch));

        // Act & Assert
        assertThrows(DuplicateEmployeeException.class, () ->
                batchService.addEmployeesToExistingBatchesFromExcel(batchId, employees));
    }
    
    
    @Test
    void testPostBatchEmployeeMap() {
        // Mock data
        String requestData = "{\"batchId\": 1,\"userIds\": [101, 102]}";
        ResponseEntity<String> mockResponse = new ResponseEntity<>("Success", HttpStatus.OK);
 
        // Mock the behavior of restTemplate.postForEntity() using any() matcher
        when(restTemplate.postForEntity(any(String.class), any(), eq(String.class))).thenReturn(mockResponse);
 
        // Call the method  
        String response = batchService.postBatchEmployeeMap(requestData);
 
        // Verify the response
        assertEquals("Success", response); 
    }
    
    
    @Test
    void testGetBatchEmployeeMap() {
        // Mock data
        Map<Long, List<Long>> batchEmployeeMap = new HashMap<>();
        batchEmployeeMap.put(1L, Arrays.asList(101L, 102L));
        batchEmployeeMap.put(2L, Arrays.asList(201L, 202L, 203L));

        // Call the method
        BatchService batchService = new BatchService();
        String result = batchService.getBatchEmployeeMap(batchEmployeeMap);

        // Assertions
        assertNotNull(result);
        assertTrue(result.contains("\"batchId\": 1"));
        assertTrue(result.contains("\"batchId\": 2"));
        assertTrue(result.contains("\"userIds\": [101, 102]"));
        assertTrue(result.contains("\"userIds\": [201, 202, 203]"));
    }

    
}
 