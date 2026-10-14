package ro.msg.learning.shop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.msg.learning.shop.dto.request.ProductRequest;
import ro.msg.learning.shop.dto.response.ProductResponse;
import ro.msg.learning.shop.mapper.ProductMapper;
import ro.msg.learning.shop.model.Product;
import ro.msg.learning.shop.service.ProductService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    private final ProductMapper mapper;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {

        List<ProductResponse> products = productService.getAllProducts().stream()
                .map(mapper::toResponseDto)
                .toList();

        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest productRequest) {

        Product product = mapper.toModelFromRequest(productRequest);
        Product createdProduct = productService.createProduct(product);
        ProductResponse response = mapper.toResponseDto(createdProduct);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
