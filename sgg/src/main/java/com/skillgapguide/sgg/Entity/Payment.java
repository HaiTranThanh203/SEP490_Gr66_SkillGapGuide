package com.skillgapguide.sgg.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "Payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Integer paymentId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private Double amount;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date date;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "transaction_code")
    private String transactionCode;

    @Column(name = "qr_code_url")
    private String qrCodeUrl;

    @Column(nullable = false)
    private String status;
}

