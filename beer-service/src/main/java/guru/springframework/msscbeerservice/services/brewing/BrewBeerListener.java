package guru.springframework.msscbeerservice.services.brewing;

import static guru.springframework.msscbeerservice.config.JmsConfig.NEW_INVENTORY_QUEUE_NAME;

import guru.springframework.msscbeerservice.config.JmsConfig;
import guru.springframework.msscbeerservice.events.BrewBeerEvent;
import guru.springframework.msscbeerservice.events.NewInventoryEvent;
import guru.springframework.msscbeerservice.repositories.BeerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BrewBeerListener {

  private final BeerRepository beerRepository;
  private final JmsTemplate jmsTemplate;

  @JmsListener(destination = JmsConfig.BREWING_REQUEST_QUEUE_NAME)
  public void listenBrewBeerEvent(BrewBeerEvent event) {
    var dto = event.getBeerDto();
    var beer = beerRepository.getOne(dto.getId());
    dto.setQuantityOnHand(beer.getQuantityToBrew());

    var inventoryEvent = new NewInventoryEvent(dto);
    jmsTemplate.convertAndSend(NEW_INVENTORY_QUEUE_NAME, inventoryEvent);
  }
}
