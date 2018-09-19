package com.quantum.app.ws.ui.model.response;

import java.util.Date;

public class ErrorMessage {
	
	private Date timestamp;
	private String message;
	
	public ErrorMessage() {
		
	}
	
	public ErrorMessage(Date timestamp, String message) {
		this.setTimestamp(timestamp);
		this.setMessage(message);
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	

}
