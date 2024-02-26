package guru.springframework.msscbeerservice.services.brewing;

import static guru.springframework.msscbeerservice.config.JmsConfig.BREWING_REQUEST_QUEUE_NAME;

import guru.springframework.msscbeerservice.domain.Beer;
import guru.springframework.msscbeerservice.events.BrewBeerEvent;
import guru.springframework.msscbeerservice.repositories.BeerRepository;
import guru.springframework.msscbeerservice.services.inventory.BeerInventoryService;
import guru.springframework.msscbeerservice.web.mappers.BeerMapper;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrewingService {

  private final BeerRepository beerRepository;
  private final BeerInventoryService beerInventoryService;
  private final JmsTemplate jmsTemplate;
  private final BeerMapper beerMapper;

  @Scheduled(fixedRate = 5000)
  public void checkForLowInventory() {
    beerRepository.findAll().forEach(this::sendInventoryEventIfMinOnHandIsGreater);
  }

  private void sendInventoryEventIfMinOnHandIsGreater(Beer beer) {
    var inventoryOnHand = beerInventoryService.getOnHandInventory(UUID.fromString(beer.getId()));
    log.debug("Min on hand is: {}", beer.getMinOnHand());
    log.debug("Inventory on hand is: {}", inventoryOnHand);

    if (beer.getMinOnHand() >= inventoryOnHand) {
      jmsTemplate.convertAndSend(BREWING_REQUEST_QUEUE_NAME,
          new BrewBeerEvent(beerMapper.beerToBeerDto(beer)));
    }
  }
}
