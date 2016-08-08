package com.learn.monitor.impl;

import com.learn.monitor.AlertService;

public class AlertServiceMockImpl implements AlertService {
   
	private String alertMsg;

	@Override
	public void alert(String message) {
		this.alertMsg = message;
	}

	public String getAlertMsg() {
		return alertMsg;
	}

	public void setAlertMsg(String alertMsg) {
		this.alertMsg = alertMsg;
	}
    
	
	
	
}
