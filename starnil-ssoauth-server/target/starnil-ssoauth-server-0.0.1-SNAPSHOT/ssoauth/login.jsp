<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<%
String serveurl = request.getParameter("serveurl");
%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>SSOAuth 统一登录界面</title>
<style type="text/css">
	body,p,div,ul,li,h1,h2,h3,h4,h5,h6{
		margin:0;
		padding: 0;
	}
	body{
		background: #E9E9E9; 
	}
	#login{
		width: 400px;
		height: 250px;
		background: #FFF;
		margin:200px auto;
		position: relative;
	}
	#login h1{
		text-align:center;
		position:absolute;
		width: 100%;
		top:-40px;
		font-size:21px;
	}
	#login form p{
		text-align: center;
	}
	#username{
		background:url(images/user.png) rgba(0,0,0,.1) no-repeat;
		width: 200px;
		height: 30px;
		border:solid #ccc 1px;
		border-radius: 3px;
		padding-left: 32px;
		margin-top: 50px;
		margin-bottom: 30px;
	}
	#password{
		background: url(images/pwd.png) rgba(0,0,0,.1) no-repeat;
		width: 200px;
		height: 30px;
		border:solid #ccc 1px;
		border-radius: 3px;
		padding-left: 32px;
		margin-bottom: 30px;
	}
	#submit{
		width: 232px;
		height: 30px;
		background: rgba(0,0,0,.1);
		border:solid #ccc 1px;
		border-radius: 3px;
	}
	#submit:hover{
		cursor: pointer;
		background:#D8D8D8;
	}
</style>
</head>
<body>
<div id="login">
<h1>STARNIL SSOAuth 统一登录</h1>	
	<form action="/ssoauth/login" method="post" >
  		<input type="hidden" name="serveurl" value="<%=serveurl%>"/>
		<p><input type="text" name="username" id="username" placeholder="用户名"></p>
		<p><input type="password" name="password" id="password" placeholder="密码"></p>
		<p><input type="submit" id="submit" value="登录"></p>
	</form>
</div>
</body>
</html>