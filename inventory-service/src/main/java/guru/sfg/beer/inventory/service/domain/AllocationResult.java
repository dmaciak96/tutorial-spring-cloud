package guru.sfg.beer.inventory.service.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AllocationResult {

  private BeerOrderDto beerOrderDto;
  private boolean allocationError;
  private boolean pendingInventory;
}
