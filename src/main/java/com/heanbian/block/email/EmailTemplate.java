package com.heanbian.block.email;

import static jakarta.mail.Message.RecipientType.BCC;
import static jakarta.mail.Message.RecipientType.CC;
import static jakarta.mail.Message.RecipientType.TO;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.activation.URLDataSource;
import jakarta.mail.Authenticator;
import jakarta.mail.Message.RecipientType;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.internet.MimeUtility;

/**
 * 邮件发送模板类
 */
public class EmailTemplate {

	/**
	 * 保留自定义正则能力，但默认不再依赖它做唯一校验。
	 */
	private String regex;

	private Session session;
	private EmailConfig config;
	private EmailMessage message;

	public EmailTemplate() {
	}

	public EmailTemplate(EmailConfig config) {
		this(config, null);
	}

	public EmailTemplate(EmailConfig config, EmailMessage message) {
		this(config, message, null);
	}

	public EmailTemplate(EmailConfig config, EmailMessage message, String regex) {
		this.config = config;
		this.message = message;
		this.regex = normalizeNullable(regex);
	}

	public EmailTemplate setSession(Session session) {
		this.session = session;
		return this;
	}

	public EmailTemplate setConfig(EmailConfig config) {
		this.config = config;
		return this;
	}

	public EmailTemplate setRegex(String regex) {
		this.regex = normalizeNullable(regex);
		return this;
	}

	public EmailTemplate setMessage(EmailMessage message) {
		this.message = message;
		return this;
	}

	public MimeMessage send() {
		if (this.message == null) {
			throw new EmailException("EmailMessage 不能为空");
		}
		return send(this.message);
	}

	public MimeMessage send(EmailMessage message) {
		try {
			return sendMimeMessage(message);
		} catch (MessagingException | IOException e) {
			throw new EmailException("发送邮件失败", e);
		}
	}

	private MimeMessage sendMimeMessage(EmailMessage message) throws MessagingException, IOException {
		Objects.requireNonNull(message, "EmailMessage 不能为空");
		Objects.requireNonNull(this.config, "EmailConfig 不能为空");

		validateMessage(message);

		Session currentSession = this.session;
		if (currentSession == null) {
			currentSession = createSession(this.config);
			currentSession.setDebug(this.config.debug());
			this.session = currentSession;
		}

		MimeMessage mimeMessage = new MimeMessage(currentSession);
		mimeMessage.setFrom(createFromAddress(this.config));
		addRecipients(mimeMessage, TO, message.getToAddress(), "接收人");
		addRecipients(mimeMessage, CC, message.getCcAddress(), "抄送人");
		addRecipients(mimeMessage, BCC, message.getBccAddress(), "密送人");

		mimeMessage.setSubject(defaultString(message.getSubject()), StandardCharsets.UTF_8.name());
		mimeMessage.setSentDate(new Date());
		mimeMessage.setContent(buildMultipart(message));
		mimeMessage.saveChanges();

		Transport.send(mimeMessage);
		return mimeMessage;
	}

	private void validateMessage(EmailMessage message) {
		if (message.getToAddress().isEmpty()) {
			throw new EmailException("接收人邮件地址至少一个");
		}
		if (isBlank(message.getContent()) && isBlank(message.getTextContent())) {
			throw new EmailException("邮件内容不能为空");
		}
	}

	private Multipart buildMultipart(EmailMessage message) throws MessagingException, IOException {
		MimeMultipart root = new MimeMultipart("mixed");

		MimeBodyPart contentWrapper = new MimeBodyPart();
		MimeMultipart alternative = new MimeMultipart("alternative");

		String htmlContent = normalizeNullable(message.getContent());
		String textContent = normalizeNullable(message.getTextContent());

		if (textContent == null && htmlContent != null) {
			textContent = htmlToPlainText(htmlContent);
		}

		if (textContent != null) {
			MimeBodyPart textPart = new MimeBodyPart();
			textPart.setText(textContent, StandardCharsets.UTF_8.name());
			alternative.addBodyPart(textPart);
		}

		if (htmlContent != null) {
			MimeBodyPart htmlPart = new MimeBodyPart();
			htmlPart.setContent(htmlContent, "text/html; charset=UTF-8");
			alternative.addBodyPart(htmlPart);
		}

		contentWrapper.setContent(alternative);
		root.addBodyPart(contentWrapper);

		addUrlAttachments(root, message.getAttachments());
		addFileAttachments(root, message.getFiles());

		return root;
	}

