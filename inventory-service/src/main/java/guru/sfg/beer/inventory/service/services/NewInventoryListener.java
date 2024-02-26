package guru.sfg.beer.inventory.service.services;

import guru.sfg.beer.inventory.service.config.JmsConfig;
import guru.sfg.beer.inventory.service.domain.BeerInventory;
import guru.sfg.beer.inventory.service.domain.NewInventoryEvent;
import guru.sfg.beer.inventory.service.repositories.BeerInventoryRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NewInventoryListener {

  private BeerInventoryRepository beerInventoryRepository;

  @JmsListener(destination = JmsConfig.NEW_INVENTORY_QUEUE_NAME)
  public void listenNewInventoryEvent(NewInventoryEvent event) {
    log.debug("Got inventory: {}", event.toString());

    beerInventoryRepository.save(BeerInventory.builder()
        .beerId(UUID.fromString(event.getBeerDto().getId()))
        .upc(event.getBeerDto().getUpc())
        .quantityOnHand(event.getBeerDto().getQuantityOnHand())
        .build());
  }

}
