package com.ecommerce.models;

public enum ApplicationUserPermission {
	CART_READ("cart:read"),
	CART_WRITE("cart:write"),
	PRODUCT_READ("product:read"),
	PrODUCT_WRITE("product:write");
	
	private final String permission;

	private ApplicationUserPermission(String permission) {
		this.permission = permission;
	}

	public String getPermission() {
		return permission;
	}
	
}
