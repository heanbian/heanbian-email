package com.heanbian.block.email;

/**
 * 邮件配置类
 */
public record EmailConfig(String host, int port, String username, String password, String from, boolean debug) {

	public static EmailConfig of(String host, int port, String username, String password, String from, boolean debug) {
		return new EmailConfig(host, port, username, password, from, debug);
	}

}
