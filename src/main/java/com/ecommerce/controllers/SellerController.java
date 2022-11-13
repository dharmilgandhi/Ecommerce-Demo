package com.ecommerce.controllers;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ecommerce.models.Product;
import com.ecommerce.models.User;
import com.ecommerce.repo.ProductRepo;

@RestController
@RequestMapping("/api/auth/seller")
public class SellerController {

	@Autowired
	private ProductRepo productRepo;
	

	@GetMapping("/product")
	public ResponseEntity<List<Product>> getAllProducts(Authentication auth){
		User seller = (User)auth.getPrincipal();
		int sellerId = seller.getUserId();
		
		
		List<Product> products = productRepo.findBySellerUserId(sellerId);
		return  new ResponseEntity<List<Product>>(products,HttpStatus.OK); 
	}
	
	@GetMapping("/product/{productId}")
	public ResponseEntity<Product> getProductById(Authentication auth,@PathVariable Integer productId){
		User seller = (User)auth.getPrincipal();
		int sellerId = seller.getUserId();
		
		Product product = productRepo.findById(productId).get();
		if(sellerId == product.getSeller().getUserId()) {
		return new ResponseEntity<Product>(product,HttpStatus.OK);
		}
		return new ResponseEntity<Product>(HttpStatus.NOT_FOUND);
	}
	
	@PostMapping("/product")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Object> addProduct(Authentication auth,@RequestBody Product product) throws IOException{
		User seller = (User)auth.getPrincipal();
		product.setSeller(seller);
		productRepo.save(product);
		Integer productId = product.getProductId();
		System.out.println(String.format("redirect:product/%d",productId));
		
		return ResponseEntity.status(HttpStatus.CREATED).location(URI.create("https://localhost:8080/api/auth/seller/product/"+productId)).build();
//		return String.format("redirect:/product/%d",productId);
//		return new ResponseEntity<Product>(product,HttpStatus.CREATED);
	}
	
	@PutMapping("/product")
	public ResponseEntity<Product> updateProduct(@RequestBody Product product){
		productRepo.save(product);
		return new ResponseEntity<Product>(product,HttpStatus.OK);
	}
	
	@DeleteMapping("/product/{productId}")
	public ResponseEntity<String> deleteProductById(Authentication auth,@PathVariable Integer productId){
		Integer sellerId = getProductById(auth,productId).getBody().getSeller().getUserId();
		
		User seller = (User)auth.getPrincipal();
		Integer sellerid = seller.getUserId();
		if(sellerId == sellerid) {
			productRepo.deleteById(productId);
			return new ResponseEntity<String>("Success",HttpStatus.OK);
		}
		return new ResponseEntity<String>("unauthorized",HttpStatus.NOT_FOUND);
	}
}
