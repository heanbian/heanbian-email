package com.heanbian.block.reactive.email;

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
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

/**
 * 邮件发送模板类
 * 
 * @author Heanbian
 * @version 11.0.5
 */
public class EmailTemplate {

	/**
	 * 默认正则表达式
	 */
	private static final String DEFAULT_EMAIL_REGEX = "\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$";
	private Session session;
	private String regex;
	private EmailConfig config;
	private EmailMessage message;

	/**
	 * @param config  邮件配置
	 * @param message 邮件消息
	 */
	public EmailTemplate(EmailConfig config, EmailMessage message) {
		this.config = EmailException.requireNonNull(config, "config must not be null");
		this.message = EmailException.requireNonNull(message, "message must not be null");
		this.regex = DEFAULT_EMAIL_REGEX;
		this.session = initSession(config);
		this.session.setDebug(config.isDebug());
	}

	/**
	 * 发送邮件
	 * 
	 * @return MimeMessage
	 */
	public MimeMessage send() {
		try {
			return send0(this.message);
		} catch (Exception e) {
			throw new EmailException(e.getMessage(), e);
		}
	}

	/**
	 * 发送邮件方法，使用默认正则表达式{@link #DEFAULT_EMAIL_REGEX}
	 * 
	 * @param message 消息体
	 * @return MimeMessage
	 * @throws UnsupportedEncodingException
	 * @throws MessagingException
	 * @throws MalformedURLException
	 * @throws Exception
	 */
	private MimeMessage send0(EmailMessage message)
			throws UnsupportedEncodingException, MessagingException, MalformedURLException {
		MimeMessage mimeMessage = new MimeMessage(session);
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
			mimeMessage.addRecipient(RecipientType.TO, new InternetAddress(to));
		}

		if (message.getCcAddress() != null && !message.getCcAddress().isEmpty()) {
			for (String cc : message.getCcAddress()) {
				if (!cc.matches(this.regex)) {
					throw new EmailException("抄送人邮件地址不合法：" + cc);
				}
				mimeMessage.addRecipient(RecipientType.CC, new InternetAddress(cc));
			}
		}

		if (message.getBccAddress() != null && !message.getBccAddress().isEmpty()) {
			for (String bcc : message.getCcAddress()) {
				if (!bcc.matches(this.regex)) {
					throw new EmailException("密送人邮件地址不合法：" + bcc);
				}
				mimeMessage.addRecipient(RecipientType.BCC, new InternetAddress(bcc));
			}
		}

		MimeBodyPart mimeBodyPart = new MimeBodyPart();
		mimeBodyPart.setContent(message.getContent(), "text/html;charset=UTF-8");

		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(mimeBodyPart);

		BodyPart bodyPart = null;
		DataSource ds = null;

		if (message.getAttachments() != null && !message.getAttachments().isEmpty()) {
			for (String url : message.getAttachments()) {
				bodyPart = new MimeBodyPart();
				ds = new URLDataSource(new URL(url));
				bodyPart.setDataHandler(new DataHandler(ds));
				bodyPart.setFileName(MimeUtility.encodeText(ds.getName()));
				multipart.addBodyPart(bodyPart);
			}
		}

		if (message.getFiles() != null && !message.getFiles().isEmpty()) {
			for (File file : message.getFiles()) {
				bodyPart = new MimeBodyPart();
				ds = new FileDataSource(file);
				bodyPart.setDataHandler(new DataHandler(ds));
				bodyPart.setFileName(MimeUtility.encodeText(ds.getName()));
				multipart.addBodyPart(bodyPart);
			}
		}

		mimeMessage.setContent(multipart);
		Transport.send(mimeMessage);
		return mimeMessage;
	}

	/**
	 * Init Session
	 * 
	 * @param config
	 * @return Session
	 */
	private final Session initSession(EmailConfig config) {
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