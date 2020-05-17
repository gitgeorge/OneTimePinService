package com.otp.request;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class ValidateRequest {

	@NotNull(message = "accountNumber cannot be missing or empty")
	private String accountNumber;

	@NotNull(message = "amount cannot be missing or empty")
	private double amount;

	@NotNull(message = "otp cannot be missing or empty")
	private String otp;
}
