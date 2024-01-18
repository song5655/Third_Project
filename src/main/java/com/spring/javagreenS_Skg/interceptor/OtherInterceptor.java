package com.spring.javagreenS_Skg.interceptor;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class OtherInterceptor extends HandlerInterceptorAdapter {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		HttpSession session = request.getSession();
  	int level = session.getAttribute("sLevel")==null ? 99 : (int) session.getAttribute("sLevel");
  	if(level == 99) {	// 비로그인 사용불가
  		RequestDispatcher dispatcher = request.getRequestDispatcher("/msg/MemberNo");
  		dispatcher.forward(request, response);
  		return false;
  	}
  	return true;
	}
		
}
