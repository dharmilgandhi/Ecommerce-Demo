package com.ecommerce.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;

import com.ecommerce.repo.ProductRepo;
import com.ecommerce.config.JwtUtil;
import com.ecommerce.models.Product;
import com.ecommerce.models.User;

@RestController
@RequestMapping("/api/public")
public class PublicController {
	
	@Autowired 
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private ProductRepo productRepo;
	
	@GetMapping("/product/search")
	public List<Product> getProducts(@RequestParam("keyword") String keyword ){
//		return "Keyword"+keyword;
		return productRepo.findByProductNameContainingIgnoreCaseOrCategoryCategoryNameContainingIgnoreCase(keyword, keyword);
	}
	
	
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody User user) throws Exception{
		System.out.println(user.getUsername());
		try {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword()));
		}catch(Unauthorized e) {throw new Exception("incorrect username or password");}
		System.out.println(user.getUsername());
		final String jwt = jwtUtil.generateToken(user.getUsername());
		return new ResponseEntity<String>(jwt,HttpStatus.OK);
	}

}
