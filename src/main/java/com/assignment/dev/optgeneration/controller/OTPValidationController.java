package com.assignment.dev.optgeneration.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.assignment.dev.optgeneration.beans.OTPRequest;
import com.assignment.dev.optgeneration.beans.Status;
import com.assignment.dev.optgeneration.service.OTPValidationService;

@RestController
@RequestMapping("/otp")
public class OTPValidationController {

	@Autowired
	private OTPValidationService otpValidationService;
	
	@ApiOperation(value = "validate Otp", nickname = "validateOtpPOST", notes = "", response = Status.class, tags={ "otpValidationController"})
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Successfully validated OTP ", response = Status.class),
        @ApiResponse(code = 1002, message = "Limit is reached"),
        @ApiResponse(code = 1003, message = "Limit is reached"),
        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
        @ApiResponse(code = 500, message = "Application failed to process the request") })

	@PostMapping("/validate")
	public ResponseEntity<Status> validateOtp(@ApiParam(value = "otpRequest", required = true) @RequestBody OTPRequest otpRequest) {
		Status otpStatus = otpValidationService.validateOtp(otpRequest);
		ResponseEntity<Status> resp = null;
		if(otpStatus.getStatusCode() == 200){
			resp = new ResponseEntity<>(otpStatus, HttpStatus.OK);
		}else if(otpStatus.getStatusCode() == 201){
			resp = new ResponseEntity<>(otpStatus, HttpStatus.FOUND);
		}else if(otpStatus.getStatusCode() == 1003){
			resp = new ResponseEntity<>(otpStatus, HttpStatus.OK);
		}
		
		return resp;
	}
}
