package com.skillgapguide.sgg.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.skillgapguide.sgg.Dto.PaymentDTO;
import com.skillgapguide.sgg.Entity.Payment;
import com.skillgapguide.sgg.Entity.User;
import com.skillgapguide.sgg.Repository.PaymentRepository;
import com.skillgapguide.sgg.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    public PaymentDTO toPaymentDTO(Payment payment) {
        String username = getUsernameByUserId(payment.getUserId());
        PaymentDTO dto = new PaymentDTO();
        dto.setPaymentId(payment.getPaymentId());
        dto.setUsername(username);
        dto.setAmount(payment.getAmount());
        dto.setDate(payment.getDate());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setTransactionCode(payment.getTransactionCode());
        dto.setQrCodeUrl(payment.getQrCodeUrl());
        dto.setStatus(payment.getStatus());
        return dto;
    }

    private String getUsernameByUserId(Integer userId) {
        User user = userRepository.findById(userId).orElse(null);
        return user != null ? user.getFullName() : "Unknown User";
    }

    public Page<PaymentDTO> getPaymentsByStatus(String status, Pageable pageable) {
        return paymentRepository.findByStatus(status, pageable)
                .map(this::toPaymentDTO);
    }

    public Page<PaymentDTO> getPaymentsBetweenDates(Date startDate, Date endDate, Pageable pageable) {
        return paymentRepository.findPaymentBetweenDates(startDate, endDate, pageable)
                .map(this::toPaymentDTO);
    }

    public Page<PaymentDTO> findPaymentByUserId(Integer userId, Pageable pageable) {
        return paymentRepository.findPaymentByUserId(userId, pageable)
                .map(this::toPaymentDTO);
    }

    public Page<PaymentDTO> findAll(Pageable pageable) {
        return paymentRepository.findAll(pageable)
                .map(this::toPaymentDTO);
    }

    public List<PaymentDTO> findAllList() {
        return paymentRepository.findAll()
                .stream()
                .map(this::toPaymentDTO)
                .toList();
    }

    public PaymentDTO findPaymentByPaymentId(Integer paymentId) {
        Payment payment = paymentRepository.findPaymentByPaymentId(paymentId);
        return payment != null ? toPaymentDTO(payment) : null;
    }
    public String getPaymentQr(Integer type) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        double totalPrice;
        String description;
        if(type == 1){
            totalPrice = 100000;
            description = user.getFullName() + " dang ky goi co ban "+user.getUserId();
        } else if (type == 2) {
            totalPrice = 200000;
            description = user.getFullName() + " dang ky goi toan dien"+user.getUserId();
        } else {
            throw new IllegalStateException("Invalid payment type");
        }
        String accountName = "Tran Tuan Minh";
        String bankId = "MB";
        String accountNo = "1020052412003";
        String qrUrl = "https://img.vietqr.io/image/" + bankId + "-" + accountNo + "-print.png?amount=" + totalPrice + "&addInfo=" + description + "&accountName=" + accountName;
        return qrUrl;
    }
    public void confirmPaymentCassio(String json) throws Exception {
        try {
            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
            JsonArray jsonArray = (JsonArray) jsonObject.get("data");
            System.out.println("data: "+jsonArray);
            // loop array
            for (JsonElement element : jsonArray) {
                JsonObject transaction = element.getAsJsonObject();
                String description = transaction.get("description").getAsString();
                String amountStr = transaction.get("amount").getAsString();
                Double amount = Double.parseDouble(amountStr);
                Integer userId;
                System.out.println(description);
                Pattern pattern = Pattern.compile("(\\d+)$");
                Matcher matcher = pattern.matcher(description);
                if (matcher.find()) {
                    String userIdStr = matcher.group(1);
                    System.out.println("User ID: " + userIdStr);
                    userId = Integer.parseInt(userIdStr);
                }
                else {
                    System.out.println("No user ID found in description");
                    throw new IllegalStateException("No user ID found in description");
                }
                Payment payment = new Payment();
                    payment.setAmount(amount);
                    payment.setDate(new Date());
                    payment.setStatus("SUCCESS");
                    payment.setPaymentMethod("QRCode");
                    payment.setUserId(userId);
            }
        } catch (Exception e){
            System.out.println(e.getMessage()
            );
        }
    }

}
