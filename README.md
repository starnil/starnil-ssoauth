# STARNIL-SSOAUTH单点登录组件。


## 一、项目介绍
项目基于Servlet实现的一个SSO认证组件。组件包含server与client端。项目不包含数据库部分内容，
server端对于用户登录认证只提供一个认证过程演示，如需将该组件应用到生产系统中请自行实现系统中指定接口进行扩展即可。


### 1.1、服务器端

服务器端实现用户认证、退出登录（子系统同步退出）、心跳检测（激活）、Token验证、过期用户清理以及登录用户Ticket数据缓存。


### 1.2、服务器端

客户端通过Filter拦截指定资源/页面，对被拦截内容进行验证，如果本地不存在登录用户信息（session）则重定向到服务器端进行认证，
认证通过后返回请求资源/页面（一并返回Token）。

### 1.3、账号信息

系统默认账号及密码：admin。



## 二、认证流程
![image](https://github.com/starnil/starnil-ssoauth/blob/master/doc/%E8%AE%A4%E8%AF%81%E6%B5%81%E7%A8%8B.png)

## 三、关于扩展


## 四、demo配置