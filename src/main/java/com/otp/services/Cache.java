package com.otp.services;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.otp.props.Properties;
import com.otp.request.ValidateRequest;
import org.springframework.stereotype.Service;


import com.otp.entity.Otp;

@Service
public class Cache {

	private final LoadingCache<String, Otp> otpCache;

	public Cache(Properties properties) {
		otpCache = CacheBuilder.newBuilder().expireAfterWrite(properties.getExpireMins(), TimeUnit.MINUTES)
				.build(new CacheLoader<String, Otp>() {
					@Override
					public Otp load(String key) {
						return new Otp();
					}
				});
	}

	/**
	 * 
	 * @param key
	 * @param data
	 */
	public void cacheData(String key, Otp data) {
		otpCache.put(key, data);
	}

	/**
	 * 
	 * @param request
	 * @throws ExecutionException
	 */
	public void updateCache(ValidateRequest request) throws ExecutionException {

		String key = request.getAccountNumber() + request.getAmount();

		Otp cachedData = otpCache.get(key);
		cachedData.setProcessed(true);

		otpCache.put(key, cachedData);

	}

	public Otp retrieveFromCache(String key) throws ExecutionException {
		return otpCache.get(key);
	}

}
