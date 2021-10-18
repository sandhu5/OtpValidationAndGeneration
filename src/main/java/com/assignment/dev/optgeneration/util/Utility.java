package com.assignment.dev.optgeneration.util;

import java.util.Random;

public class Utility {
	
	public static String createUniqueOtp(int otpSize) {
		String values = "0123456789";
		Random random = new Random();
		char[] otp = new char[otpSize];
		for (int i = 0; i < otpSize; i++) {
			otp[i] = values.charAt(random.nextInt(values.length()));
		}
		return String.valueOf(otp);
	}

}
