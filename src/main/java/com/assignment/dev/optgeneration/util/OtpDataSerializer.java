package com.assignment.dev.optgeneration.util;

import java.io.IOException;
import java.sql.Date;

import com.assignment.dev.optgeneration.beans.OtpData;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;

public class OtpDataSerializer implements StreamSerializer<OtpData> {

	  @Override
	  public void write(ObjectDataOutput out, OtpData data) throws IOException {
	    out.writeUTF(data.getMsisdn());
	    out.writeUTF(data.getOtpStatus());
	    out.writeInt(data.getOtpCreatedFreq());
	    out.writeInt(data.getAttempts());
	    out.writeObject(data.getOtpCreatedTime());
	    out.writeObject(data.getOtpValidatedTime());
	    out.writeUTF(data.getEncodedOtp());
	    out.writeLong(data.getTimeDiff());
	  }

		String otpStatus;
		int otpCreatedFreq;
		int attempts;
		Date otpCreatedTime;
		Date otpValidatedTime;
		String encodedOtp;
		long timeDiff;
		
	  @Override
	  public OtpData read(ObjectDataInput in) throws IOException {
	    return OtpData.builder()
	        .msisdn(in.readUTF())
	        .otpStatus(in.readUTF())
	        .otpCreatedFreq(in.readInt())
	        .attempts(in.readInt())
	        .otpCreatedTime(in.readObject())
	        .otpValidatedTime(in.readObject())
	        .encodedOtp(in.readUTF())
	        .timeDiff(in.readInt())
	        .build();
	  }

	  @Override
	  public int getTypeId() {
	    return 1;
	  }
	}
