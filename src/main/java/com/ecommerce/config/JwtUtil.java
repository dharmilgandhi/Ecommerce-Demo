package com.ecommerce.config;

import java.util.Base64;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.ecommerce.models.User;
import com.ecommerce.repo.UserRepo;
import com.ecommerce.service.UserAuthService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil {
	
	@Autowired
	private UserAuthService userAuthService;
	
	private String SECRET_KEY = "mysecretkeyxyzabc";
	
	public User getUser(final String token ) {
		String username = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().getSubject();
		System.out.println(username);
		return userAuthService.loadUserByUsername(username);
	}
	
	
	public String generateToken(String username) {
//		String encodedString = Base64.getEncoder().encodeToString(SECRET_KEY.getBytes());
		return Jwts.builder().setSubject(username).signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
	}
	
	
	public Boolean validateToken(final String token,User user) {
		final String username = getUser(token).getUsername();
		System.out.println(username);
		System.out.println(user.getUsername());
		return (username.equals(user.getUsername()));
	}

}
