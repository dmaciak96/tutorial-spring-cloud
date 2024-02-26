package com.example.jmsexplore.listener;

import com.example.jmsexplore.config.JmsConfig;
import com.example.jmsexplore.model.HelloWorldMessage;
import jakarta.jms.Message;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class HelloMessageListener {

  @JmsListener(destination = JmsConfig.MY_QUEUE)
  public void listen(
      @Payload HelloWorldMessage helloWorldMessage,
      @Headers MessageHeaders headers,
      Message message) {
    System.out.println("I Got a Message!!!");
    System.out.println(helloWorldMessage);
  }
}
