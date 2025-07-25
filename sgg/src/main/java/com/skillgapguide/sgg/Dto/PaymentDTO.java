package com.skillgapguide.sgg.Dto;

import lombok.Data;

import java.util.Date;

@Data
public class PaymentDTO {
    private Integer paymentId;
    private String username;
    private Double amount;
    private Date date;
    private String paymentMethod;
    private String transactionCode;
    private String qrCodeUrl;
    private String status;
}
