package com.otp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;


@Entity
@Data
@Table(name = "otp_manager")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Otp {

	@Id
	@Column()
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long otpId;

	@Column()
	private String otp;

	@Column()
	@NotNull(message = "accountNumber cannot be missing or empty")
	private String accountNumber;

	@Column()
	@NotNull(message = "msisdn cannot be missing or empty")
	private String msisdn;

	@Column()
	@NotNull(message = "amount cannot be missing or empty")
	private Double amount;

	@Column()
	@NotNull(message = "channel cannot be missing or empty")
	private String channel;

	@Column()
	private String messageId;
	
	@Column(columnDefinition = "boolean default false")
	private Boolean  processed;

	@Column()
	@NotNull(message = "dateCreated cannot be missing or empty")
	private String dateCreated;
	
	public Otp() {
		
	}

	public Otp( String otp,
			@NotNull(message = "accountNumber cannot be missing or empty") String accountNumber,
			@NotNull(message = "msisdn cannot be missing or empty") String msisdn,
			@NotNull(message = "amount cannot be missing or empty") Double amount,
			@NotNull(message = "channel cannot be missing or empty") String channel,Boolean processed,
			@NotNull(message = "dateCreated cannot be missing or empty") String dateCreated) {
	
		this.otp = otp;
		this.accountNumber = accountNumber;
		this.msisdn = msisdn;
		this.amount = amount;
		this.channel = channel;
		this.processed = processed;
		this.dateCreated = dateCreated;
	}
	
	

}
