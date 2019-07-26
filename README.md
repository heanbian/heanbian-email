# heanbian-email
基于Java mail的通用发送邮件工具包

1.pom.xml
```
<dependencies>
 <dependency>
  <groupId>com.heanbian</groupId>
  <artifactId>heanbian-email</artifactId>
  <version>5.0.0</version>
 </dependency>
</dependencies>
```

2.Examples
```
EmailConfig config = new EmailConfig();
config.setHost("smtp.exmail.qq.com");
config.setFrom("系统邮件");
config.setUsername("xxx@qq.com");
config.setPassword("p123456");
config.setPort(465);
EmailTemplate template = new EmailTemplate(config);
template.send(new EmailMessage("测试", "xxx@qq.com", "测试"));
```
