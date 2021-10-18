package com.assignment.dev.optgeneration.service.impl;

import java.sql.Date;
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
import com.assignment.dev.optgeneration.service.OTPGenerationService;
import com.assignment.dev.optgeneration.util.Utility;

@Service
public class OTPGenerationServiceImpl implements OTPGenerationService {
	
	@Autowired
	private Environment env;
	
	@Autowired
	private OTPRepository repo;
	
	@Autowired
	private CacheConfiguration cacheConfiguration;

	@Override
	@Transactional(rollbackFor={Exception.class})
	public Status generateOtp(OTPRequest otpRequest) {
		try{
			if(cacheConfiguration.get(otpRequest.getMsisdn())!= null ){
				OtpData data = cacheConfiguration.get(otpRequest.getMsisdn());
				return new Status("Already generated",201, (Integer.parseInt(env.getProperty("spring.otp.allowed.freq")) - data.getOtpCreatedFreq()) + " Chances remaining ", data.getEncodedOtp());
			}else {
				otpRequest.setOtp(Utility.createUniqueOtp(4));
				Optional<OtpData> otpData = repo.isOtpPresentForMsisdn(otpRequest);
				if (otpData.isPresent()) {
					return checkAndGenerateIfAlreadyExist(otpRequest, otpData);
				} else {
					return createNewOtp(otpRequest);
				}
			}
		}catch(Exception e){
			return new Status("Could not generate OTP ",500, e.getMessage(), "");
		}
	}

	private Status createNewOtp(OTPRequest otpRequest) {
		int res = repo.generateOtp(otpRequest);
		Status status = new Status();
		if(res > 0){
			successOtpCreationMessage(otpRequest, status);
			OtpData otpData = new OtpData(otpRequest.getMsisdn(), "GENERATED",1,0,new Date(System.currentTimeMillis()),null,otpRequest.getOtp(),0);
			cacheConfiguration.put(otpRequest.getMsisdn(),otpData);
		}else{
			status.setMessaege("Otp not generated");
		}
		return status;
	}

	private void successOtpCreationMessage(OTPRequest otpRequest, Status status) {
		status.setMessaege("Successfully generated");
		status.setOtp(otpRequest.getOtp());
		status.setStatusCode(200);
		status.setWarningMessage("You have generated otp for number "+otpRequest.getMsisdn() + " and remaining chances to gerate new otp are "+ (Integer.parseInt(env.getProperty("spring.otp.allowed.freq")) - 1));
	}

	private Status checkAndGenerateIfAlreadyExist(OTPRequest otpRequest, Optional<OtpData> otpData) {
		OtpData datainDb = otpData.get();
		
		if( datainDb.getOtpCreatedFreq() < Integer.parseInt(env.getProperty("spring.otp.allowed.freq")) && datainDb.getAttempts() < Integer.parseInt(env.getProperty("spring.otp.allowed.attempt"))){
			return ifExistedButRemovedFromCache(otpRequest, otpData);
		}else if( datainDb.getOtpCreatedFreq() < Integer.parseInt(env.getProperty("spring.otp.allowed.freq"))){
			return ifPresentAndChancesAreRemaining(otpRequest, datainDb);
		}else{
			return new Status("Already created max OTPs ",200 , "Need to wait for " + (Integer.parseInt(env.getProperty("spring.otp.expiry.time")) - datainDb.getTimeDiff()) + " minutes", " Try with "+ datainDb.getEncodedOtp());
		}
	}

	private Status ifExistedButRemovedFromCache(OTPRequest otpRequest,Optional<OtpData> otpData) {
		Status status = new Status();
		status.setMessaege("Already existed ");
		status.setOtp(otpData.get().getEncodedOtp());
		status.setStatusCode(201);
		status.setWarningMessage("Otp for number "+otpRequest.getMsisdn() + " is " + otpData.get().getEncodedOtp()+ " and remaining chances to gerate new otp are "+ (Integer.parseInt(env.getProperty("spring.otp.allowed.freq")) - (otpData.get().getOtpCreatedFreq()) ));
		cacheConfiguration.put(otpRequest.getMsisdn(),otpData.get());
		return status;
	}

	private Status ifPresentAndChancesAreRemaining(OTPRequest otpRequest, OtpData datainDb) {
		Status status = new Status();
		int result = repo.updateOtpForExistingMsisdn(otpRequest,datainDb.getOtpCreatedFreq());
		if(result > 0){
			status.setMessaege("Successfully updated ");
			status.setOtp(otpRequest.getOtp());
			status.setStatusCode(201);
			status.setWarningMessage("You have generated otp for number "+otpRequest.getMsisdn()+ " for " + (datainDb.getOtpCreatedFreq()+1) + " times and remaining chances to gerate new otp are "+ (Integer.parseInt(env.getProperty("spring.otp.allowed.freq")) - (datainDb.getOtpCreatedFreq()+1) ));
			
			datainDb.setOtpCreatedFreq(datainDb.getOtpCreatedFreq()+1);
			datainDb.setEncodedOtp(otpRequest.getOtp());
			datainDb.setAttempts(0);
			cacheConfiguration.put(otpRequest.getMsisdn(),datainDb);
		}
		return status;
	}

}
