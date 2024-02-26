package com.example.ssm.services;

import com.example.ssm.domain.Payment;
import com.example.ssm.domain.PaymentEvent;
import com.example.ssm.domain.PaymentState;
import org.springframework.statemachine.StateMachine;

public interface PaymentService {
  Payment newPayment(Payment payment);

  StateMachine<PaymentState, PaymentEvent> preAuth(Long paymentId);

  StateMachine<PaymentState, PaymentEvent> authorize(Long paymentId);

  StateMachine<PaymentState, PaymentEvent> declineAuth(Long paymentId);
}
