package guru.sfg.beer.order.service.config;

import guru.sfg.beer.order.service.domain.BeerOrderEventEnum;
import guru.sfg.beer.order.service.domain.BeerOrderStatusEnum;
import guru.sfg.beer.order.service.domain.ValidateBeerOrderRequest;
import guru.sfg.beer.order.service.services.BeerOrderManagerImpl;
import guru.sfg.beer.order.service.services.BeerOrderService;
import java.util.EnumSet;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@Slf4j
@Configuration
@EnableStateMachineFactory
@RequiredArgsConstructor
public class BeerOrderStateMachineConfig extends
    StateMachineConfigurerAdapter<BeerOrderStatusEnum, BeerOrderEventEnum> {

  private final JmsTemplate jmsTemplate;
  private final BeerOrderService beerOrderService;
  private final AllocateOrderAction allocateOrderAction;

  @Override
  public void configure(StateMachineStateConfigurer<BeerOrderStatusEnum, BeerOrderEventEnum> states)
      throws Exception {
    states.withStates()
        .initial(BeerOrderStatusEnum.NEW)
        .states(EnumSet.allOf(BeerOrderStatusEnum.class))
        .end(BeerOrderStatusEnum.PICKED_UP)
        .end(BeerOrderStatusEnum.DELIVERED)
        .end(BeerOrderStatusEnum.DELIVERY_EXCEPTION)
        .end(BeerOrderStatusEnum.VALIDATION_EXCEPTION)
        .end(BeerOrderStatusEnum.ALLOCATION_EXCEPTION);
  }

  @Override
  public void configure(
      StateMachineTransitionConfigurer<BeerOrderStatusEnum, BeerOrderEventEnum> transitions)
      throws Exception {
    transitions.withExternal()
        .source(BeerOrderStatusEnum.NEW)
        .target(BeerOrderStatusEnum.VALIDATION_PENDING)
        .event(BeerOrderEventEnum.VALIDATE_ORDER)
        .action(validateOrderAction())
        .and()
        .withExternal()
        .source(BeerOrderStatusEnum.NEW)
        .target(BeerOrderStatusEnum.VALIDATED)
        .event(BeerOrderEventEnum.VALIDATION_PASSED)
        .and()
        .withExternal()
        .source(BeerOrderStatusEnum.NEW)
        .target(BeerOrderStatusEnum.VALIDATION_EXCEPTION)
        .event(BeerOrderEventEnum.VALIDATION_FAILED)
        .and().withExternal()
        .source(BeerOrderStatusEnum.VALIDATED).target(BeerOrderStatusEnum.ALLOCATION_PENDING)
        .event(BeerOrderEventEnum.VALIDATE_ORDER)
        .action(allocateOrderAction);
  }


  public Action<BeerOrderStatusEnum, BeerOrderEventEnum> validateOrderAction() {
    return stateContext -> {
      var orderId = (UUID) stateContext.getMessageHeader(
          BeerOrderManagerImpl.BEER_ORDER_ID_HEADER_NAME);
      var customerId = (UUID) stateContext.getMessageHeader(
          BeerOrderManagerImpl.BEER_ORDER_CUSTOMER_ID_HEADER_NAME);
      var beerOrder = beerOrderService.getOrderById(customerId, orderId);
      jmsTemplate.convertAndSend(JmsConfig.VALIDATE_ORDER_DESTINATION,
          new ValidateBeerOrderRequest(beerOrder));
    };
  }
}
