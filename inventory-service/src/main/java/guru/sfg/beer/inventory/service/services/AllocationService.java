package guru.sfg.beer.inventory.service.services;

import guru.sfg.beer.inventory.service.domain.BeerOrderDto;

public interface AllocationService {

  Boolean allocateOrder(BeerOrderDto beerOrderDto);

  void deallocateOrder(BeerOrderDto beerOrderDto);

}
