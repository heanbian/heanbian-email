# heanbian-email

### pom.xml

```xml

<dependency>
	<groupId>com.heanbian</groupId>
	<artifactId>heanbian-email</artifactId>
	<version>11.0.5</version>
</dependency>

```

注：JDK 11+ ，具体最新版本，可以到maven官网查找。

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
