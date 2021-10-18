package com.assignment.dev.optgeneration.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.assignment.dev.optgeneration.beans.OTPRequest;
import com.assignment.dev.optgeneration.beans.OtpData;
import com.assignment.dev.optgeneration.beans.Status;
import com.assignment.dev.optgeneration.config.CacheConfiguration;
import com.assignment.dev.optgeneration.repo.OTPRepository;
import com.assignment.dev.optgeneration.service.OTPValidationService;

@Service
public class OTPValidationServiceImpl implements OTPValidationService {

	@Autowired
	OTPRepository otpRepository;
	
	@Autowired
	private Environment env;
	
	@Autowired
	private CacheConfiguration cacheConfiguration;
	
	@Override
	@Transactional
	public Status validateOtp(OTPRequest otpRequest) {
		try{
			if(cacheConfiguration.get(otpRequest.getMsisdn())!= null ){
				if(cacheConfiguration.get(otpRequest.getMsisdn()).getAttempts() >= Integer.parseInt(env.getProperty("spring.otp.allowed.attempt"))){
					return new Status("Attempted too much ",200 , "Wait for some time or try to create new OTP ", "");
				}
				if(otpRequest.getOtp().equals(cacheConfiguration.get(otpRequest.getMsisdn()).getEncodedOtp())){
					return successfulOtpValidation(otpRequest); 
				}else{
					return failedOtpValidationAttempt(otpRequest,cacheConfiguration.get(otpRequest.getMsisdn()));
				}
			}else{
				return validatingOtpIfNotFoundInCache(otpRequest);
			}
		}catch(Exception e){
			return new Status("Could not Validate OTP ",500, e.getMessage(), "");
		}
		
	}

	private Status validatingOtpIfNotFoundInCache(OTPRequest otpRequest) {
		Optional<OtpData> otpdata = otpRepository.isOtpPresentForMsisdn(otpRequest);
		
		if(otpdata.isPresent() && otpdata.get().getOtpCreatedFreq() >= Integer.parseInt(env.getProperty("spring.otp.allowed.freq")) && otpdata.get().getAttempts() >= Integer.parseInt(env.getProperty("spring.otp.allowed.attempt"))){
			return new Status("Exausted ",200 , "Need to wait for " + (Integer.parseInt(env.getProperty("spring.otp.expiry.time")) - otpdata.get().getTimeDiff())+ " minutes", "");
		}
		
		if(otpdata.isPresent() && otpdata.get().getAttempts() < Integer.parseInt(env.getProperty("spring.otp.allowed.attempt"))){
			if(otpRequest.getOtp().equals(otpdata.get().getEncodedOtp())){
				return successfulOtpValidation(otpRequest); 
			}else{
				return failedOtpValidationAttempt(otpRequest,otpdata.get());
			}
		}else{
			Status status = new Status();
			status.setMessaege("Please generate a new OTP");
			status.setStatusCode(200);
			status.setWarningMessage("Please generate a new OTP");
			return status;
		}
	}

	private Status successfulOtpValidation(OTPRequest otpRequest) {
		Status status = new Status();
		otpRepository.validateOtp(otpRequest);
		status.setMessaege("OTP validated ");
		status.setStatusCode(200);
		status.setWarningMessage("Successfully validated");
		cacheConfiguration.remove(otpRequest.getMsisdn());
		return status;
	}

	private Status failedOtpValidationAttempt(OTPRequest otpRequest, OtpData data) {
		Status status = new Status();
		int res = otpRepository.updateAttempts(otpRequest);
		if(res == 0){
			status.setMessaege("OTP Validation Attempted ");
			status.setStatusCode(1004);
			status.setWarningMessage("Attempt limit exceeded ");
			cacheConfiguration.remove(otpRequest.getMsisdn());
		} else {
			data.setAttempts(data.getAttempts() + 1);
			cacheConfiguration.put(otpRequest.getMsisdn(), data);
			if(data.getAttempts() >= 3){
				cacheConfiguration.remove(otpRequest.getMsisdn());
			}
			status.setMessaege("OTP Validation Attempted ");
			status.setStatusCode(200);
			status.setWarningMessage("UnSuccessful otp validation attempt ");
		}
		return status;
	}

}
