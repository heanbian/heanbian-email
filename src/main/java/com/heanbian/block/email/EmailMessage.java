package com.heanbian.block.email;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 邮件消息类
 */
public class EmailMessage {

	/**
	 * 主题
	 */
	private String subject;

	/**
	 * 接收人
	 */
	private Set<String> toAddress = new LinkedHashSet<>();

	/**
	 * 抄送人
	 */
	private Set<String> ccAddress = new LinkedHashSet<>();

	/**
	 * 密送人
	 */
	private Set<String> bccAddress = new LinkedHashSet<>();

	/**
	 * 附件，URLs
	 */
	private Set<String> attachments = new LinkedHashSet<>();

	/**
	 * 附件，Files
	 */
	private Set<File> files = new LinkedHashSet<>();

	/**
	 * HTML 内容
	 */
	private String content;

	/**
	 * 纯文本内容，可选
	 */
	private String textContent;

	public EmailMessage() {
	}

	public EmailMessage(String subject, String toAddress, String content) {
		this();
		this.subject = normalizeNullable(subject);
		if (toAddress != null && !toAddress.trim().isEmpty()) {
			addToAddress(toAddress);
		}
		this.content = normalizeNullable(content);
	}

	public EmailMessage(String subject, Set<String> toAddress, String content) {
		this();
		this.subject = normalizeNullable(subject);
		setToAddress(toAddress);
		this.content = normalizeNullable(content);
	}

	public static EmailMessage of(String subject, String toAddress, String content) {
		return new EmailMessage(subject, toAddress, content);
	}

	public String getSubject() {
		return subject;
	}

	public EmailMessage setSubject(String subject) {
		this.subject = normalizeNullable(subject);
		return this;
	}

	public Set<String> getToAddress() {
		return Collections.unmodifiableSet(toAddress);
	}

	public EmailMessage setToAddress(Set<String> toAddress) {
		this.toAddress = copyStringSet(toAddress);
		return this;
	}

	public EmailMessage addToAddress(String toAddress) {
		this.toAddress.add(requireText(toAddress, "接收人邮件地址不能为空"));
		return this;
	}

	public Set<String> getCcAddress() {
		return Collections.unmodifiableSet(ccAddress);
	}

	public EmailMessage setCcAddress(Set<String> ccAddress) {
		this.ccAddress = copyStringSet(ccAddress);
		return this;
	}

	public EmailMessage addCcAddress(String ccAddress) {
		this.ccAddress.add(requireText(ccAddress, "抄送人邮件地址不能为空"));
		return this;
	}

	public Set<String> getBccAddress() {
		return Collections.unmodifiableSet(bccAddress);
	}

	public EmailMessage setBccAddress(Set<String> bccAddress) {
		this.bccAddress = copyStringSet(bccAddress);
		return this;
	}

	public EmailMessage addBccAddress(String bccAddress) {
		this.bccAddress.add(requireText(bccAddress, "密送人邮件地址不能为空"));
		return this;
	}

	public Set<String> getAttachments() {
		return Collections.unmodifiableSet(attachments);
	}

	public EmailMessage setAttachments(Set<String> attachments) {
		this.attachments = copyStringSet(attachments);
		return this;
	}

	public EmailMessage addAttachment(String attachment) {
		this.attachments.add(requireText(attachment, "附件 URL 不能为空"));
		return this;
	}

	public Set<File> getFiles() {
		return Collections.unmodifiableSet(files);
	}

	public EmailMessage setFiles(Set<File> files) {
		this.files = copyFileSet(files);
		return this;
	}

	public EmailMessage addFile(File file) {
		this.files.add(Objects.requireNonNull(file, "附件文件不能为空"));
		return this;
	}

	public String getContent() {
		return content;
	}

	public EmailMessage setContent(String content) {
		this.content = normalizeNullable(content);
		return this;
	}

	public String getTextContent() {
		return textContent;
	}

	public EmailMessage setTextContent(String textContent) {
		this.textContent = normalizeNullable(textContent);
		return this;
	}

	private static Set<String> copyStringSet(Set<String> values) {
		Set<String> result = new LinkedHashSet<>();
		if (values == null) {
			return result;
		}
		for (String value : values) {
			result.add(requireText(value, "集合元素不能为空"));
		}
		return result;
	}

	private static Set<File> copyFileSet(Set<File> values) {
		Set<File> result = new LinkedHashSet<>();
		if (values == null) {
			return result;
		}
		for (File value : values) {
			result.add(Objects.requireNonNull(value, "附件文件不能为空"));
		}
		return result;
	}

	private static String requireText(String value, String message) {
		if (value == null || value.trim().isEmpty()) {
			throw new IllegalArgumentException(message);
		}
		return value.trim();
	}

	private static String normalizeNullable(String value) {
		if (value == null) {
			return null;
		}
		String normalized = value.trim();
		return normalized.isEmpty() ? null : normalized;
	}

}
