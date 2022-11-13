package com.ecommerce.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ecommerce.models.User;
import com.ecommerce.service.UserAuthService;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private UserAuthService userAuthService;
	
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String username=null;
		String jwt = null;
		
		final String authorizationHeader = request.getHeader("Authorization");
		
		if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
			jwt = authorizationHeader.substring(7);
			
			try {
				username = jwtUtil.getUser(jwt).getUsername();
			}catch(IllegalArgumentException  e) {
				throw new IllegalArgumentException("error during extracting username from jwt");
			}
			
		}
		
		if(username !=null && SecurityContextHolder.getContext().getAuthentication() == null) {
			User user = userAuthService.loadUserByUsername(username);
			
			if(jwtUtil.validateToken(jwt, user)) {
//				String role = "";
				
				for(GrantedAuthority r:user.getAuthorities()) {
					System.out.println(r.getAuthority());
				}
//				System.out.println(role);
				
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities());
				usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			}
		}
		
		filterChain.doFilter(request, response);
	}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	}
		// TODO Auto-generated method stub
//		final String authorizationHeader = request.getHeader("Authorization");
//	
//		String jwt = null;
//		String username = null;
//		if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
//			jwt = authorizationHeader.substring(7);
//			System.out.println(jwt);
//			username = jwtUtil.getUser(jwt).getUsername();
//			System.out.println(username);
//		}
//		
//		if(SecurityContextHolder.getContext().getAuthentication()==null) {
//			System.out.println("-----------------------------------------------------");
//			User user = userAuthService.loadUserByUsername(username);
//			System.out.println(user.toString());
//			if(jwtUtil.validateToken(jwt, user)) {
//				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities());
//				usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
//			}
//		}
//		filterChain.doFilter(request, response);
//	}

//}
