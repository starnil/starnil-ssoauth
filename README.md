# STARNIL-SSOAUTH单点登录组件。


## 一、项目介绍
项目基于Servlet实现的一个SSO认证组件。组件包含server与client端。项目不包含数据库部分内容，
server端对于用户登录认证只提供一个认证过程演示，如需将该组件应用到生产系统中，请自行实现系统中指定接口进行扩展即可。


### 1.1、服务器端

服务器端实现用户认证、退出登录（子系统同步退出）、心跳检测（激活）、Token验证、过期用户清理以及登录用户Ticket数据缓存。


### 1.2、客户端

客户端通过Filter拦截指定资源/页面，对被拦截内容进行验证，如果本地不存在登录用户信息（session）则重定向到服务器端进行认证，
认证通过后返回请求资源/页面（一并返回Token）。

### 1.3、账号信息

系统默认账号及密码：admin。



## 二、认证流程
![image](https://github.com/starnil/starnil-ssoauth/blob/master/doc/%E8%AE%A4%E8%AF%81%E6%B5%81%E7%A8%8B.png)

## 三、关于扩展
组件支持对登录业务逻辑、登录用户缓存数据进行扩展。现组件中登录认证默认采用SSOLogin类（实现Login接口）；数据缓存采用本地缓存及SSOCacheImpl类（实现SSOCache接口）。

### 3.1、登录验证扩展（实现Login接口）
如需要结合数据库及业务系统使用该组件，对于登录只需要创建一个实现类并实现Login接口，同时需要把该类在web.xml配置中指定给用于验证的Servlet即可。<br><br>
**参考SSOLogin.java**
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
扩展新的Login类之后，通过在web.xml文件Servlet（authentication）类中指定loginClass即可。<br><br>
**web.xml代码片段**
```Xml
<init-param>
 <!-- 用户登录，业务逻辑实现类，通过实现Login接口完成用户验证业务逻辑。 -->
 <param-name>loginClass</param-name>
 <param-value>com.starnil.ms.component.ssoauth.server.servlet.SSOLogin</param-value>
</init-param>
```
### 3.2、缓存扩展（实现SSOCache接口）
当前组件用户登录成功后会向本地缓存中写入Ticket对象与Token数据。Ticket储存当前登录用户基本数据、Token数据用于返回给客户端（只使用一次）。如需要结合分布式缓存服务，只需要新建一个类并实现SSOCache，同时通过web.xml指定cacheClass即可（注：需要启动SSOListener）。<br><br>
**web.xml代码片段**
```Xml
<!-- 
  缓存处理类，该类用于实现SSO用户登录后token、ticket数据的存储。默认采用本地缓存（不适用分布式环境）。
  用户可设置自己的处理类实现分布式缓存（基于redis、memcached等），但必须实现
  “com.starnil.ms.component.ssoauth.cache.SSOCache”接口。
-->
<context-param>
 <param-name>cacheClass</param-name>
 <param-value>com.starnil.ms.component.ssoauth.cache.SSOCacheImpl</param-value>
</context-param>
<!-- 清理过期用户间隔时间（单位分），3分钟清理一次 -->
<context-param>
 <param-name>clearIntervalTime</param-name>
 <param-value>3</param-value>
</context-param>
<!-- SSO监听，用于实例化系统缓存（这里可指定分布式缓存服务等）、清理过期用户等 -->
<listener>
 <listener-class>
  com.starnil.ms.component.ssoauth.server.SSOListener
 </listener-class>
</listener>
```
## 四、演示demo相关配置
项目中提供3个demo项目starnil-ssoauth-server、starnil-ssoauth-client1、starnil-ssoauth-client2供测试使用（基于tomcat）。

### 4.1、tomcat配置文件（server.xml）
添加3个Host即可，注意docBase配置成本实际路径。<br><br>
**server.xml代码片段**
```Xml
<Host name="www.ssoserver.com"  appBase="webapps" unpackWARs="true" autoDeploy="true">
 <Context  path="" docBase="D:\starnil-ssoauth-server\target\starnil-ssoauth-server-0.0.1-SNAPSHOT">
 </Context>
</Host>
<Host name="www.ssoclient1.com"  appBase="webapps" unpackWARs="true" autoDeploy="true">
 <Context  path="" docBase="D:\starnil-ssoauth-client1\target\starnil-ssoauth-client1-0.0.1-SNAPSHOT">
 </Context>
</Host>
<Host name="www.ssoclient2.com"  appBase="webapps" unpackWARs="true" autoDeploy="true">
 <Context  path="" docBase="D:\starnil-ssoauth-client2\target\starnil-ssoauth-client2-0.0.1-SNAPSHOT">
 </Context>
</Host>
```
### 4.2、修改hosts文件
如本机需要通过4.1中域名访问各项目，需要对C:\Windows\System32\Drivers\etc目录下hosts文件进行修改。<br><br>
**hosts文件添加映射**
```Txt
127.0.0.1 www.ssoserver.com
127.0.0.1 www.ssoclient1.com
127.0.0.1 www.ssoclient2.com
```
## 五、其他
如需帮助，致信：starnil@139.com