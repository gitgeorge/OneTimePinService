package com.otp.sms;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import com.otp.exceptions.ApiRequestException;
import com.otp.props.Properties;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Messaging implements MessagingInteface {

	@Autowired
	private Properties properties;

	Logger log = LoggerFactory.getLogger(Messaging.class);

	@Override
	public String processRequest(String msisdn, String message, long messageId) {

		log.info("Received an otp to be sent out ");
		
		List<Object> requestParams = new ArrayList<Object>();

		HashMap<String, Object> details = new HashMap<String, Object>();
		details.put("message", message);
		details.put("destination", msisdn);
		details.put("responseID", messageId);

		requestParams.add(details);
		HashMap<String, Object> response = sendSmsRequest(requestParams);

		return response.get("messageId").toString();
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public HashMap<String, Object> sendSmsRequest(List<Object> details) {
		
		HashMap<String, Object> responseMap = new HashMap<String, Object>();

		try {
			
			HostnameVerifier hv = new HostnameVerifier() {
				@Override
				public boolean verify(String arg0, SSLSession arg1) {
					return true;
				}
			};

			HttpsURLConnection.setDefaultHostnameVerifier(hv);

			XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();

			config.setServerURL(new URL(properties.getSmsWebService()));
			config.setEnabledForExtensions(true);

			XmlRpcClient client = new XmlRpcClient();

			client.setTransportFactory(new XmlRpcCommonsTransportFactory(client));
			client.setConfig(config);
			client.setConfig(config);

			log.info("Initiating a call to the messaging webservice");

			log.info("Message details " + details.toString());

			log.info("Sms webservice endpoint " + properties.getSmsWebService());

			Object result = client.execute(properties.getSmsMethod(), details);

			responseMap = (HashMap<String, Object>) result;

		} catch (XmlRpcException | MalformedURLException e) {
			
			log.error("Exception caught " + e.getMessage());

			throw new ApiRequestException("We could not send otp sms notification");

		}

		log.info("Response from the messaging webservice " + responseMap.toString());

		return responseMap;
	}

}
