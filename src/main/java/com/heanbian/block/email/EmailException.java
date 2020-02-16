package com.heanbian.block.email;

/**
 * 邮件异常类
 * 
 * @author Heanbian
 * @version 5.0
 */
@SuppressWarnings("serial")
public class EmailException extends RuntimeException {

	public EmailException() {
		super();
	}

	public EmailException(String message) {
		super(message);
	}

	public EmailException(String message, Throwable cause) {
		super(message, cause);
	}

	public static <T> T requireNonNull(T obj, String message) {
		if (obj == null) {
			throw new EmailException(message);
		}
		return obj;
	}
}
