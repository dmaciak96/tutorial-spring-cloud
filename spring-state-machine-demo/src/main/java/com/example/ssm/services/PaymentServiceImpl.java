package com.example.ssm.services;

import com.example.ssm.domain.Payment;
import com.example.ssm.domain.PaymentEvent;
import com.example.ssm.domain.PaymentState;
import com.example.ssm.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
  public static final String PAYMENT_HEADER_ID = "payment_id";

  private final PaymentRepository paymentRepository;
  private final StateMachineFactory<PaymentState, PaymentEvent> stateMachineFactory;
  private final PaymentStateChangeListener paymentStateChangeListener;

  @Override
  public Payment newPayment(Payment payment) {
    payment.setState(PaymentState.NEW);
    return paymentRepository.save(payment);
  }

  @Transactional
  @Override
  public StateMachine<PaymentState, PaymentEvent> preAuth(Long paymentId) {
    var stateMachine = build(paymentId);
    sendEvent(paymentId, stateMachine, PaymentEvent.PRE_AUTH_APPROVE);
    return stateMachine;
  }

  @Transactional
  @Override
  public StateMachine<PaymentState, PaymentEvent> authorize(Long paymentId) {
    var stateMachine = build(paymentId);
    sendEvent(paymentId, stateMachine, PaymentEvent.AUTH_APPROVED);
    return stateMachine;
  }

  @Transactional
  @Override
  public StateMachine<PaymentState, PaymentEvent> declineAuth(Long paymentId) {
    var stateMachine = build(paymentId);
    sendEvent(paymentId, stateMachine, PaymentEvent.AUTH_DECLINED);
    return stateMachine;
  }

  private void sendEvent(
      Long paymentId, StateMachine<PaymentState, PaymentEvent> stateMachine, PaymentEvent event) {
    var message = MessageBuilder.withPayload(event).setHeader(PAYMENT_HEADER_ID, paymentId).build();
    stateMachine.sendEvent(message);
  }

  private StateMachine<PaymentState, PaymentEvent> build(Long paymentId) {
    var payment = paymentRepository.findById(paymentId);
    var stateMachine = stateMachineFactory.getStateMachine(Long.toString(payment.get().getId()));
    stateMachine.stop();
    stateMachine
        .getStateMachineAccessor()
        .doWithAllRegions(
            stateMachineAccess -> {
              stateMachineAccess.addStateMachineInterceptor(paymentStateChangeListener);
              stateMachineAccess.resetStateMachine(
                  new DefaultStateMachineContext<>(payment.get().getState(), null, null, null));
            });
    stateMachine.start();
    return stateMachine;
  }
}
