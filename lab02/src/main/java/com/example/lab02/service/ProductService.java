package com.example.lab02.service;

import com.example.lab02.model.Product;
import com.example.lab02.repository.ProductRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    // Retrieve all products from the database
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    // Retrieve a product by its id
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    // Add a new product to the database
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }
    // Update an existing product
    public Product updateProduct(@NotNull Product product) {
        Product existingProduct = productRepository.findById(product.getId())
                .orElseThrow(() -> new IllegalStateException("Product with ID " + product.getId() + " does not exist."));
        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setImage(product.getImage());
        return productRepository.save(existingProduct);
    }
    // Delete a product by its id
    public void deleteProductById(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalStateException("Product with ID " + id + " does not exist.");
        }
        productRepository.deleteById(id);
    }

    public String saveImage(MultipartFile imageFile){
        try {
            String uploadDir ="src/main/resources/static/images/";
            String imageUrl = "/images/"+imageFile.getOriginalFilename();
            Path path = Paths.get(uploadDir + imageFile.getOriginalFilename());
            Files.createDirectories(path.getParent());
            Files.write(path, imageFile.getBytes());
            return imageUrl;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public List<String> saveImages(List<MultipartFile> imageFiles) {
        List<String> imageUrls = new ArrayList<>();
        try {
            String uploadDir = "src/main/resources/static/images/"; // Directory for image uploads
            for (MultipartFile imageFile : imageFiles) {
                String uniqueFileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                String imageUrl = "/images/" + uniqueFileName; // Constructing image URL
                Path path = Paths.get(uploadDir + uniqueFileName); // Creating a Path object representing the file location
                Files.createDirectories(path.getParent()); // Creating directories if they don't exist
                Files.write(path, imageFile.getBytes()); // Writing the file to disk
                imageUrls.add(imageUrl); // Adding the image URL to the list
            }
            return imageUrls; // Returning list of image URLs
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Returning null in case of exception
        }
    }
}
