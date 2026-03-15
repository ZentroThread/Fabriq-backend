package com.example.FabriqBackend.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/v1/payment")
public class PaymentController {

    @Value("${payhere.merchant.id}")
    private String merchantId;

    @Value("${payhere.merchant.secret}")
    private String merchantSecret;

    @PostMapping("/create")
    public Map<String, String> createPayment(@RequestBody Map<String, String> req) throws Exception {

        String orderId = req.get("orderId");
        String amount = req.get("amount");
        String currency = "LKR";

        String hash = generateHash(merchantId, orderId, amount, currency, merchantSecret);

        Map<String, String> response = new HashMap<>();
        response.put("merchant_id", merchantId);
        response.put("order_id", orderId);
        response.put("amount", amount);
        response.put("currency", currency);
        response.put("hash", hash);

        return response;
    }

    private String generateHash(String merchantId, String orderId,
                                String amount, String currency,
                                String merchantSecret) throws Exception {

        String secretHash = md5(merchantSecret).toUpperCase();
        String raw = merchantId + orderId + amount + currency + secretHash;

        return md5(raw).toUpperCase();
    }

    private String md5(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(input.getBytes());

        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }
    @PostMapping("/notify")
    public ResponseEntity<?> paymentNotify(HttpServletRequest request){

        String status = request.getParameter("status_code");

        if("2".equals(status)){
            System.out.println("Payment successful");
        }

        return ResponseEntity.ok().build();
    }
}