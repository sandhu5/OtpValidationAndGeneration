package com.assignment.dev.optgeneration.repo;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.assignment.dev.optgeneration.beans.OTPRequest;
import com.assignment.dev.optgeneration.beans.OtpData;
import com.assignment.dev.optgeneration.mapper.OtpDataRowMapper;

@Repository
public class OTPRepository {

	@Autowired
	private Environment env;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private OtpDataRowMapper dataRowMapper;

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public Optional<OtpData>  isOtpPresentForMsisdn(OTPRequest otpRequest) {
		String sql = "SELECT msisdn,otp_created_freq,encoded_otp,otp_status,TIMESTAMPDIFF(MINUTE,otp_created_time,NOW()) AS time_diff,attempts,otp_created_time FROM pindata WHERE "
				+ " msisdn = :msisdn "
				+ " AND otp_status = :otpStatus "
				+ " AND TIMESTAMPDIFF(MINUTE,otp_created_time,NOW())  <= :expireTime ; ";
		Map<String, Object> paramMap = queryParams(otpRequest);

		Optional<OtpData> otpData = namedParameterJdbcTemplate.queryForStream(sql, paramMap, dataRowMapper).findFirst();
		return otpData;

	}
	
	public int updateOtpForExistingMsisdn(OTPRequest otpRequest, int existingFreq){
		String sql = "UPDATE pindata SET otp_created_freq = :newFreq ,encoded_otp= :otp , attempts=0 WHERE"
				+ " msisdn = :msisdn "
				+ " AND otp_status = :otpStatus "
				+ " AND TIMESTAMPDIFF(MINUTE,otp_created_time,NOW() )  <=  :expireTime ;";
		try{
			Map<String, Object> paramMap = queryParams(otpRequest);
			paramMap.put("newFreq", existingFreq+1); 
			return namedParameterJdbcTemplate.update(sql, paramMap);
		}catch(Exception e){
			throw new RuntimeException();
		}
	}

	public int generateOtp(OTPRequest otpRequest) {
		String sql = "INSERT INTO authentication.pindata (msisdn, otp_created_freq, attempts, encoded_otp, otp_created_time, otp_validated_time, otp_status)"
				+ "VALUES( :msisdn, :newFreq, 0, :otp, NOW(), NULL, :otpStatus);";
		
		Map<String, Object> paramMap = queryParams(otpRequest);
		return namedParameterJdbcTemplate.update(sql, paramMap);
	}

	private Map<String, Object> queryParams(OTPRequest otpRequest) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("msisdn", otpRequest.getMsisdn());
		paramMap.put("otpStatus", "GENERATED");
		paramMap.put("newFreq", 1);
		paramMap.put("otp",otpRequest.getOtp());
		paramMap.put("expireTime", Integer.parseInt(env.getProperty("spring.otp.expiry.time")));
		return paramMap;
	}
	
	public int validateOtp(OTPRequest otpRequest) {
		String sql = "UPDATE pindata SET otp_status = :otpStatus  WHERE "
				+ " msisdn = :msisdn "
				+ " AND encoded_otp = :otp "
				+ " AND otp_status = 'GENERATED' "
				+ " AND TIMESTAMPDIFF(MINUTE,otp_created_time,NOW() )  <=  :expireTime ;";
		Map<String, Object> paramMap = queryParams(otpRequest);
		paramMap.put("otpStatus", "Validated"); 
		paramMap.put("otp", otpRequest.getOtp()); 
		return namedParameterJdbcTemplate.update(sql, paramMap);
	}

	public int updateAttempts(OTPRequest otpRequest) {
		String sql = "UPDATE pindata SET attempts = attempts+1 WHERE"
				+ " attempts < :attempt "
				+ " AND msisdn = :msisdn "
				+ " AND otp_status = :otpStatus "
				+ " AND TIMESTAMPDIFF(MINUTE,otp_created_time,NOW() )  <=  :expireTime  ;";
		Map<String, Object> paramMap = queryParams(otpRequest);
		paramMap.put("otpStatus", "GENERATED"); 
		paramMap.put("attempt", Integer.parseInt(env.getProperty("spring.otp.allowed.attempt")));
		return namedParameterJdbcTemplate.update(sql, paramMap);
	}

	
	public void remove() {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("expireTime", Integer.parseInt(env.getProperty("spring.otp.expiry.time")));
		String sql = "DELETE FROM pindata WHERE TIMESTAMPDIFF(MINUTE,otp_created_time,NOW() ) > :expireTime AND otp_status = 'GENERATED' ;";
		namedParameterJdbcTemplate.update(sql, paramMap);
	}

}
