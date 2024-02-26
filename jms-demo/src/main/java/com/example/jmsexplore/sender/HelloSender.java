package com.example.jmsexplore.sender;

import com.example.jmsexplore.config.JmsConfig;
import com.example.jmsexplore.model.HelloWorldMessage;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HelloSender {

  private final JmsTemplate jmsTemplate;

  @Scheduled(fixedRate = 2000)
  public void sendMessage() {
    System.out.println("Sending a message...");
    jmsTemplate.convertAndSend(JmsConfig.MY_QUEUE, HelloWorldMessage.builder()
        .id(UUID.randomUUID())
        .message("Hello World!")
        .build());
    System.out.println("Message sent!");
  }
}
