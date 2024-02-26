package guru.springframework.msscbeerservice.services.inventory;

import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Profile("local-discovery")
@Service
@Primary
@RequiredArgsConstructor
public class BeerInventoryServiceFeignImpl implements BeerInventoryService {

  private final InventoryServiceFeignClient inventoryServiceFeignClient;
  @Override
  public Integer getOnHandInventory(UUID beerId) {
    log.debug("Calling inventory Service - BeerId: {}",beerId);
    var response = inventoryServiceFeignClient.getOnHandInventory(beerId);

    return Objects.requireNonNull(response.getBody())
        .getQuantityOnHand();
  }
}
