# heanbian-email

## 前提条件
JDK11+
## pom.xml

具体版本，可以上Maven中央仓库查询

```
<dependency>
	<groupId>com.heanbian</groupId>
	<artifactId>heanbian-email</artifactId>
	<version>11.3.0</version>
</dependency>
```

## 使用示例

```
import com.heanbian.block.email.*;

public class Test {

	public static void main(String[] args) {
		EmailConfig config = new EmailConfig();
		config.setUsername("759752xxx@qq.com");
		config.setPassword("123456");
		config.setHost("stmp.qq.com");
		config.setPort(456);
		config.setFrom("系统邮件");
		config.setDebug(true);

		EmailMessage message = new EmailMessage();
		message.setSubject("测试主题");
		message.setContent("测试邮件内容");
		message.addToAddress("45847xxx@qq.com");

		EmailTemplate template = new EmailTemplate(config, message);
		template.send();
	}
}
```

说明：支持html邮件内容、附件（URL、File）、接收人、抄送人、密送人等。