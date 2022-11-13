package com.ecommerce;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
//import org.junit.jupiter.api.ClassOrderer.OrderAnnotation;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.ecommerce.models.Product;
import com.ecommerce.repo.CategoryRepo;
import com.ecommerce.repo.ProductRepo;
import com.ecommerce.repo.UserRepo;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.containsStringIgnoringCase;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestMethodOrder(OrderAnnotation.class)
@AutoConfigureMockMvc
class WingsEcommerceDemoApplicationTests {
	
	
	@Autowired
	MockMvc mvc;
	
	String c_u = "jack", s_u="apple", p="pass_word";
	
	@Autowired
	CategoryRepo categoryRepo;
	@Autowired
	UserRepo userRepo;
	@Autowired
	ProductRepo productRepo;
	
	@Test
	@Order(4)
	public void productSearchStatus() throws Exception{
		mvc.perform(get("/api/public/product/search").param("keyword","tablet"))
			.andExpect(status().is(200))
			.andExpect(jsonPath("$",notNullValue()));
	}
	
	@Test
	@Order(5)
	public void productSearchWithoutKeyword() throws Exception{
		mvc.perform(get("/api/public/product/search"))
			.andExpect(status().is(400));
	}
	
	@Test
	@Order(6)
	public void productSearchWithProductName() throws Exception{
		MvcResult res = mvc.perform(get("/api/public/product/search").param("keyword","tablet"))
			.andExpect(status().is(200))
			.andReturn();
		
		JSONArray arr = (JSONArray) new JSONParser().parse(res.getResponse().getContentAsString());
		assert (arr.size() >0);
		for(Object obj:arr) {
			assert (((JSONObject)obj).get("productName").toString().toLowerCase().contains("tablet"));
		}
	}
	
	@Test
	@Order(7)
	public void productSearchWithCategoryName() throws Exception{
		MvcResult res = mvc.perform(get("/api/public/product/search").param("keyword","medicine"))
			.andExpect(status().is(200))
			.andReturn();
		
		JSONArray arr = (JSONArray) new JSONParser().parse(res.getResponse().getContentAsString());
		assert (arr.size() >0);
		for(Object obj:arr) {
			assert (((JSONObject)((JSONObject)obj).get("category")).get("categoryName").toString().toLowerCase().contains("medicine"));
		}
	}
	
	@Test
	@Order(8)
	public void consumerAuthEndpoint() throws Exception{
		mvc.perform(get("/api/auth/consumer/cart")).andExpect(status().is(401));
	}
	
	@Test
	@Order(9)
	public void sellerAuthEndpoint() throws Exception{
		mvc.perform(get("/api/auth/seller/product")).andExpect(status().is(401));
	}
	
	@Test
	@Order(10)
	public void consumerWithBadCreds() throws Exception{
		mvc.perform(post("/api/public/login").contentType(MediaType.APPLICATION_JSON)
			.content(getJSONCreds(c_u,"password"))).andExpect(status().is(401));
	}

	private String getJSONCreds(String u, String p) {
		// TODO Auto-generated method stub
		Map<String,String> map = new HashMap<String,String>();
		map.put("username", u);
		map.put("password", p);
		
		return new JSONObject(map).toJSONString();
	}
	
	public MockHttpServletResponse loginHelper(String u, String p) throws Exception{
		return mvc
				.perform(post("/api/public/login").contentType(MediaType.APPLICATION_JSON).content(getJSONCreds(u,p)))
				.andReturn().getResponse();
	}
	
	@Test
	@Order(11)
	public void consumerLoginWithValidCreds() throws Exception{
		assertEquals(200,loginHelper(c_u,p).getStatus());
		System.out.println(loginHelper(c_u,p).getContentAsString());
		assertNotEquals("",loginHelper(c_u,p).getContentAsString());
	}
	
	@Test
	@Order(12)
	public void consumerGetCartWithValidJWT() throws Exception{
		mvc.perform(get("/api/auth/consumer/cart").header("Authorization","Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqYWNrIn0.ac9H0g9cd16-XPzi_Iab3zx4WeiFuAs0uUEiRw8OiSk"))
				.andExpect(status().is(200)).andExpect(jsonPath("$.cartId",is(not(equalTo("")))))
				.andExpect(jsonPath("$.cartProducts[0].quantity",is(2)))
				.andExpect(jsonPath("$.cartProducts[0].product.productName",containsStringIgnoringCase("Crocin pain relief tablet")))
				.andExpect(jsonPath("$.cartProducts[0].product.category.categoryName",is("Medicines")));
	}
	
	@Test
	@Order(13)
	public void sellerApiWithConsumerJWT() throws Exception{
		mvc.perform(get("/api/auth/seller/product").header("Authorization","Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqYWNrIn0.ac9H0g9cd16-XPzi_Iab3zx4WeiFuAs0uUEiRw8OiSk"))
		.andExpect(status().is(403));
	}
	
	@Test
	@Order(14)
	public void sellerLoginWithValidCreds() throws Exception{
		assertEquals(200,loginHelper(s_u,p).getStatus());
//		System.out.println(loginHelper(c_u,p).getContentAsString());
		assertNotEquals("",loginHelper(s_u,p).getContentAsString());
	}
	
	@Test
	@Order(15)
	public void sellerGetProductWithValidJWT() throws Exception{
		mvc.perform(get("/api/auth/seller/product").header("Authorization","Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhcHBsZSJ9.rXrdmcFyiDxcn0G4uOQw02wObInb40QLatfewgUp4EU"))
			.andExpect(status().is(200))
			.andExpect(jsonPath("$.[0].productId",is(not(equalTo("")))))
			.andExpect(jsonPath("$.[0].productName",containsStringIgnoringCase("Apple iPad 10.2 8th Gen WiFi iOS Tablet")))
			.andExpect(jsonPath("$.[0].category.categoryName",is("Electronics")));
	}
	
