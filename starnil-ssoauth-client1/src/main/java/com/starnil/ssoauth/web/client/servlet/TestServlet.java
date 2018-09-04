package com.starnil.ssoauth.web.client.servlet;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestServlet extends HttpServlet {
    private static final long serialVersionUID = 3615122544373006252L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Cookie[] cookies = request.getCookies();
        String username = "";

        //判断用户是否已经登录认证中心
        if (null != cookies) {
            for (Cookie cookie : cookies) {
            	System.out.println("SSOClient1: " + cookie.getName() + " = " + cookie.getValue());
                if ("sso".equals(cookie.getName())) {
                    username = cookie.getValue();   
                    System.out.println("SSOClient1: 验证cookies, username = " + username);           
                    break;
                }
            }
        } else {
            System.out.println("SSOClient1 TestServlet: 验证cookies = null.");
        }
        System.out.println("SSOClient1 cookies = " + cookies);
        
    	request.getRequestDispatcher("/WEB-INF/jsp/welcome.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request, response);
    }

    
}