package com.heanbian.block.email;

import static java.lang.String.*;

public class EmailException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public EmailException() {
		super();
	}

	public EmailException(String message, Object... args) {
		this(null, message, args);
	}

	public EmailException(Throwable cause, String message, Object... args) {
		super(format(message, args), cause);
	}

}
