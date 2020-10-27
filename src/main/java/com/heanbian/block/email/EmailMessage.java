package com.heanbian.block.email;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * 邮件消息类
 * 
 * @author Heanbian
 */
public class EmailMessage {

	/**
	 * 主题
	 */
	private String subject;

	/**
	 * 接收人邮件地址
	 */
	private Set<String> toAddress;

	/**
	 * 抄送人邮件地址
	 */
	private Set<String> ccAddress;

	/**
	 * 密送人邮件地址
	 */
	private Set<String> bccAddress;

	/**
	 * 附件，Internet URLs
	 */
	private Set<String> attachments;

	/**
	 * 附件，Local files
	 */
	private Set<File> files;

	/**
	 * 正文内容
	 */
	private String content;

	public EmailMessage() {
	}

	/**
	 * 消息体
	 * 
	 * @param subject   {@link #subject}
	 * @param toAddress {@link #toAddress}
	 * @param content   {@link #content}
	 */
	public EmailMessage(String subject, String toAddress, String content) {
		this(subject, Set.of(toAddress), null, null, content);
	}

	/**
	 * 消息体
	 * 
	 * @param subject   {@link #subject}
	 * @param toAddress {@link #toAddress}
	 * @param content   {@link #content}
	 */
	public EmailMessage(String subject, Set<String> toAddress, String content) {
		this(subject, toAddress, null, null, content);
	}

	/**
	 * 消息体
	 * 
	 * @param subject    {@link #subject}
	 * @param toAddress  {@link #toAddress}
	 * @param ccAddress  {@link #ccAddress}
	 * @param bccAddress {@link #bccAddress}
	 * @param content    {@link #content}
	 */
	public EmailMessage(String subject, Set<String> toAddress, Set<String> ccAddress, Set<String> bccAddress,
			String content) {
		this.subject = subject;
		this.toAddress = toAddress;
		this.ccAddress = ccAddress;
		this.bccAddress = bccAddress;
		this.content = content;
	}

	/**
	 * @return {@link #subject}
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject {@link #subject}
	 * @return EmailMessage
	 */
	public EmailMessage setSubject(String subject) {
		this.subject = subject;
		return this;
	}

	/**
	 * @return {@link #toAddress}
	 */
	public Set<String> getToAddress() {
		return toAddress;
	}

	/**
	 * @param toAddress {@link #toAddress}
	 * @return EmailMessage
	 */
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

	/**
	 * @return {@link #ccAddress}
	 */
	public Set<String> getCcAddress() {
		return ccAddress;
	}

	/**
	 * @param ccAddress {@link #ccAddress}
	 * @return EmailMessage
	 */
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

	/**
	 * @return {@link #bccAddress}
	 */
	public Set<String> getBccAddress() {
		return bccAddress;
	}

	/**
	 * @param bccAddress {@link #bccAddress}
	 * @return EmailMessage
	 */
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

	/**
	 * @return {@link #attachments}
	 */
	public Set<String> getAttachments() {
		return attachments;
	}

	/**
	 * @param attachments {@link #attachments}
	 * @return EmailMessage
	 */
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

	/**
	 * @return {@link #files}
	 */
	public Set<File> getFiles() {
		return files;
	}

	/**
	 * @param files {@link #files}
	 * @return EmailMessage
	 */
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

	/**
	 * @return {@link #content}
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content {@link #content}
	 * @return EmailMessage
	 */
	public EmailMessage setContent(String content) {
		this.content = content;
		return this;
	}

}
