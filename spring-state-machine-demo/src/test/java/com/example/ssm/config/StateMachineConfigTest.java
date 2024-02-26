package com.example.ssm.config;

import static org.junit.jupiter.api.Assertions.*;

import com.example.ssm.domain.PaymentEvent;
import com.example.ssm.domain.PaymentState;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.config.StateMachineFactory;

@SpringBootTest
class StateMachineConfigTest {

  @Autowired StateMachineFactory<PaymentState, PaymentEvent> factory;

  @Test
  void testNewStateMachine() {
    var stateMachine = factory.getStateMachine(UUID.randomUUID());
    stateMachine.start();
    System.out.println(stateMachine.getState().toString());

    stateMachine.sendEvent(PaymentEvent.PRE_AUTHORIZE);
    System.out.println(stateMachine.getState().toString());

    stateMachine.sendEvent(PaymentEvent.PRE_AUTH_APPROVE);
    System.out.println(stateMachine.getState().toString());
  }
}
