package com.assignment.dev.optgeneration.service;

import com.assignment.dev.optgeneration.beans.OTPRequest;
import com.assignment.dev.optgeneration.beans.Status;

public interface OTPGenerationService {
	
	public Status generateOtp(OTPRequest otpRequest);

}
