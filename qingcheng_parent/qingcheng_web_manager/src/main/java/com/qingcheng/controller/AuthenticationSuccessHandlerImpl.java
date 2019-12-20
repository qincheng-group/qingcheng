package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.system.LoginLog;
import com.qingcheng.service.system.LoginLogService;
import com.qingcheng.util.WebUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * @Author:JiashengPang  登录成功处理器
 * @Description:   登录认证成功之后的操作  记录登录日志
 * @Date: 14:48 2019/12/16
 */
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

    @Reference
    private LoginLogService loginLogService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        //登录之后会调用
        String loginName = authentication.getName();
        String ip = httpServletRequest.getRemoteAddr();
        LoginLog loginLog = new LoginLog();
        loginLog.setLoginName(loginName); 
        loginLog.setLoginTime(new Date());
        loginLog.setIp(ip);
        loginLog.setLocation(WebUtil.getCityByIP(ip));  //地区

        String agent = httpServletRequest.getHeader("user-agent");
        loginLog.setBrowserName(WebUtil.getBrowserName(agent)); //浏览器名称

        loginLogService.add(loginLog);
        //登录成功后要转发到main.jsp  否则过不去
        httpServletRequest.getRequestDispatcher("/main.html").forward(httpServletRequest,httpServletResponse);
    }
}
