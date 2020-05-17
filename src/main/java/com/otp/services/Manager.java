package com.otp.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import com.otp.props.Properties;
import com.otp.request.Request;
import com.otp.request.ValidateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.otp.dao.DataAccess;
import com.otp.entity.Otp;
import com.otp.response.Response;
import com.otp.security.Encryption;
import com.otp.sms.Messaging;

import net.minidev.json.JSONObject;

@org.springframework.stereotype.Service
public class Manager implements ManagerInterface {

	@Autowired
	private Encryption encryption;

	@Autowired
	private Messaging messaging;

	@Autowired
	private DataAccess dao;

	@Autowired
	private Properties properties;

	@Autowired
	private Cache cache;

	private JSONObject json = new JSONObject();

	Logger log = LoggerFactory.getLogger(Manager.class);

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");

	String message;

	@Override
	public Otp saveOtp(Otp request) {

		log.info("Data to be persisted " + request.toString());

		return dao.save(request);

	}

	@Override
	public Response generateOtp(Request request) {

		Otp dataPersisted = saveOtp(requestEntity(request));

		String key = request.getAccountNumber() + request.getAmount();

		cache.cacheData(key, dataPersisted);

		String messageId = messaging.processRequest(dataPersisted.getMsisdn(), dataPersisted.getOtp(),
				dataPersisted.getOtpId());

		dataPersisted.setMessageId(messageId);

		updateOtpDetails(dataPersisted,encryption);

		dataPersisted.setOtp(encryption.decrypt(dataPersisted.getOtp()));

		json.put("Data", dataPersisted);

		message = "Successfully generated and sent the otp ";

		return new Response(properties.getSuccessStatus(), json, message);

	}

	@Override
	public Response validateOtp(ValidateRequest request) {

		log.info("Validating otp request: " + request.toString());

		try {

			String key = request.getAccountNumber() + request.getAmount();

			log.info("Retrieving the opt using the key: " + key);

			Otp cachedData = cache.retrieveFromCache(key);

			if (validateOtpData(cachedData)) {

				log.info("Data already cleared from the cache: ");

				// if we are unable to fetch from cache revert back and fetch from database
				Otp databaseData = dao.fetchOtpByAccount(request.getAccountNumber(), request.getAmount());

				if (validateOtpData(databaseData)) {

					message = "Invalid key used to validate otp";

					return new Response(properties.getFailedStatus(), json, message);

				}

				log.info("Checking for the record from the db ");

				String decryptedOtp = encryption.decrypt(databaseData.getOtp());

				databaseData.setOtp(decryptedOtp);

				return otpCheck(request, databaseData, properties, cache,encryption);

			} else {

				return otpCheck(request, cachedData, properties, cache,encryption);

			}

		} catch (ExecutionException e) {

			message = e.getMessage();

			e.printStackTrace();

			log.error("Exception caught : " + message);

			return new Response(properties.getFailedStatus(), json, message);

		}
	}

	/**
	 * 
	 * @param entity
	 */
	public void updateOtpDetails(Otp entity, Encryption encryption) {

		String encryptedOtp = encryption.encrypt(entity.getOtp());

		entity.setOtp(encryptedOtp);

		dao.save(entity);

	}

	/**
	 * 
	 * @param otp
	 * @return
	 */
	public Boolean validateOtpData(Otp otp) {

		if (otp == null || otp.getAccountNumber() == null
				|| otp.getAccountNumber().isEmpty() && otp.getAmount() == null && otp.getOtp() == null
				|| otp.getOtp().isEmpty()) {

			return true;

		} else {

			return false;

		}

	}

	/**
	 * 
	 * @return
	 */
	public String randNumberGenerator(int min, int max) {

		Random random = new Random();

		int otp = min + random.nextInt(max);

		log.info("Generating otp: " + otp);

		return String.valueOf(otp);

	}

	/**
	 *
	 * @param request
	 * @param properties
	 * @return
	 */
	public Boolean hasOtpExpired(Otp request, Properties properties) {

		try {

			Date dateCreated = sdf.parse(request.getDateCreated());

			Date systemDate = new Date();

			log.info("Otp generation date and time " + dateCreated);

			log.info("System date and time " + systemDate);

			long diff = systemDate.getTime() - dateCreated.getTime();

			long diffMinutes = diff / (60 * 1000) % 60;

			log.info("Difference between time of generation and request time " + (int) diffMinutes);

			log.info("Configured cache time " + properties.getExpireMins());

			if ((int) diffMinutes > properties.getExpireMins()) {

				log.info("Otp has expired ");

				return true;

			}
		} catch (ParseException e) {

			message = "Exception caught : " + e.getMessage();

			log.error(message);

			e.printStackTrace();

			return false;

		}

		return false;

	}

	/**
	 * 
	 * @param request
	 * @param otp
	 * @param properties
	 * @param cache
	 * @param encryption
	 * @return
	 */
	public Response otpCheck(ValidateRequest request, Otp otp, Properties properties, Cache cache,
			Encryption encryption) {

		try {

			JSONObject json = new JSONObject();

			json.put("Data", otp);

			if (otp.getProcessed()) {

				message = "Otp has already been used ";

				log.info(message);

				return new Response(properties.getFailedStatus(), json, message);

			}

			if (hasOtpExpired(otp, properties)) {

				message = "Expired otp ";

				log.info(message);

				return new Response(properties.getExpiredStatus(), json, message);

			}

			if (request.getOtp().equalsIgnoreCase(otp.getOtp())) {

				message = "Successfully validated otp  ";

				log.info(message);

				cache.updateCache(request);

				updateOtpDetails(otp, encryption);

				return new Response(properties.getSuccessStatus(), json, message);

			}

			message = "Invalid otp supplied ";

			log.info(message);

			return new Response(properties.getFailedStatus(), json, message);

		} catch (ExecutionException e) {

			message = e.getMessage();

			e.printStackTrace();

			log.error("Exception caught: " + message);

			return new Response(properties.getFailedStatus(), json, message);

		}
	}

	/**
	 *
	 * @param request
	 * @return
	 */
	private Otp requestEntity(Request request) {

		String dateCreated = sdf.format(new Date());

		int min = properties.getMinRange();

		int max = properties.getMaxRange();

		String otp = randNumberGenerator(min, max);

		return new Otp(otp, request.getAccountNumber(), request.getMsisdn(),
				request.getAmount(), request.getChannel(),false, dateCreated);

	}

}
