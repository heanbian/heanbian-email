package com.heanbian.block.email;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * 邮件消息类
 * 
 */
public class EmailMessage {

	/**
	 * 主题
	 */
	private String subject;

	/**
	 * 接收人
	 */
	private Set<String> toAddress;

	/**
	 * 抄送人
	 */
	private Set<String> ccAddress;

	/**
	 * 密送人
	 */
	private Set<String> bccAddress;

	/**
	 * 附件，URLs
	 */
	private Set<String> attachments;

	/**
	 * 附件，Files
	 */
	private Set<File> files;

	/**
	 * 内容
	 */
	private String content;

	public EmailMessage() {
	}

	public EmailMessage(String subject, String toAddress, String content) {
		this(subject, Set.of(toAddress), null, null, content);
	}

	public EmailMessage(String subject, Set<String> toAddress, String content) {
		this(subject, toAddress, null, null, content);
	}

	public EmailMessage(String subject, Set<String> toAddress, Set<String> ccAddress, Set<String> bccAddress,
			String content) {
		this.subject = subject;
		this.toAddress = toAddress;
		this.ccAddress = ccAddress;
		this.bccAddress = bccAddress;
		this.content = content;
	}

	public String getSubject() {
		return subject;
	}

	public EmailMessage setSubject(String subject) {
		this.subject = subject;
		return this;
	}

	public Set<String> getToAddress() {
		return toAddress;
	}

	public EmailMessage setToAddress(Set<String> toAddress) {
		this.toAddress = toAddress;
		return this;
	}

	public EmailMessage addToAddress(String toAddres) {
		if (this.toAddress == null) {
			this.toAddress = new HashSet<>();
		}
		this.toAddress.add(toAddres);
		return this;
	}

	public Set<String> getCcAddress() {
		return ccAddress;
	}

	public EmailMessage setCcAddress(Set<String> ccAddress) {
		this.ccAddress = ccAddress;
		return this;
	}

	public EmailMessage addCcAddress(String ccAddres) {
		if (this.ccAddress == null) {
			this.ccAddress = new HashSet<>();
		}
		this.ccAddress.add(ccAddres);
		return this;
	}

	public Set<String> getBccAddress() {
		return bccAddress;
	}

	public EmailMessage setBccAddress(Set<String> bccAddress) {
		this.bccAddress = bccAddress;
		return this;
	}

	public EmailMessage addBccAddress(String bccAddres) {
		if (this.bccAddress == null) {
			this.bccAddress = new HashSet<>();
		}
		this.bccAddress.add(bccAddres);
		return this;
	}

	public Set<String> getAttachments() {
		return attachments;
	}

	public EmailMessage setAttachments(Set<String> attachments) {
		this.attachments = attachments;
		return this;
	}

	public EmailMessage addAttachment(String attachment) {
		if (this.attachments == null) {
			this.attachments = new HashSet<>();
		}
		this.attachments.add(attachment);
		return this;
	}

	public Set<File> getFiles() {
		return files;
	}

	public EmailMessage setFiles(Set<File> files) {
		this.files = files;
		return this;
	}

	public EmailMessage addFile(File file) {
		if (this.files == null) {
			this.files = new HashSet<>();
		}
		this.files.add(file);
		return this;
	}

	public String getContent() {
		return content;
	}

	public EmailMessage setContent(String content) {
		this.content = content;
		return this;
	}

}
