package com.canadiancoders.backend.dtos.loginDTOS;



public class ResponseMessage 
{		
	private String message;
	private boolean success;
	
	public ResponseMessage(String message, boolean success) {
		this.message = message;
		this.success = success;
	}

	public ResponseMessage(String message)
	{
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	
}
