package com.assignment.dev.optgeneration.beans;

import java.sql.Date;

import com.hazelcast.internal.serialization.SerializableByConvention;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SerializableByConvention
@Builder
public class OtpData {
	
	String msisdn;
	String otpStatus;
	int otpCreatedFreq;
	int attempts;
	Date otpCreatedTime;
	Date otpValidatedTime;
	String encodedOtp;
	long timeDiff;

}
