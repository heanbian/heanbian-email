package com.heanbian.email;

import java.io.File;
import java.net.URL;
import java.util.List;
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
public class HEmailTemplate {

	/**
	 * 默认正则表达式
	 */
	private static final String DEFAULT_EMAIL_REGEX = "^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$";
	private MimeMessage mimeMessage;
	private Session session;
	private String _email_regex;
	private HEmailConfig config;

	/**
	 * @param config 邮件配置
	 */
	public HEmailTemplate(HEmailConfig config) {
		this.config = config;
		this._email_regex = DEFAULT_EMAIL_REGEX;
	}

	/**
	 * 发送邮件方法，使用自定义正则表达式
	 * 
	 * @param message      消息体
	 * @param _email_regex 验证邮件正则表达式
	 * @return MimeMessage 返回结果
	 * @throws Exception 异常
	 */
	public MimeMessage send(HEmailMessage message, String _email_regex) throws Exception {
		if (_email_regex != null) {
			this._email_regex = _email_regex;
		}
		return send(message);
	}

	/**
	 * 发送邮件方法，使用默认正则表达式{@link #DEFAULT_EMAIL_REGEX}
	 * 
	 * @param subject   主题
	 * @param toAddress 接收人
	 * @param content   正文内容
	 * @return MimeMessage 返回结果
	 * @throws Exception 异常
	 */
	public MimeMessage send(String subject, List<String> toAddress, String content) throws Exception {
		return send(new HEmailMessage(subject, toAddress, content));
	}

	/**
	 * 发送邮件方法，使用默认正则表达式{@link #DEFAULT_EMAIL_REGEX}
	 * 
	 * @param subject   主题
	 * @param toAddress 接收人
	 * @param content   正文内容
	 * @return MimeMessage 返回结果
	 * @throws Exception 异常
	 */
	public MimeMessage send(String subject, String toAddress, String content) throws Exception {
		return send(new HEmailMessage(subject, toAddress, content));
	}

	/**
	 * 发送邮件方法，使用默认正则表达式{@link #DEFAULT_EMAIL_REGEX}
	 * 
	 * @param message 消息体
	 * @return MimeMessage 返回结果
	 * @throws Exception 异常
	 */
	public MimeMessage send(HEmailMessage message) throws Exception {
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
			throw new HEmailException("接收人邮件地址至少一个");
		}

		for (String to : message.getToAddress()) {
			if (!to.matches(this._email_regex)) {
				throw new HEmailException("接收人邮件地址不合法：" + to);
			}
			mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
		}

		if (message.getCcAddress() != null && !message.getCcAddress().isEmpty()) {
			for (String cc : message.getCcAddress()) {
				if (!cc.matches(this._email_regex)) {
					throw new HEmailException("抄送人邮件地址不合法：" + cc);
				}
				mimeMessage.addRecipient(Message.RecipientType.CC, new InternetAddress(cc));
			}
		}

		if (message.getBccAddress() != null && !message.getBccAddress().isEmpty()) {
			for (String bcc : message.getCcAddress()) {
				if (!bcc.matches(this._email_regex)) {
					throw new HEmailException("密送人邮件地址不合法：" + bcc);
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
	 * @param config 邮件配置
	 * @return Mail Session
	 */
	private static Session initSession(HEmailConfig config) {
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
	public HEmailConfig getConfig() {
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
	 * @return this
	 */
	public HEmailTemplate setMimeMessage(MimeMessage mimeMessage) {
		this.mimeMessage = mimeMessage;
		return this;
	}

	/**
	 * @return {@link #session}
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * @param session {@link #session}
	 * @return this
	 */
	public HEmailTemplate setSession(Session session) {
		this.session = session;
		return this;
	}

	/**
	 * @return {@link #_email_regex}
	 */
	public String get_email_regex() {
		return _email_regex;
	}

}