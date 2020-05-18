### 项目介绍
阿里云短信验证码的实例。

### 技术工具
项目采用SpringBoot构建。
涉及到的其他技术工具有阿里云短信服务，fastjson。

### 开发环境
Mac  IDEA  
JDK8   
Redis   

### 如何运行
1、git clone https://github.com/ID-WangQiang/aliyun-sms.git   
2、用IDEA打开项目，在配置文件application.properties中填写AccessKey、AccessSecret、阿里云短板模版Code  
3、运行AliyunSmsApplication.java文件，访问localhost:8080/send/{手机号}

### 参考资料
[阿里云短信服务](https://help.aliyun.com/product/44282.html)  
[Java SDK](https://help.aliyun.com/document_detail/112148.html)  
[短信服务代码实例](https://api.aliyun.com/?spm=a2c4g.11186623.2.15.39d060e2bjChk2#/?product=Dysmsapi&version=2017-05-25&api=SendSms&tab=DEMO&lang=JAVA)   
[狂神说](https://www.bilibili.com/video/BV1c64y1M7qN)  
