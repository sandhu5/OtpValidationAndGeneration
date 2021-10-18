package com.assignment.dev.optgeneration.service;

import com.assignment.dev.optgeneration.beans.OTPRequest;
import com.assignment.dev.optgeneration.beans.Status;

public interface OTPValidationService {
	
	public Status validateOtp(OTPRequest otpRequest);
}
