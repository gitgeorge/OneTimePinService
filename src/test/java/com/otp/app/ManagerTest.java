package com.otp.app;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.ParseException;

import com.otp.props.Properties;
import com.otp.request.ValidateRequest;
import com.otp.services.Cache;
import com.otp.services.Manager;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


import com.otp.entity.Otp;
import com.otp.response.Response;
import com.otp.security.Encryption;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OneTimePinServiceApplication.class)
public class ManagerTest {

	@Autowired
	Properties properties;

	@Autowired
	Cache cache;

	@Autowired
	private Encryption encrypt;

	Logger log = LoggerFactory.getLogger(Manager.class);

	@Test
	public void testValidateOtpData() {

		Manager manager = new Manager();

		Otp otp = new Otp();
		otp.setAccountNumber("00000001");
		otp.setAmount(1000.0);
		otp.setChannel("USSD");
		otp.setMsisdn("0713369470");
		otp.setDateCreated("2020-24-04");
		otp.setMessageId("12345");
		otp.setOtp("1234");

		log.info("Otp  :  " + otp);

		Boolean state = manager.validateOtpData(otp);

		log.info("State :  " + state);

		assertFalse(state);

	}

	@Test
	public void testRandNumberGenerator() {

		Manager manager = new Manager();

		String otp = manager.randNumberGenerator(1, 300);

		log.info("Otp generated :  " + otp);

		assertNotNull(otp);

	}

	@Test
	public void testHasOtpExpired() throws ParseException {

		Otp otp = new Otp();

		otp.setAccountNumber("00000001");
		otp.setAmount(1000.0);
		otp.setChannel("USSD");
		otp.setMsisdn("0713369470");
		otp.setDateCreated("2020-04-23 12:10");
		otp.setMessageId("12345");
		otp.setOtp("1234");

		Manager manager = new Manager();

		Boolean status = manager.hasOtpExpired(otp, properties);

		log.info("HasOtpExpired : " + status);

		assertTrue(status);

	}

	@Test
	public void testOtpCheck() {

		Otp otp = new Otp();

		otp.setAccountNumber("00000001");
		otp.setAmount(1000.0);
		otp.setChannel("USSD");
		otp.setMsisdn("0713369470");
		otp.setDateCreated("2020-04-23 12:10");
		otp.setMessageId("12345");
		otp.setOtp("1234");
		otp.setProcessed(false);
		otp.setOtpId(123);

		ValidateRequest request = new ValidateRequest();

		request.setAccountNumber("00000001");
		request.setAmount(1000.0);
		request.setOtp("1234");

		Manager manager = new Manager();

		Response response = manager.otpCheck(request, otp, properties, cache,encrypt);

		log.info("Response : " + response);

		assertEquals("2", response.getStatus());

	}

}
