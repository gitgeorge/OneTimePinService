package com.otp.app;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.otp.request.Request;
import com.otp.request.ValidateRequest;
import com.otp.services.Manager;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.otp.response.Response;

import net.minidev.json.JSONObject;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OneTimePinServiceApplication.class)
@AutoConfigureMockMvc
public class ControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private Manager maager;

	Logger log = LoggerFactory.getLogger(Manager.class);

	private JSONObject json = new JSONObject();

	@Test
	public void testGenerateOtp() throws Exception {

		Request req = new Request();
		req.setAccountNumber("00000001");
		req.setAmount(1000.0);
		req.setChannel("USSD");
		req.setMsisdn("0713369470");

		json.put("Data", req);

		Response resp = new Response();
		resp.setData(json);
		resp.setMessage("success");
		resp.setStatus("1");

		String jsonReq = this.mapToJson(req);

		String uri = "/otpManager/generateOtp";

		Mockito.when(maager.generateOtp(Mockito.any(Request.class))).thenReturn(resp);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post(uri)
				.accept(org.springframework.http.MediaType.APPLICATION_JSON).content(jsonReq)
				.contentType(org.springframework.http.MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		MockHttpServletResponse response = result.getResponse();
		String outPutJson = response.getContentAsString();

		log.info("results:  " + outPutJson);

		String status = stringToJson(outPutJson).getStatus();

		log.info("status --:  " + status);

		assertEquals(status, resp.getStatus());

	}

	@Test
	public void testValidateOtp() throws Exception {

		ValidateRequest req = new ValidateRequest();
		req.setAccountNumber("00000001");
		req.setAmount(1000.0);
		req.setOtp("1234");

		json.put("Data", req);

		Response resp = new Response();
		resp.setData(json);
		resp.setMessage("success");
		resp.setStatus("1");

		String jsonReq = this.mapToJson(req);

		String uri = "/otpManager/vaidateOtp";

		Mockito.when(maager.validateOtp(Mockito.any(ValidateRequest.class))).thenReturn(resp);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(uri)
				.accept(org.springframework.http.MediaType.APPLICATION_JSON).content(jsonReq)
				.contentType(org.springframework.http.MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		MockHttpServletResponse response = result.getResponse();
		String outPutJson = response.getContentAsString();

		log.info("results:  " + outPutJson);

		String status = stringToJson(outPutJson).getStatus();

		log.info("status --:  " + status);

		assertEquals(status, resp.getStatus());

	}

	private String mapToJson(Object obj) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(obj);
	}

	private Response stringToJson(String outPutJson) throws JsonMappingException, JsonProcessingException {
		return new ObjectMapper().readValue(outPutJson, Response.class);
	}
}
