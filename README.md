# SpringBootVul-GUI
一个半自动化springboot打点工具，内置目前springboot所有漏洞

## 声明



> **⚠️ 本项目所有内容仅作为安全研究和授权测试使用, 相关人员对因误用和滥用该项目造成的一切损害概不负责**

## 0x01简介

本着简单到极致的原则，开发了这么一款半自动化工具（PS：这个工具所包含了20个漏洞，代码工作量也不是一般得多，开发不易，有任何问题可提issue）

尽管是一个为懒人量身打造的工具，但是还是有几点需要注意



注意！！以下几点请务必谨记

注意！！以下几点请务必谨记

注意！！以下几点请务必谨记

1、工具中出现的漏洞，需要先熟悉之后才能利用本工具。部分接口极其容易造成服务器的springboot服务异常，包括不限于报错、程序退出、无法执行代码，请小心使用！！

2、本工具仅限学习使用，请勿用于非法用途！！！！！！！！

3、工具仍在开发阶段，目前测试无异常，使用次数多难免会造成不可预见的问题，请提issue，确认后会修改BUG，感谢各位。



## 0x02使用教程

```bash
# git clone下载本项目
git clone https://github.com/wh1t3zer/SpringBootVul-GUI/tree/develop
可直接运行Springboot_vul.java

# 也可直接下载jar包
java -jar SpringBootVul_GUI.jar
```

确保采用的是jdk1.8版本

本系统采用的是javafx，高版本需自行加载javafx依赖

本项目中的heapdump转存会自动下载网站上的文件，并会放到jar包下的HFile文件夹

## 0x03开发进度 TODO

* [x] 配置不正当导致的泄露
* [x] 脱敏密码明文(1)
* [x] 增加漏洞利用选择模块，可以选择单一或多个漏洞进行检测
* [x] 命令执行漏洞式支持交互式执行命令
* [x] Spring Gateway RCE
* [x] heapdump文件下载导致敏感信息泄露
* [x] druid数据连接池
* [x] 脱敏密码明文(2)
* [x] 脱敏密码明文(3)
* [ ] 基于SpEL注入的RCE
* [ ] SpringCloud的SnakeYaml的RCE
* [ ] eureka中xstream基于反序列化的RCE
* [ ] jolokia中logback基于JNDI注入的RCE
* [ ] jolokia中realm基于JNDI注入的RCE
* [ ] H2数据库设置query属性的RCE
* [ ] h2数据库的控制台基于JNDI注入的RCE
* [ ] mysql中jdbc基于反序列化的RCE
* [ ] logging.config的logback基于JNDI的RCE
* [ ] logging.config的groovyRCE
* [ ] spring.main.source的groovyRCE
* [ ] spring.datasource.data 基于h2数据库的RCE

## 0x04短期目标 Prepare

* [x] 一键打入内存马(目前只有Spring Cloud Gateway)
* [ ] 加入Bypass逻辑
* [ ] 加入深度扫描任务

## 0x05项目演示

### #1 密码脱敏

脱敏（1）

![](./image/1725461792423.jpg)

![](./image/1725461834405.jpg)

脱敏（2）

得到Authorization字段的数据，用base64解码即可，有时间再优化下能直接显示到文本框里

![](./image/image-20240908184925841.png)

![](./image/image-20240908184831197.png)

脱敏（3）

![](./image/image-20240908185521389.png)

### #2 Spring Cloud Gateway 交互式命令

![](./image/1725462083302.jpg)

![](./image/1725462104669.jpg)

### #3 端点扫描

端点扫描经过延时降速处理，防止请求频繁，heapdump文件无法下载，不过偶尔还会发生，直接手动下载就可以了

![](./image/1725462287383.jpg)

### #4 一键上马

![](./image/WechatIMG1409.jpg)

![](./image/WechatIMG1415.jpg)

## 0x06参考项目

感谢以下项目或文章提供帮助和支持，具体漏洞原理可参考以下地址

1、https://github.com/LandGrey/SpringBootVulExploit

2、https://mp.weixin.qq.com/s/2wKB3jACAkIiIZ96tVb5fA

3、https://xz.aliyun.com/t/11331?time__1311=Cq0xR70QoiqDqGXYYIhxWucgYDkIHT1iT4D#toc-3
