package guru.sfg.beer.order.service.services;

import guru.sfg.beer.order.service.config.JmsConfig;
import guru.sfg.beer.order.service.domain.BeerOrder;
import guru.sfg.beer.order.service.domain.BeerOrderEventEnum;
import guru.sfg.beer.order.service.domain.BeerOrderStatusEnum;
import guru.sfg.beer.order.service.domain.BeerOrderValidationResultEvent;
import guru.sfg.beer.order.service.repositories.BeerOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BeerOrderManagerImpl implements
    BeerOrderManager {

  public static final String BEER_ORDER_ID_HEADER_NAME = "beer-order-id";
  public static final String BEER_ORDER_CUSTOMER_ID_HEADER_NAME = "beer-order-customer-id";


  private final StateMachineFactory<BeerOrderStatusEnum, BeerOrderEventEnum> stateMachineFactory;
  private final BeerOrderRepository beerOrderRepository;
  private final BeerOrderStatusChangeInterceptor beerOrderStatusChangeInterceptor;
  private final JmsTemplate jmsTemplate;

  @Override
  @Transactional
  public BeerOrder newBeerOrder(BeerOrder beerOrder) {
    beerOrder.setId(null);
    beerOrder.setOrderStatus(BeerOrderStatusEnum.NEW);
    var savedOrder = beerOrderRepository.save(beerOrder);
    sendBeerOrderEvent(savedOrder, BeerOrderEventEnum.VALIDATE_ORDER);
    return savedOrder;
  }

  @JmsListener(destination = JmsConfig.VALIDATE_ORDER_RESULT_QUEUE_NAME)
  public void listenOrderValidationResult(BeerOrderValidationResultEvent event) {
    log.debug("Beer order validation result event was received");
    var beerOrder = beerOrderRepository.findOneById(event.beerOrderId);
    if (event.isValid) {
      sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.VALIDATION_PASSED);
      sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.ALLOCATED_ORDER);
    } else {
      sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.VALIDATION_FAILED);
    }
  }

  private void sendBeerOrderEvent(BeerOrder beerOrder, BeerOrderEventEnum event) {
    var stateMachine = build(beerOrder);
    var msg = MessageBuilder.withPayload(event)
        .setHeader(BEER_ORDER_ID_HEADER_NAME, beerOrder.getId())
        .setHeader(BEER_ORDER_CUSTOMER_ID_HEADER_NAME, beerOrder.getCustomer().getId()).build();
    stateMachine.sendEvent(msg);
  }

  private StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> build(BeerOrder beerOrder) {
    var stateMachine = stateMachineFactory.getStateMachine(beerOrder.getId());
    stateMachine.stop();
    stateMachine.getStateMachineAccessor()
        .doWithAllRegions(sma -> {
          sma.addStateMachineInterceptor(beerOrderStatusChangeInterceptor);
          sma.resetStateMachine(
              new DefaultStateMachineContext<>(beerOrder.getOrderStatus(), null, null, null));
        });
    stateMachine.start();
    return stateMachine;
  }
}
