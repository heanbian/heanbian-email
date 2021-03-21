package com.heanbian.block.email;

import static java.util.Objects.requireNonNull;
import static javax.mail.Message.RecipientType.BCC;
import static javax.mail.Message.RecipientType.CC;
import static javax.mail.Message.RecipientType.TO;
import static javax.mail.internet.MimeUtility.encodeText;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.URLDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * 邮件发送模板类
 * 
 */
public class EmailTemplate {

	private static final String DEFAULT_EMAIL_REGEX = "\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$";
	private String regex;
	private Session session;
	private EmailConfig config;
	private EmailMessage message;

	public EmailTemplate() {
		this.regex = DEFAULT_EMAIL_REGEX;
	}

	public EmailTemplate(EmailConfig config) {
		this(config, null, DEFAULT_EMAIL_REGEX);
	}

	public EmailTemplate(EmailConfig config, EmailMessage message) {
		this(config, message, DEFAULT_EMAIL_REGEX);
	}

	public EmailTemplate(EmailConfig config, EmailMessage message, String regex) {
		this.session = putSession(config);
		this.session.setDebug(config.isDebug());
		this.config = config;
		this.message = message;
		this.regex = regex;
	}

	public EmailTemplate setConfig(EmailConfig config) {
		this.session = putSession(config);
		this.session.setDebug(config.isDebug());
		this.config = config;
		return this;
	}

	public EmailTemplate setRegex(String regex) {
		this.regex = regex;
		return this;
	}

	public EmailTemplate setMessage(EmailMessage message) {
		this.message = message;
		return this;
	}

	public MimeMessage send() {
		return send(this.message);
	}

	public MimeMessage send(EmailMessage message) {
		try {
			return sendMimeMessage(message);
		} catch (Exception e) {
			throw new EmailException(e.getMessage(), e);
		}
	}

	private MimeMessage sendMimeMessage(EmailMessage message)
			throws UnsupportedEncodingException, MessagingException, MalformedURLException {
		requireNonNull(message, "message must not be null");
		if (this.regex == null) {
			this.regex = DEFAULT_EMAIL_REGEX;
		}

		MimeMessage mimeMessage = new MimeMessage(this.session);
		mimeMessage.setFrom(new InternetAddress(config.getUsername(), config.getFrom()));
		mimeMessage.setSubject(message.getSubject());
		mimeMessage.setText("您的邮箱客户端不支持HTML格式邮件");

		if (message.getToAddress() == null || message.getToAddress().isEmpty()) {
			throw new EmailException("接收人邮件地址至少一个");
		}

		for (String to : message.getToAddress()) {
			if (!to.matches(this.regex)) {
				throw new EmailException("接收人邮件地址不合法：" + to);
			}
			mimeMessage.addRecipient(TO, new InternetAddress(to));
		}

		if (message.getCcAddress() != null && !message.getCcAddress().isEmpty()) {
			for (String cc : message.getCcAddress()) {
				if (!cc.matches(this.regex)) {
					throw new EmailException("抄送人邮件地址不合法：" + cc);
				}
				mimeMessage.addRecipient(CC, new InternetAddress(cc));
			}
		}

		if (message.getBccAddress() != null && !message.getBccAddress().isEmpty()) {
			for (String bcc : message.getCcAddress()) {
				if (!bcc.matches(this.regex)) {
					throw new EmailException("密送人邮件地址不合法：" + bcc);
				}
				mimeMessage.addRecipient(BCC, new InternetAddress(bcc));
			}
		}

		MimeBodyPart mimeBodyPart = new MimeBodyPart();
		mimeBodyPart.setContent(message.getContent(), "text/html;charset=UTF-8");

		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(mimeBodyPart);

		if (message.getAttachments() != null && !message.getAttachments().isEmpty()) {
			BodyPart bodyPart = null;
			DataSource ds = null;
			for (String url : message.getAttachments()) {
				bodyPart = new MimeBodyPart();
				ds = new URLDataSource(new URL(url));
				bodyPart.setDataHandler(new DataHandler(ds));
				bodyPart.setFileName(encodeText(ds.getName()));
				multipart.addBodyPart(bodyPart);
			}
		}

		if (message.getFiles() != null && !message.getFiles().isEmpty()) {
			BodyPart bodyPart = null;
			DataSource ds = null;
			for (File file : message.getFiles()) {
				bodyPart = new MimeBodyPart();
				ds = new FileDataSource(file);
				bodyPart.setDataHandler(new DataHandler(ds));
				bodyPart.setFileName(encodeText(ds.getName()));
				multipart.addBodyPart(bodyPart);
			}
		}

		mimeMessage.setContent(multipart);
		Transport.send(mimeMessage);
		return mimeMessage;
	}

	private static Session putSession(EmailConfig c) {
		Properties p = new Properties();
		p.put("mail.smtp.host", c.getHost());
		p.put("mail.smtp.port", c.getPort());
		p.put("mail.smtp.auth", "true");
		p.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		p.put("mail.smtp.socketFactory.fallback", "false");

		return Session.getDefaultInstance(p, new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(c.getUsername(), c.getPassword());
			}
		});
	}
}