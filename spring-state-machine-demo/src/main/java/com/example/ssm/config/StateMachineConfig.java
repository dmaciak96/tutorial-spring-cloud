package com.example.ssm.config;

import com.example.ssm.domain.PaymentEvent;
import com.example.ssm.domain.PaymentState;
import com.example.ssm.services.PaymentServiceImpl;
import java.util.EnumSet;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

@Slf4j
@Configuration
@EnableStateMachineFactory
public class StateMachineConfig extends StateMachineConfigurerAdapter<PaymentState, PaymentEvent> {

  @Override
  public void configure(StateMachineStateConfigurer<PaymentState, PaymentEvent> states)
      throws Exception {
    states
        .withStates()
        .initial(PaymentState.NEW)
        .states(EnumSet.allOf(PaymentState.class))
        .end(PaymentState.AUTH)
        .end(PaymentState.PRE_AUTH_ERROR)
        .end(PaymentState.AUTH_ERROR);
  }

  @Override
  public void configure(StateMachineTransitionConfigurer<PaymentState, PaymentEvent> transitions)
      throws Exception {
    transitions
        .withExternal()
        .source(PaymentState.NEW)
        .target(PaymentState.NEW)
        .event(PaymentEvent.PRE_AUTHORIZE)
        .action(preAuthAction())
        .guard(paymentIdGuard())
        .and()
        .withExternal()
        .source(PaymentState.NEW)
        .target(PaymentState.PRE_AUTH)
        .event(PaymentEvent.PRE_AUTH_APPROVE)
        .and()
        .withExternal()
        .source(PaymentState.NEW)
        .target(PaymentState.PRE_AUTH_ERROR)
        .event(PaymentEvent.PRE_AUTH_DECLINED)
        .and()
        .withExternal()
        .source(PaymentState.PRE_AUTH)
        .target(PaymentState.AUTH_ERROR)
        .event(PaymentEvent.AUTH_DECLINED)
        .and()
        .withExternal()
        .source(PaymentState.PRE_AUTH)
        .target(PaymentState.AUTH)
        .event(PaymentEvent.AUTH_APPROVED)
        .and()
        .withExternal()
        .source(PaymentState.PRE_AUTH)
        .target(PaymentState.PRE_AUTH)
        .event(PaymentEvent.AUTHORIZE)
        .action(authorizeAction())
        .guard(paymentIdGuard());
  }

  @Override
  public void configure(StateMachineConfigurationConfigurer<PaymentState, PaymentEvent> config)
      throws Exception {
    var listenerAdapter =
        new StateMachineListenerAdapter<PaymentState, PaymentEvent>() {
          @Override
          public void stateChanged(
              State<PaymentState, PaymentEvent> from, State<PaymentState, PaymentEvent> to) {
            log.info("State changed(from: {}, to:{})", from, to);
          }
        };
    config.withConfiguration().listener(listenerAdapter);
  }

  public Action<PaymentState, PaymentEvent> preAuthAction() {
    return stateContext -> {
      System.out.println("PreAuth was called!!!");
      if (new Random().nextInt(10) < 8) {
        System.out.println("Approved");
        stateContext
            .getStateMachine()
            .sendEvent(
                MessageBuilder.withPayload(PaymentEvent.PRE_AUTH_APPROVE)
                    .setHeader(
                        PaymentServiceImpl.PAYMENT_HEADER_ID,
                        stateContext.getMessageHeader(PaymentServiceImpl.PAYMENT_HEADER_ID))
                    .build());
      } else {
        System.out.println("Declined");
        stateContext
            .getStateMachine()
            .sendEvent(
                MessageBuilder.withPayload(PaymentEvent.PRE_AUTH_DECLINED)
                    .setHeader(
                        PaymentServiceImpl.PAYMENT_HEADER_ID,
                        stateContext.getMessageHeader(PaymentServiceImpl.PAYMENT_HEADER_ID))
                    .build());
      }
    };
  }

  public Action<PaymentState, PaymentEvent> authorizeAction() {
    return stateContext -> {
      System.out.println("Authorize was called!!!");
      if (new Random().nextInt(10) < 8) {
        System.out.println("Approved");
        stateContext
            .getStateMachine()
            .sendEvent(
                MessageBuilder.withPayload(PaymentEvent.AUTH_APPROVED)
                    .setHeader(
                        PaymentServiceImpl.PAYMENT_HEADER_ID,
                        stateContext.getMessageHeader(PaymentServiceImpl.PAYMENT_HEADER_ID))
                    .build());
      } else {
        System.out.println("Declined");
        stateContext
            .getStateMachine()
            .sendEvent(
                MessageBuilder.withPayload(PaymentEvent.AUTH_DECLINED)
                    .setHeader(
                        PaymentServiceImpl.PAYMENT_HEADER_ID,
                        stateContext.getMessageHeader(PaymentServiceImpl.PAYMENT_HEADER_ID))
                    .build());
      }
    };
  }

  public Guard<PaymentState, PaymentEvent> paymentIdGuard() {
    return context -> context.getMessageHeader(PaymentServiceImpl.PAYMENT_HEADER_ID) != null;
  }
}