	private void addRecipients(MimeMessage mimeMessage, RecipientType type, Set<String> addresses, String label)
			throws MessagingException {

		for (String raw : addresses) {
			validateWithCustomRegex(raw, label);

			InternetAddress address = new InternetAddress(raw, true);
			address.validate();
			mimeMessage.addRecipient(type, address);
		}
	}

	private void validateWithCustomRegex(String address, String label) {
		if (regex != null && !address.matches(regex)) {
			throw new EmailException("%s邮件地址不合法：%s", label, address);
		}
	}

	private void addUrlAttachments(Multipart multipart, Set<String> attachments) throws MessagingException, IOException {
		for (String url : attachments) {
			URI uri;
			try {
				uri = URI.create(url);
			} catch (IllegalArgumentException ex) {
				throw new EmailException(ex, "附件 URL 不合法：%s", url);
			}
			if (!uri.isAbsolute()) {
				throw new EmailException("附件 URL 必须是绝对地址：%s", url);
			}

			DataSource dataSource = new URLDataSource(uri.toURL());
			MimeBodyPart bodyPart = new MimeBodyPart();
			bodyPart.setDataHandler(new DataHandler(dataSource));
			bodyPart.setFileName(MimeUtility.encodeText(dataSource.getName(), StandardCharsets.UTF_8.name(), null));
			multipart.addBodyPart(bodyPart);
		}
	}

	private void addFileAttachments(Multipart multipart, Set<File> files) throws MessagingException, IOException {
		for (File file : files) {
			if (!file.exists()) {
				throw new EmailException("附件文件不存在：%s", file.getAbsolutePath());
			}
			if (!file.isFile()) {
				throw new EmailException("附件不是文件：%s", file.getAbsolutePath());
			}
			if (!file.canRead()) {
				throw new EmailException("附件文件不可读：%s", file.getAbsolutePath());
			}

			DataSource dataSource = new FileDataSource(file);
			MimeBodyPart bodyPart = new MimeBodyPart();
			bodyPart.setDataHandler(new DataHandler(dataSource));
			bodyPart.setFileName(MimeUtility.encodeText(dataSource.getName(), StandardCharsets.UTF_8.name(), null));
			multipart.addBodyPart(bodyPart);
		}
	}

	private static InternetAddress createFromAddress(EmailConfig config) throws MessagingException, IOException {
		String address = config.fromAddress();
		String personal = config.fromPersonal();

		InternetAddress fromAddress;
		if (isBlank(personal)) {
			fromAddress = new InternetAddress(address);
		} else {
			fromAddress = new InternetAddress(address, personal, StandardCharsets.UTF_8.name());
		}
		fromAddress.validate();
		return fromAddress;
	}

	private static Session createSession(EmailConfig config) {
		Properties properties = new Properties();
		properties.put("mail.transport.protocol", "smtp");
		properties.put("mail.smtp.host", config.host());
		properties.put("mail.smtp.port", Integer.toString(config.port()));
		properties.put("mail.smtp.auth", "true");

		// 兼容常见 SMTP 场景：
		// 465 -> implicit SSL
		// 其他端口 -> STARTTLS
		boolean implicitSsl = config.port() == 465;
		properties.put("mail.smtp.ssl.enable", Boolean.toString(implicitSsl));
		properties.put("mail.smtp.starttls.enable", Boolean.toString(!implicitSsl));

		properties.put("mail.mime.charset", StandardCharsets.UTF_8.name());
		properties.put("mail.smtp.connectiontimeout", "10000");
		properties.put("mail.smtp.timeout", "10000");
		properties.put("mail.smtp.writetimeout", "10000");

		return Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(config.username(), config.password());
			}
		});
	}

	private static String htmlToPlainText(String html) {
		if (html == null || html.isBlank()) {
			return null;
		}
		return html
				.replaceAll("(?i)<br\\s*/?>", "\n")
				.replaceAll("(?i)</p>", "\n")
				.replaceAll("<[^>]+>", "")
				.replace("&nbsp;", " ")
				.replace("&lt;", "<")
				.replace("&gt;", ">")
				.replace("&amp;", "&")
				.trim();
	}

	private static String normalizeNullable(String value) {
		if (value == null) {
			return null;
		}
		String normalized = value.trim();
		return normalized.isEmpty() ? null : normalized;
	}

	private static boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}

	private static String defaultString(String value) {
		return value == null ? "" : value;
	}

}
