package ro.msg.learning.shop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.msg.learning.shop.dto.response.ProductResponse;
import ro.msg.learning.shop.mapper.ProductMapper;
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
                .map(mapper::toDto)
                .toList();

        return new ResponseEntity<>(products, HttpStatus.OK);
    }
}
