package guru.springframework.msscbeerservice.services;

import guru.springframework.msscbeerservice.web.model.BeerDto;

import guru.springframework.msscbeerservice.web.model.BeerPagedList;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

/**
 * Created by jt on 2019-06-06.
 */
public interface BeerService {
    BeerDto getById(UUID beerId, boolean showInventoryOnHand);

    BeerDto saveNewBeer(BeerDto beerDto);

    BeerDto updateBeer(UUID beerId, BeerDto beerDto);

    BeerPagedList listBeers(String beerName, String beerStyle, PageRequest pageRequest, boolean showInventoryOnHand);

    BeerDto getByUpc(String upc);
}
