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
组件支持对登录业务逻辑、登录用户缓存数据进行扩展。现组件中登录认证默认采用SSOLogin类（实现Login接口）；数据缓存采用本地缓存及SSOCacheImpl类（实现SSOCache接口）。

### 3.1、登录验证扩展（实现Login接口）
**参考SSOLogin类**
如需要结合数据库及业务系统使用该组件，对于登录只需要创建一个实现类并实现Login接口，同时需要把该类在web.xml配置中指定给用于验证的Servlet即可。
```java
public class SSOLogin implements Login {

 @Override
 public LoginStatus verify(String userName, String password, String imgCode) {
  LoginStatus ls = new LoginStatus();
  if("admin".equals(userName) && "admin".equals(password)) {
   String userId = GUIDUtil.uuid();
   SSOUser user = new SSOUser(); // 创建一个 user对象。
   user.setId(userId); // userId请确保唯一，否则SSO在创建用户缓存数据时因为id相同会导致数据冲突，引起不可预见性错误。
   user.setName(userName); // 必须，用户名将返回给客户端。
   ls.setState(1000); // 登录成功设置1000（必须）
   // 登录成功，必须给LoginStatus设置一个正确的SSOUser对象（包含用户ID和用户名），用于创建SSO系统的用户信息用。
   ls.setUser(user);
  } else {
   ls.setState(444);
  }
  return ls;
 }
}
```

## 四、demo配置