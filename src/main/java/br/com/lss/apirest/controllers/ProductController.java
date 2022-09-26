package br.com.lss.apirest.controllers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.lss.apirest.models.ProductModel;
import br.com.lss.apirest.repositories.ProductRepository;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
public class ProductController {
	
	@Autowired
	private ProductRepository productRepository;
	
	@GetMapping("/products")
	public ResponseEntity<List<ProductModel>> getAllProducts() {
		
		List<ProductModel> listProducts = productRepository.findAll();
		
		if (!listProducts.isEmpty()) {
			for (ProductModel product: listProducts) {
				UUID id = product.getIdProduct();
				product.add(linkTo(methodOn(ProductController.class).getOneProduct(id)).withSelfRel());
			}
		}
		
		return new ResponseEntity<List<ProductModel>>(listProducts, HttpStatus.OK);
	}

	@GetMapping("/products/{id}")
	public ResponseEntity<ProductModel> getOneProduct(@PathVariable("id") UUID id) {
		Optional<ProductModel> productO = productRepository.findById(id);
		if (!productO.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		productO.get().add(linkTo(methodOn(ProductController.class).getAllProducts()).withRel("Products List"));
		return new ResponseEntity<ProductModel>(productO.get(), HttpStatus.OK);
	}
	
	@PostMapping("/products")
	public ResponseEntity<ProductModel> saveProduct(@RequestBody @Valid ProductModel product) {
		return new ResponseEntity<ProductModel>(productRepository.save(product), HttpStatus.CREATED);
	}
	
	@DeleteMapping("/products/{id}")
	public ResponseEntity<?> deleteProduct(@PathVariable("id") UUID id) {
		Optional<ProductModel> productO = productRepository.findById(id);
		if (!productO.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		productRepository.delete(productO.get());
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PutMapping("/products/{id}")
	public ResponseEntity<ProductModel> updateProduct(@PathVariable("id") UUID id, @RequestBody @Valid ProductModel product) {
		Optional<ProductModel> productO = productRepository.findById(id);
		if (!productO.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		product.setIdProduct(productO.get().getIdProduct());
		return new ResponseEntity<ProductModel>(productRepository.save(product), HttpStatus.OK);
	}
}
