package com.assignment.dev.optgeneration.mapper;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.assignment.dev.optgeneration.beans.OtpData;

@Component
public class OtpDataRowMapper implements RowMapper<OtpData> {

	@Override
	public OtpData mapRow(ResultSet resultSet, int rowNum) throws SQLException {

		String msisdn = resultSet.getString("msisdn");
		String otpStatus = resultSet.getString("otp_status");
		String otp = resultSet.getString("encoded_otp");
		int otpCreated = resultSet.getInt("otp_created_freq");
		int timeDiff = resultSet.getInt("time_diff");
		int attempts = resultSet.getInt("attempts");
		Date otpCreatedTime = resultSet.getDate("otp_created_time");

		return new OtpData(msisdn,otpStatus,otpCreated,attempts,otpCreatedTime,null,otp,timeDiff);

	}

}