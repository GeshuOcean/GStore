package com.gstore.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Ocean .
 */
@Slf4j
public class CookieUtil {
    private final static String COOKIE_DOMAIN = ".happygstore.com";
    private final static String COOKIE_NAME = "gstore_login_token";

    public static String readLoginToken(HttpServletRequest request){
      Cookie[] cks = request.getCookies();
      if(cks !=null){
          for (Cookie ck: cks) {
              log.info("read cookieName:{},cookieValue:{}",ck.getName(),ck.getValue());
              if (StringUtils.equals(ck.getName(),COOKIE_NAME)){
                  log.info("read cookieName:{},cookieValue:{}",ck.getName(),ck.getValue());
                  return ck.getValue();
              }
          }
      }
      return null;
    }

    public static void writeLoginToken(HttpServletResponse response,String token){
        Cookie ck = new Cookie(COOKIE_NAME,token);
        ck.setDomain(COOKIE_DOMAIN);
        ck.setPath("/");//代表设置在根目录
        ck.setHttpOnly(true);//设置无法通过脚本获取信息，浏览器也不会把信息发给第三方

//        单位是秒，如果maxage不设置的话，cookie就不会写入硬盘，而是写在内存。只在当前页面有效
//        如果是-1，代表永久有效
        ck.setMaxAge(60*60*24*365);
        log.info("write cookieName():{},cookieValue:{}",ck.getName(),ck.getValue());
        response.addCookie(ck);
    }

    public static void delLoginToken(HttpServletRequest request,HttpServletResponse response){
        Cookie[] cks = request.getCookies();
        if(cks != null){
            for (Cookie ck : cks){
                if (StringUtils.equals(ck.getName(),COOKIE_NAME)){
                    ck.setDomain(COOKIE_DOMAIN);
                    ck.setPath("/");
                    ck.setMaxAge(0);//设置为0，代表删除次Cookie
                    log.info("read cookieName:{},cookieValue:{}",ck.getName(),ck.getValue());
                    response.addCookie(ck);
                    return;
                }
            }
        }
    }
}
