package com.ecommerce.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.config.JwtUtil;
import com.ecommerce.models.Cart;
import com.ecommerce.models.CartProduct;
import com.ecommerce.models.Category;
import com.ecommerce.models.Product;
import com.ecommerce.models.User;
import com.ecommerce.repo.CartProductRepo;
import com.ecommerce.repo.CartRepo;
import com.ecommerce.repo.CategoryRepo;
import com.ecommerce.repo.ProductRepo;


@RestController
@RequestMapping("/api/auth/consumer")
public class ConsumerController {
	
	@Autowired
	private CartRepo cartRepo;
	@Autowired
	private ProductRepo productRepo;
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private CartProductRepo cartProductRepo;

	@Autowired 
	private CategoryRepo categoryRepo;
	
//	String username = jwtUtil.getUser(null)
	@GetMapping("/cart")
	public ResponseEntity<?> getCart(Authentication auth) {
		String username = auth.getName();
		Optional<Cart> userCart = cartRepo.findByUserUsername(username);
		return new ResponseEntity<>(userCart,HttpStatus.OK);
	}
	
	@PostMapping("/cart")
	public ResponseEntity<Object> postCart(Authentication auth,@RequestBody Product product){
		
		
		User user = (User) auth.getPrincipal();
		String username =user.getUsername();
		Integer userId = user.getUserId();
		Integer productId = product.getProductId();
		
		product.setSeller(user);
		productRepo.save(product);
		Cart cart = cartRepo.findByUserUsername(username).get();
		
		CartProduct cart_product = new CartProduct();
		cart_product.setProduct(product);
		cart_product.setCart(cart);
		cartProductRepo.save(cart_product);
		
		
		Double totalAmount = cart.getTotalAmount()+ (product.getPrice()*cart_product.getQuantity());
		System.out.println(totalAmount);
		
		List<CartProduct> cartproducts = cart.getCartProducts();
		cartproducts.add(cart_product);
		cart.setUser(user);
		cart.setCartProducts(cartproducts);
		cart.setTotalAmount(totalAmount);
		System.out.println(cart.getTotalAmount());
		
		cartRepo.save(cart);
//		productRepo.save(product);		
		return new ResponseEntity<>(product,HttpStatus.OK); 
	}
}
