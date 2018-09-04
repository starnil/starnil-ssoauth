<%@ page language="java" import="com.starnil.ms.component.ssoauth.client.Constants,com.starnil.ms.component.ssoauth.SSOUser" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Client1</title>
</head>
<body style="font-family:'微软雅黑'; font-size:14px;">
<%
SSOUser user = (SSOUser) request.getSession().getAttribute(Constants.SSOAUTH_USER_SESSION);
String userName = user.getName();
%>
<div style="height:32px; line-height:32px; padding:10px; background-color:#9F9; color:#000"><%=userName %>, 欢迎登录【SSOClient1】. &nbsp;&nbsp;
[<a href="/logout">退出</a>]</div>
<br/>
<br/>
STARNIL SSOAUTH 组件。该组件实现系统单点登录。
<br/>
<br/>
作者：starnil@139.com
</body>
</html>