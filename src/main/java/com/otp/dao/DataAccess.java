package com.otp.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.otp.entity.Otp;

@Repository
public interface DataAccess extends JpaRepository<Otp, Long> {

	@Query(value = "select * from otp_manager where account_number = ?1"
			+ " and amount = ?2 order by 1 desc limit 1", nativeQuery = true)
	Otp fetchOtpByAccount(String accountNumber, double amount);

}
