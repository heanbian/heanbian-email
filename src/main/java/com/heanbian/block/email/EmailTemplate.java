package com.heanbian.block.email;

import static java.util.Objects.requireNonNull;
import static jakarta.mail.Message.RecipientType.BCC;
import static jakarta.mail.Message.RecipientType.CC;
import static jakarta.mail.Message.RecipientType.TO;
import static jakarta.mail.internet.MimeUtility.encodeText;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.activation.URLDataSource;
import jakarta.mail.Authenticator;
import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

/**
 * 邮件发送模板类
 * 
 * @author Heanbian
 */
public class EmailTemplate {

	/**
	 * 默认正则表达式
	 */
	private static final String DEFAULT_EMAIL_REGEX = "\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$";
	private Session session;
	private String regex = DEFAULT_EMAIL_REGEX;
	private EmailConfig config;
	private EmailMessage message;

	public EmailTemplate(EmailConfig config) {
		this.session = initSession(config);
		this.session.setDebug(config.isDebug());
		this.config = config;
	}

	public EmailTemplate(EmailConfig config, EmailMessage message) {
		this(config);
		this.message = message;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	/**
	 * 发送邮件
	 * 
	 * @return MimeMessage
	 */
	public MimeMessage send() {
		return send(this.message);
	}

	/**
	 * 发送邮件
	 * 
	 * @param message
	 * @return MimeMessage
	 */
	public MimeMessage send(EmailMessage message) {
		try {
			return send0(message);
		} catch (Exception e) {
			throw new EmailException(e.getMessage(), e);
		}
	}

	private MimeMessage send0(EmailMessage message)
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

	private final Session initSession(EmailConfig config) {
		requireNonNull(config, "config must not be null");
		Properties p = new Properties();
		p.put("mail.smtp.host", config.getHost());
		p.put("mail.smtp.auth", "true");
		p.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		p.put("mail.smtp.socketFactory.fallback", "false");
		p.put("mail.smtp.port", config.getPort());

		return Session.getDefaultInstance(p, new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(config.getUsername(), config.getPassword());
			}
		});
	}

}