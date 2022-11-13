package com.ecommerce.config;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.RegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ecommerce.service.UserAuthService;

@Configuration
@EnableWebSecurity
public class ApiSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private UserAuthService userAuthService;
	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;
	@Autowired
	private ApiAuthenticationEntryPoint authenticationEntryPoint;
	
//	@Override
	public void configure(WebSecurity web) throws Exception {
	web
		.ignoring()
		.antMatchers("/console/**");
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http
		.csrf().disable()
		.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
		.and()
		.authorizeRequests()
		.antMatchers("/console/**","/api/public/**")
		.permitAll()
		.antMatchers("/api/auth/consumer/**").hasAuthority("CONSUMER")
		.antMatchers("/api/auth/seller/**").hasAuthority("SELLER")
		.anyRequest()
		.authenticated()
		.and()
		.sessionManagement()
		.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		
		http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
}
	
	

	@Autowired
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// TODO Auto-generated method stub
		auth.userDetailsService(userAuthService);

	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return NoOpPasswordEncoder.getInstance();
	}
	
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		// TODO Auto-generated method stub
		return super.authenticationManagerBean();
	}



//	@Bean
//	public RegistrationBean jwtAuthFilterRegister(JwtAuthenticationFilter filter) {
//		return new RegistrationBean() {
//			
//			@Override
//			protected void register(String description, ServletContext servletContext) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			protected String getDescription() {
//				// TODO Auto-generated method stub
//				return null;
//			}
//		};
//	}

}
