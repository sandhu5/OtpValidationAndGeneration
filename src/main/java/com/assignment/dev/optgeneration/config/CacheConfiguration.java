package com.assignment.dev.optgeneration.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.assignment.dev.optgeneration.beans.OtpData;
import com.assignment.dev.optgeneration.util.OtpDataSerializer;
import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

@Configuration
@Component
public class CacheConfiguration {

	public static final String OTPDATA = "otpData";
	private final HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(createConfig());

	public Config createConfig() {
		Config config = new Config();
		config.addMapConfig(mapConfig());
		config.getSerializationConfig().addSerializerConfig(serializerConfig());
		return config;
	}
	
	private SerializerConfig serializerConfig() {
		return new SerializerConfig()
				.setImplementation(new OtpDataSerializer()).setTypeClass(
						OtpData.class);
	}

	private MapConfig mapConfig() {
		MapConfig mapConfig = new MapConfig(OTPDATA);
		mapConfig.setTimeToLiveSeconds(3600);
		return mapConfig;
	}

	public OtpData put(String number, OtpData otpData) {
		IMap<String, OtpData> map = hazelcastInstance.getMap(OTPDATA);
		return map.put(number, otpData);
	}

	public OtpData get(String key) {
		IMap<String, OtpData> map = hazelcastInstance.getMap(OTPDATA);
		return map.get(key);
	}

	public void remove(String key) {
		IMap<String, OtpData> map = hazelcastInstance.getMap(OTPDATA);
	    map.remove(key);
	}
}