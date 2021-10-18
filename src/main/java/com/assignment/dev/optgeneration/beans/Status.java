package com.assignment.dev.optgeneration.beans;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Status {
	String messaege;
	int statusCode ;
	String warningMessage;
	String otp;

}
