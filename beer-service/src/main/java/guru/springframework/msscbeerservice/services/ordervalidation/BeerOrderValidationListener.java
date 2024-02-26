package guru.springframework.msscbeerservice.services.ordervalidation;

import guru.springframework.msscbeerservice.config.JmsConfig;
import guru.springframework.msscbeerservice.events.BeerOrderValidationEvent;
import guru.springframework.msscbeerservice.events.BeerOrderValidationResultEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BeerOrderValidationListener {

  private final JmsTemplate jmsTemplate;

  @JmsListener(destination = JmsConfig.VALIDATE_ORDER_QUEUE_NAME)
  public void listenValidateOrderEvent(BeerOrderValidationEvent event) {
    log.debug("Validating Beer Order: {}", event.getBeerOrderDto().getId());

    var isValidUpc = event.getBeerOrderDto().getBeerOrderLines().stream()
        .allMatch(orderLine -> orderLine.getUpc() != null);

    jmsTemplate.convertAndSend(JmsConfig.VALIDATE_ORDER_RESULT_QUEUE_NAME,
        BeerOrderValidationResultEvent.builder()
            .beerOrderId(event.getBeerOrderDto().getId())
            .isValid(isValidUpc)
            .build());
    log.debug("Event was sent to {}", JmsConfig.VALIDATE_ORDER_QUEUE_NAME);
  }
}
