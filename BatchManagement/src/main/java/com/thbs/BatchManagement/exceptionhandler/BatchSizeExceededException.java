package com.thbs.BatchManagement.exceptionhandler;

public class BatchSizeExceededException extends RuntimeException {

	public BatchSizeExceededException() {
		super();
	}
	public BatchSizeExceededException(String message) {
		super(message);
	}

}


