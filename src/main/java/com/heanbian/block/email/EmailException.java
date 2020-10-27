package com.heanbian.block.email;

/**
 * 邮件异常类
 * 
 * @author Heanbian
 */
public class EmailException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public EmailException() {
		super();
	}

	public EmailException(String message, Object... args) {
		super(String.format(message, args));
	}

	public EmailException(Throwable cause, String message, Object... args) {
		super(String.format(message, args), cause);
	}

}
