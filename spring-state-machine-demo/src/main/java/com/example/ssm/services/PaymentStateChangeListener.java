package com.example.ssm.services;

import com.example.ssm.domain.PaymentEvent;
import com.example.ssm.domain.PaymentState;
import com.example.ssm.repository.PaymentRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentStateChangeListener
    extends StateMachineInterceptorAdapter<PaymentState, PaymentEvent> {
  private final PaymentRepository paymentRepository;

  @Override
  public void preStateChange(
      State<PaymentState, PaymentEvent> state,
      Message<PaymentEvent> message,
      Transition<PaymentState, PaymentEvent> transition,
      StateMachine<PaymentState, PaymentEvent> stateMachine,
      StateMachine<PaymentState, PaymentEvent> rootStateMachine) {
    Optional.ofNullable(message)
        .ifPresent(
            msg -> {
              Optional.ofNullable(
                      (Long)
                          msg.getHeaders().getOrDefault(PaymentServiceImpl.PAYMENT_HEADER_ID, -1))
                  .ifPresent(
                      paymentId -> {
                        var payment = paymentRepository.findById(paymentId);
                        payment.get().setState(state.getId());
                        paymentRepository.save(payment.get());
                      });
            });
  }
}
