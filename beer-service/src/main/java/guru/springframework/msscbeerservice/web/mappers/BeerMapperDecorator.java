package guru.springframework.msscbeerservice.web.mappers;

import guru.springframework.msscbeerservice.domain.Beer;
import guru.springframework.msscbeerservice.services.inventory.BeerInventoryService;
import guru.springframework.msscbeerservice.web.model.BeerDto;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BeerMapperDecorator implements BeerMapper {
  private BeerInventoryService beerInventoryService;
  private BeerMapper beerMapper;

  @Autowired
  public void setBeerInventoryService(BeerInventoryService beerInventoryService) {
    this.beerInventoryService = beerInventoryService;
  }

  @Autowired
  public void setBeerMapper(BeerMapper beerMapper) {
    this.beerMapper = beerMapper;
  }

  @Override
  public BeerDto beerToBeerDto(Beer beer) {
    var dto = beerMapper.beerToBeerDto(beer);
    dto.setQuantityOnHand(beerInventoryService.getOnHandInventory(UUID.fromString(dto.getId())));
    return dto;
  }

  @Override
  public BeerDto beerToBeerDtoWithoutInventory(Beer beer) {
    return beerMapper.beerToBeerDto(beer);
  }

  @Override
  public Beer beerDtoToBeer(BeerDto dto) {
    return beerMapper.beerDtoToBeer(dto);
  }
}
