package com.starnil.ms.component.ssoauth;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

/**
 * 基于POST请求的重定向类。
 * 
 * @author starnil@139.com
 * @version 1.0
 *
 */
public class RespPostRedirect {
	Map<String, String> parameter = new HashMap<String, String>();
	HttpServletResponse response;

	public RespPostRedirect(HttpServletResponse response) {
		this.response = response;
	}

	public void setParameter(String key, String value) {
		this.parameter.put(key, value);
	}

	public void sendRedirect(String url) throws IOException {
		PrintWriter out = null; 
		try {
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/html;charset=UTF-8");
			out = this.response.getWriter();
			out.println("<!doctype html>");
			out.println("<html>");
			out.println("<head><meta charset=\"UTF-8\"><title>loading……</title></head>");
			out.println("<body>");
			out.println("<form name=\"sendRedirectForm\" action=\"" + url + "\" method=\"post\">");
			Iterator<String> it = parameter.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				out.println("<input type=\"hidden\" name=\"" + key + "\" value=\"" + this.parameter.get(key) + "\"/>");
			}
			out.println("</from>");
			out.println("<script>window.document.sendRedirectForm.submit();</script> ");
			out.println("</body>");
			out.println("</HTML>");
		} finally {
			if(out != null) {
				out.flush();
				out.close();
			}
		}
	}
}
