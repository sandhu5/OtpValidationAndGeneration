package com.assignment.dev.optgeneration.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.assignment.dev.optgeneration.repo.OTPRepository;

@Component
@EnableAsync
@EnableScheduling
public class SchedularForExpiredOtp {

	@Autowired
	private OTPRepository otpRepository;

	@Scheduled(fixedRateString = "${interval}")
	@Async
	public void removeExpiredOtps() throws InterruptedException {
		otpRepository.remove();
	}
}
