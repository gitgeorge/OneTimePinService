package com.otp.response;

import org.springframework.stereotype.Service;

import lombok.Data;
import net.minidev.json.JSONObject;

@Service
@Data
public class Response {

	/**
	 * Otp status
	 */
	private String status;

	/**
	 * Otp data
	 */
	private JSONObject data;

	/**
	 * Otp status description
	 */
	private String message;

	public Response() {

	}

	public Response(String status, JSONObject data, String message) {
		super();
		this.status = status;
		this.data = data;
		this.message = message;
	}

}
