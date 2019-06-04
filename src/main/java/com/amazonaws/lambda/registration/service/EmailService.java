package com.amazonaws.lambda.registration.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.io.FileUtils;

import com.amazonaws.lambda.registration.model.User;

public class EmailService {

	private static final String REGISTRATION_MESSAGE = "Registration Flow complete";

	private static final String SENDER_MAIL_ID = "*****************@gmail.com";

	private Authenticator getAuthenticator() {
		return new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(SENDER_MAIL_ID, "password");
			}
		};
	}

	public void registrationComplete(User user) throws AddressException, MessagingException, IOException {

		Properties properties = addSmtpProperties();
		Session session = Session.getInstance(properties, getAuthenticator());

		// sends the e-mail
		Transport.send(createEmailMessage(session, user));

	}

	private Properties addSmtpProperties() throws IOException {
		File file = new File(getClass().getClassLoader().getResource("smtpmail.properties").getFile());
		InputStream input = new FileInputStream(file);
		Properties properties = new Properties();
		properties.load(input);
		return properties;
	}

	private Message createEmailMessage(Session session, User user)
			throws AddressException, MessagingException, IOException {

		Message msg = new MimeMessage(session);

		InternetAddress[] toAddresses = new InternetAddress[1];
		InternetAddress toAddress = new InternetAddress(user.getEmailAddress());
		toAddresses[0] = toAddress;

		msg.setFrom(new InternetAddress(SENDER_MAIL_ID));
		msg.setRecipients(Message.RecipientType.TO, toAddresses);
		msg.setSubject(REGISTRATION_MESSAGE);
		msg.setSentDate(new Date());
		msg.setContent(getDisplayContent(), "text/html");
		return msg;
	}

	private String getDisplayContent() throws IOException {
		File file = new File(getClass().getClassLoader().getResource("template.html").getFile());
		return FileUtils.readFileToString(file, "UTF-8");
	}
}
