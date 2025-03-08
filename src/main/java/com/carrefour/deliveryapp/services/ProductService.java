package com.carrefour.deliveryapp.services;

import com.carrefour.deliveryapp.dtos.ProductDTO;
import com.carrefour.deliveryapp.entities.Product;
import com.carrefour.deliveryapp.exceptions.InsufficientQtyException;
import com.carrefour.deliveryapp.exceptions.NotFoundException;
import com.carrefour.deliveryapp.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public List<Product> verifyAndUpdateStock(List<ProductDTO> productDTOS) {
        List<Long> productIds = productDTOS.stream().map(ProductDTO::getId).toList();
        List<Product> products = productRepository.findAllById(productIds);

        if (products.size() != productDTOS.size()) {
            throw new NotFoundException("Some products are not found in inventory.");
        }

        for (ProductDTO productDTO : productDTOS) {
            Product product = products.stream()
                    .filter(p -> p.getId().equals(productDTO.getId()))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Product with ID " + productDTO.getId() + " not found."));

            // Verifying if there are more products to add
            if (product.getStockQuantity() < productDTO.getQuantity()) {
                throw new InsufficientQtyException("Not enough stock for product: " + product.getId());
            }

            product.setStockQuantity(product.getStockQuantity() - productDTO.getQuantity()); // update product stock
        }

        productRepository.saveAll(products);
        return products;
    }
}