	@Test
	@Order(16)
	public void consumerApiWithSellerJWT() throws Exception{
		mvc.perform(get("/api/auth/consumer/cart").header("Authorization","Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhcHBsZSJ9.rXrdmcFyiDxcn0G4uOQw02wObInb40QLatfewgUp4EU"))
		.andExpect(status().is(403));
	}
	
	public JSONObject getProduct(int id, String name, Double price, int cId, String cName) {
		Map<String,String> mapC = new HashMap<String,String>();
		mapC.put("categoryId",String.valueOf(cId));
		mapC.put("categoryName", cName);
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("productId", id);
		map.put("productName", name);
		map.put("price", String.valueOf(price));
		map.put("category", mapC);
		return new JSONObject(map);
		
	}
	
	static String createdURI;
	
	@Test
	@Order(17)
	public void sellerAddNewProduct() throws Exception{
		createdURI = mvc
				 .perform(post("/api/auth/seller/product").header("Authorization", "Bearer "+loginHelper(s_u,p).getContentAsString())
						 .contentType(MediaType.APPLICATION_JSON)
						 .content(getProduct(3,"iPhone 11",49000.0,2,"Electronics").toJSONString()))
				 		 .andExpect(status().is(201)).andReturn().getResponse().getRedirectedUrl();
	}
	
	
@Test
@Order(18)
public void sellerCheckAddedNewProduct() throws Exception {
System.out.println("URI is-------------------"+createdURI);
mvc.perform(get(new URL(createdURI).getPath()).header("Authorization", "Bearer "+loginHelper(s_u,p).getContentAsString())) 
.andExpect(status().is(200))
.andExpect(jsonPath("$.productId", is(3)))
.andExpect(jsonPath("$.productName", is("iPhone 11")))
.andExpect(jsonPath("$.price", is(49000.0))) 
.andExpect(jsonPath("$.category.categoryName", is("Electronics")));

mvc.perform(get("/api/auth/seller/product").header("Authorization", "Bearer "+loginHelper(s_u,p).getContentAsString()))
.andExpect(status().is(200)).andExpect(content().string(containsString("iPhone 11")));

}



@Test
@Order (19)
public void sellerCheckProductFromAnotherSeller() throws Exception {


System.out.println(new URL(createdURI).getPath());
mvc.perform(get(new URL(createdURI).getPath()).header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJnbGF4byJ9.hfUajp7WrJyIRiRuYUCM31BsW5n2AC3tfqFZ_9WD3_0"))
.andExpect(status().is(404));

mvc.perform(get("/api/auth/seller/product").header("Authorization", "Bearer "+loginHelper("glaxo",p).getContentAsString()))
.andExpect(status().is(200))
.andExpect(content()
		.string (not (containsString("iPhone 11"))));

}

@Test
@Order (20)
public void sellerUpdateProduct() throws Exception {

String[] arr = createdURI.split("/");
for(String s:arr) {
	System.out.println("Content of array"+s);
}

mvc.perform(put("/api/auth/seller/product").header("Authorization", "Bearer "+loginHelper(s_u,p).getContentAsString())
.contentType (MediaType.APPLICATION_JSON)
.content(getProduct (Integer.valueOf(arr[arr.length-1]), "iPhone 12", 98000.0, 2, "Electronics")
.toJSONString()))
.andExpect(status().is(200));

mvc.perform(get(new URL(createdURI).getPath()).header("Authorization", "Bearer "+loginHelper(s_u,p).getContentAsString())) .andExpect(status().is(200))
.andExpect(jsonPath("$.productId", is(Integer.valueOf(arr[arr.length-1]))))
.andExpect(jsonPath("$.productName", is("iPhone 12"))).andExpect(jsonPath("$.price", is(98000.0))) .andExpect(jsonPath("$.category.categoryName", is("Electronics")));

mvc.perform(get("/api/auth/seller/product").header("Authorization", "Bearer "+loginHelper(s_u,p).getContentAsString())) .andExpect(status().is(200)).andExpect(content().string (containsString("iPhone 12")));
}



@Test
@Order(22)
public void consumerAddProductToCart() throws Exception {

String jstring = "{'productId':3,'category':{'categoryName':'Electronics','categoryId':2},'price':98000.0,'productName:'iPhone 12'}";
String[] arr = createdURI.split("/"); 
mvc.perform(get("/api/auth/consumer/cart").header("Authorization", "Bearer "+loginHelper(c_u,p).getContentAsString())) 
.andExpect(content().string(not(containsString("iPhone 12"))));

System.out.println(getProduct(Integer.valueOf(arr[arr.length-1]), "iPhone 12", 98000.0, 2, "Electronics").toJSONString());
mvc.perform(post("/api/auth/consumer/cart").header("Authorization", "Bearer "+loginHelper(c_u,p).getContentAsString()) 
		.contentType (MediaType.APPLICATION_JSON) 
		.content(jstring));
//		.content(getProduct (3, "iPhone 12", 98000.0, 2, "Electronics").toJSONString())) 
//		.andExpect(status().is(200));

mvc.perform(get("/api/auth/consumer/cart").header("Authorization", "Bearer "+loginHelper(c_u,p).getContentAsString())) 
.andExpect(content().string (containsString("iPhone 12")));
}



}
