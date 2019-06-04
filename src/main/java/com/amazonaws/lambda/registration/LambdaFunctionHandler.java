package com.amazonaws.lambda.registration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import com.amazonaws.lambda.registration.model.User;
import com.amazonaws.lambda.registration.service.EmailService;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LambdaFunctionHandler implements RequestStreamHandler {

	@Override
	public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		User user = objectMapper.readValue(getJSONfromInputStream(inputStream), User.class);

		String responseText = "Email sent!";

		context.getLogger().log("Input is " + user);

		try {
			new EmailService().registrationComplete(user);
		} catch (AddressException e) {
			responseText = "Error Occurred while sending Mail!";
		} catch (MessagingException e) {
			responseText = "Error Occurred while sending Mail!";
		}
		context.getLogger().log("Output is " + responseText);

	}

	private String getJSONfromInputStream(InputStream input) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		StringBuilder json = new StringBuilder("");
		String line;
		while ((line = reader.readLine()) != null) {
			json.append(line);
		}
		return json.toString();
	}
}
