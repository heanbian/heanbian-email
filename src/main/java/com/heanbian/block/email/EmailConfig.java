package com.heanbian.block.email;

/**
 * 邮件配置类
 * 
 */
public class EmailConfig {

	/**
	 * 代理邮件主机地址
	 */
	private String host;

	/**
	 * 端口号
	 */
	private int port;

	/**
	 * 用户名
	 */
	private String username;

	/**
	 * 密码
	 */
	private String password;

	/**
	 * 来自
	 */
	private String from;

	/**
	 * Debug
	 */
	private boolean debug;

	public EmailConfig() {
	}

	public EmailConfig(String host, String username, String password) {
		this(host, 465, username, password, username, false);
	}

	public EmailConfig(String host, int port, String username, String password) {
		this(host, port, username, password, username, false);
	}

	public EmailConfig(String host, int port, String username, String password, String from, boolean debug) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.from = from;
		this.debug = debug;
	}

	public String getHost() {
		return host;
	}

	public EmailConfig setHost(String host) {
		this.host = host;
		return this;
	}

	public int getPort() {
		return port;
	}

	public EmailConfig setPort(int port) {
		this.port = port;
		return this;
	}

	public String getUsername() {
		return username;
	}

	public EmailConfig setUsername(String username) {
		this.username = username;
		this.from = username;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public EmailConfig setPassword(String password) {
		this.password = password;
		return this;
	}

	public String getFrom() {
		return from;
	}

	public EmailConfig setFrom(String from) {
		this.from = from;
		return this;
	}

	public boolean isDebug() {
		return debug;
	}

	public EmailConfig setDebug(boolean debug) {
		this.debug = debug;
		return this;
	}

}
