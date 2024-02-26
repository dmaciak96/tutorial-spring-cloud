package guru.sfg.beer.order.service.web.mappers;

import guru.sfg.beer.order.service.domain.BeerOrderLine;
import guru.sfg.beer.order.service.services.beer.BeerService;
import guru.sfg.beer.order.service.web.model.BeerOrderLineDto;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BeerOrderLineDecorator implements BeerOrderLineMapper {
  private BeerOrderLineMapper beerOrderLineMapper;
  private BeerService beerService;

  @Autowired
  public void setBeerService(BeerService beerService) {
    this.beerService = beerService;
  }

  @Autowired
  public void setBeerOrderLineMapper(BeerOrderLineMapper beerOrderLineMapper) {
    this.beerOrderLineMapper = beerOrderLineMapper;
  }

  @Override
  public BeerOrderLineDto beerOrderLineToDto(BeerOrderLine line) {
    var dto = beerOrderLineMapper.beerOrderLineToDto(line);
    var beer = beerService.getBeerById(line.getBeerId());
    dto.setBeerName(beer.getBeerName());
    dto.setUpc(beer.getUpc());
    return dto;
  }

  @Override
  public BeerOrderLine dtoToBeerOrderLine(BeerOrderLineDto dto) {
    return beerOrderLineMapper.dtoToBeerOrderLine(dto);
  }
}
