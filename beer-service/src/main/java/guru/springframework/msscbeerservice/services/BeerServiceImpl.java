package guru.springframework.msscbeerservice.services;

import guru.springframework.msscbeerservice.domain.Beer;
import guru.springframework.msscbeerservice.repositories.BeerRepository;
import guru.springframework.msscbeerservice.web.controller.NotFoundException;
import guru.springframework.msscbeerservice.web.mappers.BeerMapper;
import guru.springframework.msscbeerservice.web.model.BeerDto;
import guru.springframework.msscbeerservice.web.model.BeerPagedList;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/** Created by jt on 2019-06-06. */
@RequiredArgsConstructor
@Service
public class BeerServiceImpl implements BeerService {
  private final BeerRepository beerRepository;
  private final BeerMapper beerMapper;

  @Override
  public BeerDto getById(UUID beerId, boolean showInventoryOnHand) {
    if (showInventoryOnHand) {
      return beerMapper.beerToBeerDto(
          beerRepository.findById(beerId.toString()).orElseThrow(NotFoundException::new));
    }
    return beerMapper.beerToBeerDtoWithoutInventory(
        beerRepository.findById(beerId.toString()).orElseThrow(NotFoundException::new));
  }

  @Override
  public BeerDto saveNewBeer(BeerDto beerDto) {
    return beerMapper.beerToBeerDto(beerRepository.save(beerMapper.beerDtoToBeer(beerDto)));
  }

  @Override
  public BeerDto updateBeer(UUID beerId, BeerDto beerDto) {
    Beer beer = beerRepository.findById(beerId.toString()).orElseThrow(NotFoundException::new);

    beer.setBeerName(beerDto.getBeerName());
    beer.setBeerStyle(beerDto.getBeerStyle().name());
    beer.setPrice(beerDto.getPrice());
    beer.setUpc(beerDto.getUpc());

    return beerMapper.beerToBeerDto(beerRepository.save(beer));
  }

  @Override
  public BeerPagedList listBeers(
      String beerName, String beerStyle, PageRequest pageRequest, boolean showInventoryOnHand) {
    Page<Beer> beerPage;

    if (!StringUtils.isEmpty(beerName) && !StringUtils.isEmpty(beerStyle)) {
      beerPage = beerRepository.findAllByBeerNameAndBeerStyle(beerName, beerStyle, pageRequest);
    } else if (StringUtils.isEmpty(beerName) && !StringUtils.isEmpty(beerStyle)) {
      beerPage = beerRepository.findAllByBeerStyle(beerStyle, pageRequest);
    } else if (!StringUtils.isEmpty(beerName) && StringUtils.isEmpty(beerStyle)) {
      beerPage = beerRepository.findAllByBeerName(beerName, pageRequest);
    } else {
      beerPage = beerRepository.findAll(pageRequest);
    }

    return new BeerPagedList(
        beerPage.getContent().stream()
            .map(
                beer -> {
                  if (showInventoryOnHand) {
                    return beerMapper.beerToBeerDto(beer);
                  }
                  return beerMapper.beerToBeerDtoWithoutInventory(beer);
                })
            .collect(Collectors.toList()),
        PageRequest.of(
            beerPage.getPageable().getPageNumber(), beerPage.getPageable().getPageSize()),
        beerPage.getTotalElements());
  }

  @Override
  public BeerDto getByUpc(String upc) {
    return beerMapper.beerToBeerDto(
        beerRepository.findByUpc(upc).orElseThrow(NotFoundException::new));
  }
}
