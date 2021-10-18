package com.assignment.dev.optgeneration.mapper;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PinData {

	
	String msisdn;
	String otpStatus;
	int otpCreatedFreq;
	int attempts;
	Date otpCreatedTime;
	Date otpValidatedTime;
	String encodedOtp;
	int timeDiff;



}
