package com.example.ssm.services;

import static org.junit.jupiter.api.Assertions.*;

import com.example.ssm.domain.Payment;
import com.example.ssm.domain.PaymentState;
import com.example.ssm.repository.PaymentRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PaymentServiceImplTest {

  @Autowired PaymentService paymentService;
  @Autowired PaymentRepository paymentRepository;
  Payment payment;

  @BeforeEach
  void setup() {
    this.payment = Payment.builder().amount(new BigDecimal("12.99")).build();
  }

  @Test
  void preAuthorizationTest() {
    var savedPayment = paymentService.newPayment(payment);
    paymentService.preAuth(savedPayment.getId());

    var result = paymentRepository.findById(savedPayment.getId());
    var state = result.get().getState();
    assertTrue(state == PaymentState.PRE_AUTH || state == PaymentState.PRE_AUTH_ERROR);
  }

  @Test
  void authorizationTest() {
    var savedPayment = paymentService.newPayment(payment);
    paymentService.preAuth(savedPayment.getId());

    var preAuthorizedPayment = paymentRepository.findById(savedPayment.getId());
    var preAuthState = preAuthorizedPayment.get().getState();
    if(preAuthState == PaymentState.PRE_AUTH) {
      paymentService.authorize(savedPayment.getId());
      var result = paymentRepository.findById(savedPayment.getId());
      var state = result.get().getState();
      assertTrue(state == PaymentState.AUTH || state == PaymentState.AUTH_ERROR);
    }
  }
}
