package com.otp.sms;

import java.util.HashMap;
import java.util.List;

public interface MessagingInteface {

	String processRequest(String msisdn, String message, long messageId);

	HashMap<String, Object> sendSmsRequest(List<Object> details);

}
