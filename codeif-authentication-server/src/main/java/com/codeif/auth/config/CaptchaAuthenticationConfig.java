/*
package com.codeif.auth.config;

import com.codeif.auth.filter.CaptchaAuthenticationFilter;
import com.codeif.auth.handler.CustomAuthenticationFailureHandler;
import com.codeif.auth.handler.CustomAuthenticationSuccessHandler;
import com.codeif.auth.provider.SmsCodeAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

*/
/**
 * 图片验证码自定义配置
 **//*

@Component
public class CaptchaAuthenticationConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

	@Autowired
	private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
	@Autowired
	private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	public void configure(HttpSecurity http) {
		CaptchaAuthenticationFilter myFilter = new CaptchaAuthenticationFilter();
		myFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
		myFilter.setAuthenticationFailureHandler(customAuthenticationFailureHandler);
		myFilter.setAuthenticationSuccessHandler(customAuthenticationSuccessHandler);

		SmsCodeAuthenticationProvider smsCodeAuthenticationProvider = new SmsCodeAuthenticationProvider();
		smsCodeAuthenticationProvider.setUserDetailsService(userDetailsService);

		http.authenticationProvider(smsCodeAuthenticationProvider)
			.addFilterAt(myFilter, UsernamePasswordAuthenticationFilter.class);

	}
}*/