package guru.sfg.beer.inventory.service.domain;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NewInventoryEvent extends BeerEvent {

  public NewInventoryEvent(BeerDto beerDto) {
    super(beerDto);
  }
}
