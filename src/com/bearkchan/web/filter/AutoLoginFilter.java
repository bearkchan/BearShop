package com.bearkchan.web.filter;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.bearkchan.domain.User;
import com.bearkchan.service.UserService;


public class AutoLoginFilter implements Filter{
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		//强转成HttpServletRequest
		HttpServletRequest req = (HttpServletRequest) request;
		
		User user = (User) req.getSession().getAttribute("user");
		
		if(user==null){
			String cookie_username = null;
			String cookie_password = null;
			
			//获取携带用户名和密码cookie
			Cookie[] cookies = req.getCookies();
			if(cookies!=null){
				for(Cookie cookie:cookies){
					//获得想要的cookie
					if("cookie_username".equals(cookie.getName())){
						cookie_username = cookie.getValue();
					}
					if("cookie_password".equals(cookie.getName())){
						cookie_password = cookie.getValue();
					}
				}
			}
			
			if(cookie_username!=null&&cookie_password!=null){
				//去数据库校验该用户名和密码是否正确
				UserService service = new UserService();
				try {
					user = service.login(cookie_username,cookie_password);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				//完成自动登录 
				req.getSession().setAttribute("user", user);
				
			}
		}
		
		
		//放行
		chain.doFilter(req, response);
		
	}
	

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}

	

	@Override
	public void destroy() {
		
	}

}
