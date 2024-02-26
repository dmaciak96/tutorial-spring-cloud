package guru.springframework.msscbeerservice.services.inventory;

import guru.springframework.msscbeerservice.services.inventory.model.BeerInventoryDto;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryServiceFailoverFeignClientImpl implements InventoryServiceFeignClient {

  private final InventoryFailoverFeignClient inventoryFailoverFeignClient;

  @Override
  public ResponseEntity<BeerInventoryDto> getOnHandInventory(UUID beerId) {
    return inventoryFailoverFeignClient.getOnHandInventory();
  }
}
