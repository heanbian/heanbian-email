# heanbian-email

### pom.xml

```xml

<dependency>
	<groupId>com.heanbian</groupId>
	<artifactId>heanbian-email</artifactId>
	<version>5.0.0</version>
</dependency>

```

### 程序使用方法

```java

EmailConfig config = new EmailConfig();
config.setHost("smtp.exmail.qq.com");
config.setFrom("系统邮件");
config.setUsername("xxx@qq.com");
config.setPassword("p123456");
config.setPort(465);

EmailMessage message = new EmailMessage("测试", "xxx@qq.com", "测试");

EmailTemplate template = new EmailTemplate(config,message);
template.send();

```
