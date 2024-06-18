package com.example.lab02.controller;

import com.example.lab02.model.Product;
import com.example.lab02.service.CategoryService;
import com.example.lab02.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService; // Đảm bảo bạn đã inject CategoryService
// Display a list of all products
    @GetMapping
    public String showProductList(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "/products/products-list";
    }
    // For adding a new product
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories()); // Load categories
        return "/products/add-product";
    }
    // Process the form for adding a new product
    @PostMapping("/add")
    public String addProduct(@Valid Product product,
                             @RequestParam("imageFile") List<MultipartFile> imageFile,
                             BindingResult result,
                             Model model) {
        if (result.hasErrors()) {
            return "/products/add-product";
        }
//        if (!imageFile.isEmpty()) {
//            String imageUrl = productService.saveImage(imageFile);
//            product.setImage(imageUrl);
//
//        }

        //kiểu mảng
//        if (imageFile != null && imageFile.length > 0) {
//            List<String> imageUrls = new ArrayList<>();
//            for (MultipartFile image : imageFile) {
//                String imageUrl = productService.saveImage(image);
//                if (imageUrl != null) {
//                    imageUrls.add(imageUrl);
//                } else {
//                    model.addAttribute("uploadError", "Image upload failed for " + image.getOriginalFilename());
//                    return "/products/add-product";
//                }
//            }
//            // Assuming you have a method to set multiple images
//            product.setImage(imageUrls);
//        }


        if (imageFile != null && !imageFile.isEmpty()) {
            List<String> imageUrls = new ArrayList<>();
            for (MultipartFile image : imageFile) {
                String imageUrl = productService.saveImage(image);
                imageUrls.add(imageUrl);
            }
            product.setImage(imageUrls); // Set danh sách URL ảnh cho sản phẩm
        }


        productService.addProduct(product);
        return "redirect:/products";
    }

    private String saveImageStatic(MultipartFile image) throws IOException {
        File saveFile = new ClassPathResource("static/images").getFile();
        String fileName = UUID.randomUUID()+ "." + StringUtils.getFilenameExtension(image.getOriginalFilename());
        Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + fileName);
        Files.copy(image.getInputStream(), path);
        return fileName;
    }

    // For editing a product
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories()); //Load categories
        return "/products/update-product";
    }
    // Process the form for updating a product
    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable Long id, @Valid Product product,
                                @RequestParam("imageFile") List<MultipartFile> imageFile,
                                BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            product.setId(id); // set id to keep it in the form in case of errors
            model.addAttribute("categories", categoryService.getAllCategories());
            return "/products/update-product";
        }
//        if(!imageFile.isEmpty()){
//            String imageUrl = productService.saveImage(imageFile);
//            product.setImage(imageUrl);
//        }
//        if (imageFile != null && imageFile.length > 0) {
//            List<String> imageUrls = new ArrayList<>();
//            for (MultipartFile image : imageFile) {
//                String imageUrl = productService.saveImage(image);
//                if (imageUrl != null) {
//                    imageUrls.add(imageUrl);
//                } else {
//                    model.addAttribute("uploadError", "Image upload failed for " + image.getOriginalFilename());
//                    return "/products/update-product";
//                }
//            }
//            product.setImage(imageUrls);
//        }

        if (imageFile != null && !imageFile.isEmpty()) {
            List<String> imageUrls = new ArrayList<>();
            for (MultipartFile image : imageFile) {
                String imageUrl = productService.saveImage(image);
                imageUrls.add(imageUrl);
            }
            product.setImage(imageUrls); // Set danh sách URL ảnh cho sản phẩm
        }
        productService.updateProduct(product);
        return "redirect:/products";
    }
    // Handle request to delete a product
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
        return "redirect:/products";
    }

    @GetMapping("/{id}")
    public String detailProduct(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);

        return "/products/product-detail";
    }

}
