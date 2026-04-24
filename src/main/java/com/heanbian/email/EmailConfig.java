package com.heanbian.email;

public record EmailConfig(
		String host,
		int port,
		String username,
		String password,
		String from,
		boolean debug) {

	public EmailConfig {
		host = requireText(host, "SMTP host 不能为空");
		if (port < 1 || port > 65535) {
			throw new IllegalArgumentException("SMTP port 必须在 1~65535 之间");
		}
		username = requireText(username, "SMTP 用户名不能为空");
		password = requireText(password, "SMTP 密码不能为空");
		from = requireText(from, "发件人信息不能为空");
	}

	public static EmailConfig of(String host, int port, String username, String password, String from, boolean debug) {
		return new EmailConfig(host, port, username, password, from, debug);
	}

	public static EmailConfig of(String host, int port, String username, String password, String from) {
		return of(host, port, username, password, from, false);
	}

	/**
	 * 兼容当前 API：
	 * - 如果 from 看起来像邮箱地址，则把它当发件地址
	 * - 否则把 username 当发件地址，from 当显示名
	 */
	public String fromAddress() {
		return looksLikeEmail(from) ? from : username;
	}

	public String fromPersonal() {
		return looksLikeEmail(from) ? null : from;
	}

	private static String requireText(String value, String message) {
		if (value == null || value.trim().isEmpty()) {
			throw new IllegalArgumentException(message);
		}
		return value.trim();
	}

	private static boolean looksLikeEmail(String value) {
		return value != null && value.contains("@");
	}

}
