package com.example.lab02.service;

import com.example.lab02.model.CartItem;
import com.example.lab02.model.Order;
import com.example.lab02.model.OrderDetail;
import com.example.lab02.repository.OrderDetailRepository;
import com.example.lab02.repository.OrderRepository;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private CartService cartService; // Assuming you have a CartService
    @Transactional
    public Order createOrder(String customerName,  String email, String phone, String address, String note, String paymentMethod,List<CartItem> cartItems) {
        Order order = new Order();
        order.setCustomerName(customerName);
        order.setEmail(email);
        order.setPhone(phone);
        order.setAddress(address);
        order.setNote(note);
        order.setPaymentMethod(paymentMethod);
        order = orderRepository.save(order);
        for (CartItem item : cartItems) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(item.getProduct());
            detail.setQuantity(item.getQuantity());
            orderDetailRepository.save(detail);
        }
        // Optionally clear the cart after order placement
        cartService.clearCart();
        return order;
    }
}
