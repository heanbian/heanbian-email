package com.heanbian.email;

/**
 * 邮件配置类
 * 
 * @author heanbian@heanbian.com
 * @since 1.0
 * @version 1.0
 */
public class HEmailConfig {

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
	 * 发送方，默认{@link #username}
	 */
	private String from;

	/**
	 * 是否Debug
	 */
	private boolean debug;

	public HEmailConfig() {
	}

	/**
	 * 配置参数
	 * 
	 * @param host     {@link #host}
	 * @param port     {@link #port}
	 * @param username {@link #username}
	 * @param password {@link #password}
	 * @param from     {@link #from}
	 * @param debug    {@link #debug}
	 */
	public HEmailConfig(String host, int port, String username, String password, String from, boolean debug) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.from = from;
		this.debug = debug;
	}

	/**
	 * @return {@link #host}
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host {@link #host}
	 * @return this
	 */
	public HEmailConfig setHost(String host) {
		this.host = host;
		return this;
	}

	/**
	 * @return {@link #port}
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port {@link #port}
	 * @return this
	 */
	public HEmailConfig setPort(int port) {
		this.port = port;
		return this;
	}

	/**
	 * @return {@link #username}
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username {@link #username}
	 * @return this
	 */
	public HEmailConfig setUsername(String username) {
		this.username = username;
		return this;
	}

	/**
	 * @return {@link #password}
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password {@link #password}
	 * @return this
	 */
	public HEmailConfig setPassword(String password) {
		this.password = password;
		return this;
	}

	/**
	 * @return {@link #from}
	 */
	public String getFrom() {
		return from;
	}

	/**
	 * @param from {@link #from}
	 * @return this
	 */
	public HEmailConfig setFrom(String from) {
		this.from = from;
		return this;
	}

	/**
	 * @return {@link #debug}
	 */
	public boolean isDebug() {
		return debug;
	}

	/**
	 * @param debug {@link #debug}
	 * @return this
	 */
	public HEmailConfig setDebug(boolean debug) {
		this.debug = debug;
		return this;
	}

}
