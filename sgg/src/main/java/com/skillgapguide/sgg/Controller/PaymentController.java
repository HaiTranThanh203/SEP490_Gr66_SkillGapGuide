package com.skillgapguide.sgg.Controller;

import com.skillgapguide.sgg.Entity.Payment;
import com.skillgapguide.sgg.Entity.User;
import com.skillgapguide.sgg.Repository.PaymentRepository;
import com.skillgapguide.sgg.Repository.UserRepository;
import com.skillgapguide.sgg.Service.VnPayService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import com.skillgapguide.sgg.Response.EHttpStatus;
import com.skillgapguide.sgg.Response.Response;
import com.skillgapguide.sgg.Service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import jakarta.servlet.http.HttpServletResponse;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import jakarta.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class PaymentController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VnPayService vnPayService;
    @Autowired
    private PaymentRepository paymentRepo;
    private final PaymentService paymentService;
    @GetMapping("/filter")
    public Response<Page<Payment>> filterPaymentsByStatus(
            @RequestParam String status,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return new Response<>(EHttpStatus.OK,"Lọc Status bằng: " + status,paymentService.getPaymentsByStatus(status, PageRequest.of(pageNo - 1, pageSize)));
    }
    @GetMapping("/filter/byDatesRange")
    public Response<Page<Payment>> filterPaymentsByDatesRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return new Response<>(EHttpStatus.OK, "Lọc Payments giữa khoảng thời gian: " + startDate + " và " + endDate,
                paymentService.getPaymentsBetweenDates(startDate, endDate, PageRequest.of(pageNo - 1, pageSize)));
    }
    @GetMapping("/filter/byUserId")
    public Response<Page<Payment>> filterPaymentsByUserId(
            @RequestParam Integer userId,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return new Response<>(EHttpStatus.OK, "Lọc Payments của User ID: " + userId,
                paymentService.findPaymentByUserId(userId, PageRequest.of(pageNo - 1, pageSize)));
    }
    @GetMapping("/findAllPayments")
    public Response<Page<Payment>> findAllPayments(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return new Response<>(EHttpStatus.OK, "Lấy tất cả Payments",
                paymentService.findAll(PageRequest.of(pageNo - 1, pageSize)));
    }
    @GetMapping("/findPaymentByPaymentId")
    public Response<Payment> findPaymentByPaymentId(@RequestParam Integer paymentId) {
        Payment payment = paymentService.findPaymentByPaymentId(paymentId);
        if (payment != null) {
            return new Response<>(EHttpStatus.OK, "Tìm thấy Payment với ID: " + paymentId, payment);
        } else {
            return new Response<>(EHttpStatus.INVALID_INFORMATION, "Không tìm thấy Payment với ID: " + paymentId, null);
        }
    }
    @GetMapping("/export/excel")
    public void exportPaymentsToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        response.setHeader("Content-Disposition", "attachment; filename=payments_" + currentDate + ".xlsx");

    @GetMapping("/create")
    public ResponseEntity<Map<String, Object>> createPayment(@RequestParam double amount, HttpServletRequest request) throws Exception {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Payment> payments = paymentService.findAllList();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Payments");

        String orderInfo = "Thanh toán đơn hàng #" + System.currentTimeMillis();
        String ipAddr = request.getRemoteAddr();
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Payment ID");
        header.createCell(1).setCellValue("User ID");
        header.createCell(2).setCellValue("Amount");
        header.createCell(3).setCellValue("Date");
        header.createCell(4).setCellValue("Payment Method");
        header.createCell(5).setCellValue("Transaction Code");
        header.createCell(6).setCellValue("QR Code URL");
        header.createCell(7).setCellValue("Status");

        // 👉 Gửi userId vào service để nhúng vào TxnRef
        String url = vnPayService.createPaymentUrl(amount, orderInfo, ipAddr, user.getUserId());

        Map<String, Object> response = Map.of(
                "paymentUrl", url,
                "message", "Redirect to payment"
        );
        return ResponseEntity.ok(response);
    }
        int rowIdx = 1;
        for (Payment payment : payments) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(payment.getPaymentId());
            row.createCell(1).setCellValue(payment.getUserId());
            row.createCell(2).setCellValue(payment.getAmount());
            row.createCell(3).setCellValue(payment.getDate().toString());
            row.createCell(4).setCellValue(payment.getPaymentMethod());
            row.createCell(5).setCellValue(payment.getTransactionCode());
            row.createCell(6).setCellValue(payment.getQrCodeUrl());
            row.createCell(7).setCellValue(payment.getStatus());
        }

    @GetMapping("/vnpay-return")
    public ResponseEntity<Map<String, Object>> paymentReturn(@RequestParam Map<String, String> params) {
        try {
            String status = params.get("vnp_ResponseCode");
            String transactionNo = params.get("vnp_TransactionNo");
            double amount = Double.parseDouble(params.get("vnp_Amount")) / 100;
            String txnRef = params.get("vnp_TxnRef");
        workbook.write(response.getOutputStream());
        workbook.close();
    }
    @GetMapping("/export/pdf")
    public void exportPaymentsToPdf(HttpServletResponse response) throws IOException, DocumentException {
        response.setContentType("application/pdf");
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        response.setHeader("Content-Disposition", "attachment; filename=payments_" + currentDate + ".pdf");

            // Tách userId từ txnRef: ví dụ 123_172102938123
            Integer userId = Integer.parseInt(txnRef.split("_")[0]);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        List<Payment> payments = paymentService.findAllList();

            Payment payment = new Payment();
            payment.setAmount(amount);
            payment.setDate(new Date());
            payment.setStatus("00".equals(status) ? "SUCCESS" : "FAILED");
            payment.setTransactionCode(transactionNo);
            payment.setPaymentMethod("VNPAY");
            payment.setUserId(user.getUserId());
        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

            Payment saved = paymentRepo.save(payment);
        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{4, 2, 2, 3, 3, 3, 4, 2});

            return ResponseEntity.ok(Map.of(
                    "message", "00".equals(status) ? "Thanh toán thành công" : "Thanh toán thất bại",
                    "status", status,
                    "transactionCode", transactionNo,
                    "paymentId", saved.getPaymentId()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "message", "Lỗi khi xử lý callback từ VNPAY",
                    "error", e.getMessage()
            ));
        String[] headers = {"Payment ID", "User ID", "Amount", "Date", "Payment Method", "Transaction Code", "QR Code URL", "Status"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
        }

        for (Payment payment : payments) {
            table.addCell(String.valueOf(payment.getPaymentId()));
            table.addCell(String.valueOf(payment.getUserId()));
            table.addCell(String.valueOf(payment.getAmount()));
            table.addCell(payment.getDate().toString());
            table.addCell(payment.getPaymentMethod());
            table.addCell(payment.getTransactionCode());
            table.addCell(payment.getQrCodeUrl());
            table.addCell(payment.getStatus());
        }

        document.add(table);
        document.close();
    }
}
