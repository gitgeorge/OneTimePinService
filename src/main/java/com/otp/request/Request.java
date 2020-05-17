package com.otp.request;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class Request {

	@NotNull(message = "accountNumber cannot be missing or empty")
	private String accountNumber;

	@NotNull(message = "msisdn cannot be missing or empty")
	private String msisdn;

	@NotNull(message = "amount cannot be missing or empty")
	private double amount;

	@NotNull(message = "channel cannot be missing or empty")
	private String channel;

}
