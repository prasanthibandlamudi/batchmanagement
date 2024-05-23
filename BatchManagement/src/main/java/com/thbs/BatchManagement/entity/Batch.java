package com.thbs.BatchManagement.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.ElementCollection;
import java.time.LocalDate;
import java.util.List;

@Entity  
@Table(name="Batch", uniqueConstraints = @UniqueConstraint(columnNames = "batchName"))
public class Batch {
	
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long batchId;
	
	private String batchName;
	
	private String batchDescription;
	
	private LocalDate startDate;
	
	private LocalDate endDate;
	
	private Long batchSize;
	
	private boolean learningPlan;
	
	@ElementCollection
	private List<Long> employeeId;
				
	public Batch() {
		super();
	}

	public Batch(Long batchId, String batchName, String batchDescription, LocalDate startDate, LocalDate endDate,
			Long batchSize, boolean learningPlan, List<Long> employeeId) {
		super();
		this.batchId = batchId;
		this.batchName = batchName;
		this.batchDescription = batchDescription;
		this.startDate = startDate;
		this.endDate = endDate;
		this.batchSize = batchSize;
		this.learningPlan = learningPlan;
		this.employeeId = employeeId;
	}

	public Long getBatchId() {
		return batchId;
	}   

	public void setBatchId(Long batchId) {
		this.batchId = batchId;
	}

	public String getBatchName() {
		return batchName;
	}

	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}
	
	public String getBatchDescription() {
		return batchDescription;
	}

	public void setBatchDescription(String batchDescription) {
		this.batchDescription = batchDescription;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate localDate) {
		this.startDate = localDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate localDate) {
		this.endDate = localDate;
	}

	public List<Long> getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(List<Long> employeeId) {
		this.employeeId = employeeId;
	}

	public Long getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(Long batchSize) {
		this.batchSize = batchSize;
	}

	public boolean isLearningPlan() {
		return learningPlan;
	}

	public void setLearningPlan(boolean learningPlan) {
		this.learningPlan = learningPlan;
	}

	
}
