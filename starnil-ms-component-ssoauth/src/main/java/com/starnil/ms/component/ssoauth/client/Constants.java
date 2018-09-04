package com.starnil.ms.component.ssoauth.client;

/**
 * 系统应用中部分静态常量。开发中可在此加入所需常量。尽量满足static、final特性。
 * 
 * @author starnil@139.com
 * @version 1.0
 *
 */
public class Constants {
	
	/** 系统已登录user session名，需获取user信息，可通过此名在session中查找 */
	public static final String SSOAUTH_USER_SESSION = "SSOAUTH_USER_SESSION_STARNIL_MS";
	public static final String SSOAUTH_CLIENT_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCJPwCkU+ocXFWena0Dc4a2z803+TiJRr5Zs3YU45a8nWruYBPRvk+cqfoVLOugwAjgLwSmI5szdsyk0V3w2pq5AHNgVH9DNSQRpX1Vf+Gw4t5aaNi1XioekFKntCy328l8ORerRCCECrbBzir+r5/T91SX7z4zwT951ZozbYmyawIDAQAB";
	public static final String SSOAUTH_CLIENT_PRIVATE_KEY = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAJiojQwWkzRuzmc1vB3Nzvmg1DblGYZd3YD0RZLKNfAcFDHDIW0wZvAxYJNe6OIMF/SKwlfswRx5e2z4olBaJCnKXzktLom0sTtXWTbfFFqEic+yfqNpNc0FmJk3FwfMmr4upYVC5R7PJ3xNMdOFFzp64DFySNIOo3iM43g7g3kDAgMBAAECgYAZed94CfhtAqTtcnk/XA5TeHSR6K/WzalekOVfduGDfZwdJdxZNX+oTAU68tbYin4g8Fs4gylDED7505B66mAoWxRhmkeBQ9610s5hycONKWJ5zEc8nHodS3LFMEbsOF61rxoUgHkc2dOFTVPSivthbEfI5fzi/V+w1i29iBBjKQJBANTuQT6iEQ2ZYAstpdDHaKWZ8jg48SDcCPKn57+XXVrC0E0AX5iPTz7W7oJpB3/+S8Od5WleXS9l2ffOUIQ4xa8CQQC3iVembg2aziHnWjPSvuoH9ELnrLxUsUQMknFOxK5GzM9LDScaiG6dP/qXJWWwpcL5/KJdR2fIE1GHwCwBBmrtAkEAwTjfbgXFcdC/jgVDVhzOQpBbdFd/wLDLUd+59mtV5LmqKmXvWdKaN8z0rNTAYI6TlBThjGd8KjnDvRK0j/ewswJBAJ27kDN9U33ed66/i9grsS/i0XkPr89NhUEUvgJG78vzCpfilt+rrvy/1ln9jtZwrw9u+g8WZe/CAoibNHYxEMUCQQCTEz0/oFb6NplbgF9f9YSS3J8Xcowzn0dkcUK8H4toLiWG12v4m9O74GzPr16/7pd1AUgIyoA+u1ZbigALGlX2";
	public static final String SSOAUTH_CLIENT_SERVICE_URL_NAME = "serveurl";
	public static final String SSOAUTH_TOKEN_NAME = "token";
	public static final String SSOAUTH_CLIENT_LOGOUT_PATH = "logoutPath";
	public static final String SSOAUTH_CLIENT_SESSIONID_NAME = "sessionid_name";
	public static final String SSOAUTH_CLIENT_SESSIONID = "session_id";
	public static final String SSOAUTH_LOGOUT_TYPE = "SSOAuth";
	public static final int SSOAUTH_HTTP_TIMEOUT = 10000;
	public static String SSOAUTH_LOGOUT_PATH = null;
}