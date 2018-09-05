# STARNIL-SSOAUTH�����¼�����


## һ����Ŀ����
��Ŀ����Servletʵ�ֵ�һ��SSO��֤������������server��client�ˡ���Ŀ���������ݿⲿ�����ݣ�
server�˶����û���¼��ֻ֤�ṩһ����֤������ʾ�����轫�����Ӧ�õ�����ϵͳ�У�������ʵ��ϵͳ��ָ���ӿڽ�����չ���ɡ�


### 1.1����������

��������ʵ���û���֤���˳���¼����ϵͳͬ���˳�����������⣨�����Token��֤�������û������Լ���¼�û�Ticket���ݻ��档


### 1.2���ͻ���

�ͻ���ͨ��Filter����ָ����Դ/ҳ�棬�Ա��������ݽ�����֤��������ز����ڵ�¼�û���Ϣ��session�����ض��򵽷������˽�����֤��
��֤ͨ���󷵻�������Դ/ҳ�棨һ������Token����

### 1.3���˺���Ϣ

ϵͳĬ���˺ż����룺admin��



## ������֤����
![image](https://github.com/starnil/starnil-ssoauth/blob/master/doc/%E8%AE%A4%E8%AF%81%E6%B5%81%E7%A8%8B.png)

## ����������չ
���֧�ֶԵ�¼ҵ���߼�����¼�û��������ݽ�����չ��������е�¼��֤Ĭ�ϲ���SSOLogin�ࣨʵ��Login�ӿڣ������ݻ�����ñ��ػ��漰SSOCacheImpl�ࣨʵ��SSOCache�ӿڣ���

### 3.1����¼��֤��չ��ʵ��Login�ӿڣ�
����Ҫ������ݿ⼰ҵ��ϵͳʹ�ø���������ڵ�¼ֻ��Ҫ����һ��ʵ���ಢʵ��Login�ӿڣ�ͬʱ��Ҫ�Ѹ�����web.xml������ָ����������֤��Servlet���ɡ�<br><br>
**�ο�SSOLogin.java**
```java
public class SSOLogin implements Login {
 @Override
 public LoginStatus verify(String userName, String password, String imgCode) {
  LoginStatus ls = new LoginStatus();
  if("admin".equals(userName) && "admin".equals(password)) {
   String userId = GUIDUtil.uuid();
   SSOUser user = new SSOUser(); // ����һ�� user����
   user.setId(userId); // userId��ȷ��Ψһ������SSO�ڴ����û���������ʱ��Ϊid��ͬ�ᵼ�����ݳ�ͻ�����𲻿�Ԥ���Դ���
   user.setName(userName); // ���룬�û��������ظ��ͻ��ˡ�
   ls.setState(1000); // ��¼�ɹ�����1000�����룩
   // ��¼�ɹ��������LoginStatus����һ����ȷ��SSOUser���󣨰����û�ID���û����������ڴ���SSOϵͳ���û���Ϣ�á�
   ls.setUser(user);
  } else {
   ls.setState(444);
  }
  return ls;
 }
}
```
��չ�µ�Login��֮��ͨ����web.xml�ļ�Servlet��authentication������ָ��loginClass���ɡ�<br><br>
**web.xml����Ƭ��**
```Xml
<init-param>
 <!-- �û���¼��ҵ���߼�ʵ���࣬ͨ��ʵ��Login�ӿ�����û���֤ҵ���߼��� -->
 <param-name>loginClass</param-name>
 <param-value>com.starnil.ms.component.ssoauth.server.servlet.SSOLogin</param-value>
</init-param>
```
### 3.2��������չ��ʵ��SSOCache�ӿڣ�
��ǰ����û���¼�ɹ�����򱾵ػ�����д��Ticket������Token���ݡ�Ticket���浱ǰ��¼�û��������ݡ�Token�������ڷ��ظ��ͻ��ˣ�ֻʹ��һ�Σ�������Ҫ��Ϸֲ�ʽ�������ֻ��Ҫ�½�һ���ಢʵ��SSOCache��ͬʱͨ��web.xmlָ��cacheClass���ɣ�ע����Ҫ����SSOListener����<br><br>
**web.xml����Ƭ��**
```Xml
<!-- 
  ���洦���࣬��������ʵ��SSO�û���¼��token��ticket���ݵĴ洢��Ĭ�ϲ��ñ��ػ��棨�����÷ֲ�ʽ��������
  �û��������Լ��Ĵ�����ʵ�ֲַ�ʽ���棨����redis��memcached�ȣ���������ʵ��
  ��com.starnil.ms.component.ssoauth.cache.SSOCache���ӿڡ�
-->
<context-param>
 <param-name>cacheClass</param-name>
 <param-value>com.starnil.ms.component.ssoauth.cache.SSOCacheImpl</param-value>
</context-param>
<!-- ��������û����ʱ�䣨��λ�֣���3��������һ�� -->
<context-param>
 <param-name>clearIntervalTime</param-name>
 <param-value>3</param-value>
</context-param>
<!-- SSO����������ʵ����ϵͳ���棨�����ָ���ֲ�ʽ�������ȣ�����������û��� -->
<listener>
 <listener-class>
  com.starnil.ms.component.ssoauth.server.SSOListener
 </listener-class>
</listener>
```
## �ġ���ʾdemo�������
��Ŀ���ṩ3��demo��Ŀstarnil-ssoauth-server��starnil-ssoauth-client1��starnil-ssoauth-client2������ʹ�ã�����tomcat����

### 4.1��tomcat�����ļ���server.xml��
���3��Host���ɣ�ע��docBase���óɱ�ʵ��·����<br><br>
**server.xml����Ƭ��**
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
### 4.2���޸�hosts�ļ�
�籾����Ҫͨ��4.1���������ʸ���Ŀ����Ҫ��C:\Windows\System32\Drivers\etcĿ¼��hosts�ļ������޸ġ�<br><br>
**hosts�ļ����ӳ��**
```Txt
127.0.0.1 www.ssoserver.com
127.0.0.1 www.ssoclient1.com
127.0.0.1 www.ssoclient2.com
```
## �塢����
������������ţ�starnil@139.com