package com.ecommerce.models;

//import java.util.Set;
//import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//
//import com.google.common.collect.Sets;

public enum Role {
	CONSUMER,
	SELLER;
	
}	

class RolesGrantedAuthority implements GrantedAuthority{
	private static final long serialVersionUID = -3408298481881657796L;
	String role;
	
	public RolesGrantedAuthority(String role) {
		this.role=role;
	}

	@Override
	public String getAuthority() {
		// TODO Auto-generated method stub
		return this.role;
	}
	
	
}
