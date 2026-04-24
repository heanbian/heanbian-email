package com.heanbian.block.email;

import java.util.IllegalFormatException;

public class EmailException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public EmailException() {
		super();
	}

	public EmailException(String message) {
		super(message);
	}

	public EmailException(String message, Throwable cause) {
		super(message, cause);
	}

	public EmailException(Throwable cause) {
		super(cause);
	}

	public EmailException(String message, Object... args) {
		super(formatSafely(message, args));
	}

	public EmailException(Throwable cause, String message, Object... args) {
		super(formatSafely(message, args), cause);
	}

	private static String formatSafely(String message, Object... args) {
		if (message == null) {
			return null;
		}
		if (args == null || args.length == 0) {
			return message;
		}
		try {
			return String.format(message, args);
		} catch (IllegalFormatException ex) {
			return message;
		}
	}

}
