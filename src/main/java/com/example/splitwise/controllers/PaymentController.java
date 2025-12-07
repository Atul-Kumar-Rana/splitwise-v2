package com.example.splitwise.controllers;

import com.example.splitwise.model.Transaction;
import com.example.splitwise.repo.TransactionRepo;
import com.example.splitwise.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final TransactionRepo transactionRepo;
    public PaymentController(PaymentService paymentService, TransactionRepo transactionRepo){ this.paymentService = paymentService;
        this.transactionRepo = transactionRepo;
    }

    public static class PayDto {
        public Long debitorId;
        public Long payerUserId;
        public BigDecimal amount;
    }

    // Pay a split (partial or full)
    @PostMapping("/pay")
    public ResponseEntity<Transaction> pay(@RequestBody PayDto dto){
        if (dto.debitorId == null || dto.payerUserId == null || dto.amount == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            Transaction tx = paymentService.payDebitor(dto.debitorId, dto.payerUserId, dto.amount);
            return ResponseEntity.status(HttpStatus.CREATED).body(tx);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception ex) {
            // generic fallback (e.g., optimistic lock)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
