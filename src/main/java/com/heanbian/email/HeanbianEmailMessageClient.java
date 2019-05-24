package com.heanbian.email;

import java.io.File;
import java.net.URL;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.URLDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
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
 * 邮件发送类
 * 
 * @author heanbian@heanbian.com
 * @since 1.0
 * @version 1.0
 */
public class HeanbianEmailMessageClient {

	private static final String DEFAULT_EMAIL_REGEX = "^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$";
	private MimeMessage mimeMessage;
	private Session session;
	private String _email_regex;
	private HeanbianEmailConfig config;

	/**
	 * {@link #com.heanbian.email.HeanbianEmailConfig}
	 */
	public HeanbianEmailMessageClient(HeanbianEmailConfig config) {
		this.config = config;
		this._email_regex = DEFAULT_EMAIL_REGEX;
	}

	/**
	 * 发送邮件
	 * 
	 * @param message      {@link #com.heanbian.email.HeanbianEmailMessage}
	 * @param _email_regex 验证邮件正则表达式
	 * @return MimeMessage
	 * @throws Exception 异常
	 */
	public MimeMessage send(HeanbianEmailMessage message, String _email_regex) throws Exception {
		if (_email_regex != null) {
			this._email_regex = _email_regex;
		}
		return send(message);
	}

	/**
	 * 发送邮件
	 * 
	 * @param message {@link #com.heanbian.email.HeanbianEmailMessage}
	 * @return MimeMessage
	 * @throws Exception 异常
	 */
	public MimeMessage send(HeanbianEmailMessage message) throws Exception {
		if (session == null) {
			session = initSession(config);
			session.setDebug(config.isDebug());
		}
		mimeMessage = new MimeMessage(session);

		if (config.getFrom() == null) {
			mimeMessage.setFrom(new InternetAddress(config.getUsername(), config.getUsername()));
		} else {
			mimeMessage.setFrom(new InternetAddress(config.getUsername(), config.getFrom()));
		}

		mimeMessage.setSubject(message.getSubject());
		mimeMessage.setText("您的邮箱客户端不支持HTML格式邮件");

		if (message.getToAddress() == null || message.getToAddress().isEmpty()) {
			throw new HeanbianEmailException("接收人邮件地址至少一个");
		}

		for (String to : message.getToAddress()) {
			if (to.matches(this._email_regex)) {
				throw new HeanbianEmailException("接收人邮件地址不合法：" + to);
			}
			mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
		}

		if (message.getCcAddress() != null && !message.getCcAddress().isEmpty()) {
			for (String cc : message.getCcAddress()) {
				if (cc.matches(this._email_regex)) {
					throw new HeanbianEmailException("抄送人邮件地址不合法：" + cc);
				}
				mimeMessage.addRecipient(Message.RecipientType.CC, new InternetAddress(cc));
			}
		}

		if (message.getBccAddress() != null && !message.getBccAddress().isEmpty()) {
			for (String bcc : message.getCcAddress()) {
				if (bcc.matches(this._email_regex)) {
					throw new HeanbianEmailException("密送人邮件地址不合法：" + bcc);
				}
				mimeMessage.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc));
			}
		}

		MimeBodyPart bodyPart = new MimeBodyPart();
		bodyPart.setContent(message.getContent(), "text/html;charset=UTF-8");

		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(bodyPart);

		BodyPart attachmentPart = null;
		DataSource ds = null;

		if (message.getAttachments() != null && !message.getAttachments().isEmpty()) {
			for (String url : message.getAttachments()) {
				attachmentPart = new MimeBodyPart();
				ds = new URLDataSource(new URL(url));

				attachmentPart.setDataHandler(new DataHandler(ds));
				attachmentPart.setFileName(MimeUtility.encodeText(ds.getName()));
				multipart.addBodyPart(attachmentPart);
			}
		}

		if (message.getFiles() != null && !message.getFiles().isEmpty()) {
			for (File file : message.getFiles()) {
				attachmentPart = new MimeBodyPart();
				ds = new FileDataSource(file);

				attachmentPart.setDataHandler(new DataHandler(ds));
				attachmentPart.setFileName(MimeUtility.encodeText(ds.getName()));
				multipart.addBodyPart(attachmentPart);
			}
		}

		mimeMessage.setContent(multipart);
		Transport.send(mimeMessage);
		return mimeMessage;
	}

	/**
	 * 初始化Mail Session
	 * 
	 * @param config {@link #config}
	 * @return Mail Session
	 */
	private static Session initSession(HeanbianEmailConfig config) {
		Properties props = new Properties();
		props.put("mail.smtp.host", config.getHost());
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");
		props.put("mail.smtp.port", config.getPort());

		return Session.getDefaultInstance(props, new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(config.getUsername(), config.getPassword());
			}
		});
	}

	/**
	 * @return {@link #config}
	 */
	public HeanbianEmailConfig getConfig() {
		return config;
	}

	/**
	 * @return {@link #mimeMessage}
	 */
	public MimeMessage getMimeMessage() {
		return mimeMessage;
	}

	/**
	 * @param mimeMessage {@link #mimeMessage}
	 */
	public void setMimeMessage(MimeMessage mimeMessage) {
		this.mimeMessage = mimeMessage;
	}

	/**
	 * @return {@link #session}
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * @param session {@link #session}
	 */
	public void setSession(Session session) {
		this.session = session;
	}

	/**
	 * @return {@link #_email_regex}
	 */
	public String get_email_regex() {
		return _email_regex;
	}

}