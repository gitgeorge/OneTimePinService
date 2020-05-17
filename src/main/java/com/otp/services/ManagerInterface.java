package com.otp.services;

import com.otp.request.Request;
import com.otp.request.ValidateRequest;
import org.springframework.stereotype.Service;

import com.otp.entity.Otp;
import com.otp.response.Response;

@Service
public interface ManagerInterface {

	Response generateOtp(Request request);

	Response validateOtp(ValidateRequest request);

	Otp saveOtp(Otp manager);
	
}
