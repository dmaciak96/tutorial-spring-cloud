package guru.sfg.beer.order.service.config;

import guru.sfg.beer.order.service.domain.AllocateOrderRequest;
import guru.sfg.beer.order.service.domain.BeerOrderEventEnum;
import guru.sfg.beer.order.service.domain.BeerOrderStatusEnum;
import guru.sfg.beer.order.service.repositories.BeerOrderRepository;
import guru.sfg.beer.order.service.services.BeerOrderManagerImpl;
import guru.sfg.beer.order.service.web.mappers.BeerOrderMapper;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AllocateOrderAction implements Action<BeerOrderStatusEnum, BeerOrderEventEnum> {

  private final JmsTemplate jmsTemplate;
  private final BeerOrderRepository beerOrderRepository;
  private final BeerOrderMapper beerOrderMapper;

  @Override
  public void execute(StateContext<BeerOrderStatusEnum, BeerOrderEventEnum> stateContext) {
    var orderId = (UUID) stateContext.getMessage().getHeaders()
        .get(BeerOrderManagerImpl.BEER_ORDER_ID_HEADER_NAME);
    var beerOrder = beerOrderRepository.findOneById(orderId);

    jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_QUEUE_NAME,
        new AllocateOrderRequest(beerOrderMapper.beerOrderToDto(beerOrder)));
    log.debug("Allocation beer order event was sent");
  }
}
