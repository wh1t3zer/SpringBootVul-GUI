![views since 2024/09/06](https://visitor-badge-deno.deno.dev/wh1t3zer.SpringBootVul-GUI.svg)

# SpringBootVul-GUI

一个半自动化springboot打点工具，内置目前springboot所有漏洞

## 声明



> **⚠️ 本项目所有内容仅作为安全研究和授权测试使用, 相关人员对因误用和滥用该项目造成的一切损害概不负责**

## 0x01简介

本着简单到极致的原则，开发了这么一款半自动化工具（PS：这个工具所包含了20个漏洞，开发不易，有任何问题可提issue）

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



**文件结构**

```
├──SpringbootVul-GUI
  ├── META-INF/
  ├── resources/		# 存放资源文件、字典和exp的跨文件
  ├── HPFile/				# 存放下载的heapdump
  ├── src/					# 工程代码
  ├── image/				
  ├── libs/					# 所需依赖
```

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

* [x] eureka中xstream基于反序列化的RCE

* [x] spring.datasource.data 基于h2数据库的RCE

* [x] 基于SpEL注入的RCE

* [x] spring.main.source的groovyRCE

* [x] logging.config的groovyRCE

* [x] H2数据库设置query属性的RCE

* [ ] SpringCloud的SnakeYaml的RCE

* [ ] jolokia中logback基于JNDI注入的RCE

* [ ] jolokia中realm基于JNDI注入的RCE

* [ ] h2数据库的控制台基于JNDI注入的RCE

* [ ] mysql中jdbc基于反序列化的RCE

* [ ] logging.config的logback基于JNDI的RCE

  

## 0x04短期目标 Prepare

* [x] 一键打入内存马(目前只有Spring Cloud Gateway)
* [ ] 加入Bypass逻辑
* [ ] 加入深度扫描任务
* [ ] 部分RCE的痕迹一键清除

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

### #3 Eureka 反序列化RCE（慎用）

直接点击getshell反弹，单纯poc测试的没写，python文件放同一目录下了，需要在vps启用2个端口，一个是你python服务器的端口，一个是反弹端口，写在python文件中，反弹端口默认是9000，注意这两个端口区别，输入框的端口是python端口

**注意！！！**该数据包发送后会驻留到目标Eureka，会不断请求，若造成服务器出错时，可能会导致无法访问网站的路由

![](./image/image-20240911124128856.png)

### #4 H2DatabaseSource RCE（慎用）

目前已经基本完成一键getshell，理论上只要在不关闭的情况下可以无限弹，因为目前的payload是从T5开始的，如果遇到网站被测试过时，那大概率会报错而导致对方服务宕机，因为这是不回显RCE，无法判断到底有没有被测试过。

监听端口默认是**8881**

输入框中填写你开启服务器的端口，目前为了能无限弹的机制，暂时只能设置在该项目的resources文件夹开启

```bash
python -m http.server 80
```

![](./image/1726047278563.jpg)

### #5 SpEL注入导致的RCE

可以同时检测多个参数值，要在参数值上打上一个单引号'作为标记'

http://127.0.0.1:9091/article?id=1'&b=2'

getshell功能可以直接弹shell，getshell模块直接输入地址+路由+参数，无需加=和后面的值

![](./image/image-20240912201142594.png)

![](./image/image-20240912201344077.png)

### #6 MainSourceGroovyRCE

poc测试暂时没写，一键getshell监听的端口是托管groovy文件的端口，反弹端口默认为7777

输入框中填写你开启服务器的端口，目前为了更好弹shell，最好设置在该项目的resources文件夹开启

**注意！！！**：“HTTP 服务器如果返回含有畸形 groovy 语法内容的文件，会导致程序异常退出”

所以师傅有需要修改代码或者其他用途的时候，修改代码的时候不要改错groovy内容，并且文件内容也不要随意修改，以防万一

```bash
python -m http.server 80
```

![](./image/image-20240913231419290.png)

### #7 LoggingConfigGroovyRCE

poc测试暂时没写，一键getshell监听的端口是托管groovy文件的端口，反弹端口默认为4444，开启的方法同上

**注意！！！**：“HTTP 服务器如果返回含有畸形 groovy 语法内容的文件，会导致程序异常退出”

所以师傅有需要修改代码或者其他用途的时候，修改代码的时候不要改错groovy内容，并且文件内容也不要随意修改，以防万一

![](./image/image-20240915162516029.png)

### #8 H2DatabaseQueryRCE(慎用)

这个也是跟H2dataSource漏洞一样，会使用sql语句来触发，考虑到无限弹shell并且如果一个网站同时测这两个漏洞，默认设置的含T5类似的，初始值是T15，代码写了递增，测试次数上要注意

![](./image/image-20240915232444633.png)

### #9 端点扫描

端点扫描经过延时降速处理，heapdump可以下载大文件，用了分块，做了个小进度条，以后优化下，textflow布局以后要改

![](./image/image-20240914013633068.png)

### #10 一键上马

![](./image/WechatIMG1409.jpg)

![](./image/WechatIMG1415.jpg)

## 0x06参考项目

感谢以下项目或文章提供帮助和支持，具体漏洞原理可参考以下地址

1、https://github.com/LandGrey/SpringBootVulExploit

2、https://mp.weixin.qq.com/s/2wKB3jACAkIiIZ96tVb5fA

3、https://xz.aliyun.com/t/11331?time__1311=Cq0xR70QoiqDqGXYYIhxWucgYDkIHT1iT4D#toc-3

4、https://blog.csdn.net/weixin_50464560/article/details/121193783

## Star History

[![Star History Chart](https://api.star-history.com/svg?repos=wh1t3zer/SpringBootVul-GUI&type=Date)](https://star-history.com/#wh1t3zer/SpringBootVul-GUI&Date)

