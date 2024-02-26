package guru.springframework.msscbeerservice.web.controller;

import guru.springframework.msscbeerservice.services.BeerService;
import guru.springframework.msscbeerservice.web.model.BeerDto;
import guru.springframework.msscbeerservice.web.model.BeerPagedList;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/** Created by jt on 2019-05-12. */
@RequiredArgsConstructor
@RestController
public class BeerController {
  private static final String BEER_ENDPOINT = "/api/v1/beer";
  private static final String BEER_UPC_ENDPOINT = "/api/v1/beerUpc";

  private static final int DEFAULT_PAGE_NUMBER = 0;
  private static final int DEFAULT_PAGE_SIZE = 25;

  private final BeerService beerService;

  @GetMapping(BEER_ENDPOINT)
  public ResponseEntity<BeerPagedList> getAllBeers(
      @RequestParam(required = false) Integer pageNumber,
      @RequestParam(required = false) Integer pageSize,
      @RequestParam(required = false) String beerName,
      @RequestParam(required = false) String beerStyle,
      @RequestParam(defaultValue = "false") boolean showInventoryOnHand) {

    if (pageNumber == null || pageNumber < 0) {
      pageNumber = DEFAULT_PAGE_NUMBER;
    }

    if (pageSize == null || pageSize < 1) {
      pageSize = DEFAULT_PAGE_SIZE;
    }

    var beerList =
        beerService.listBeers(
            beerName, beerStyle, PageRequest.of(pageNumber, pageSize), showInventoryOnHand);
    return new ResponseEntity<>(beerList, HttpStatus.OK);
  }

  @GetMapping(BEER_ENDPOINT + "/{beerId}")
  public ResponseEntity<BeerDto> getBeerById(
      @PathVariable("beerId") UUID beerId,
      @RequestParam(defaultValue = "false") boolean showInventoryOnHand) {
    return new ResponseEntity<>(beerService.getById(beerId, showInventoryOnHand), HttpStatus.OK);
  }

  @PostMapping(BEER_ENDPOINT)
  public ResponseEntity saveNewBeer(@RequestBody @Validated BeerDto beerDto) {
    return new ResponseEntity<>(beerService.saveNewBeer(beerDto), HttpStatus.CREATED);
  }

  @PutMapping(BEER_ENDPOINT + "/{beerId}")
  public ResponseEntity updateBeerById(
      @PathVariable("beerId") UUID beerId, @RequestBody @Validated BeerDto beerDto) {
    return new ResponseEntity<>(beerService.updateBeer(beerId, beerDto), HttpStatus.NO_CONTENT);
  }

  @GetMapping(BEER_UPC_ENDPOINT + "/{upc}")
  public ResponseEntity<BeerDto> getByUpc(@PathVariable String upc) {
    return new ResponseEntity<>(beerService.getByUpc(upc), HttpStatus.OK);
  }
}
