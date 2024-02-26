package guru.sfg.beer.order.service.services.beer;

import guru.sfg.beer.order.service.services.beer.model.BeerDto;
import java.util.UUID;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@ConfigurationProperties(prefix = "sfg.brewery", ignoreUnknownFields = false)
public class BeerServiceRestTemplateImpl implements BeerService {
  private static final String GET_BEER_BY_ID = "/api/v1/beer/{beerId}";
  public static final String BEER_PATH_V1 = "/api/v1/beer/";
  public static final String BEER_UPC_PATH_V1 = "/api/v1/beerUpc/";

  @Setter private String beerServiceHost;
  private final RestTemplate restTemplate;

  public BeerServiceRestTemplateImpl(RestTemplateBuilder restTemplateBuilder) {
    this.restTemplate = restTemplateBuilder.build();
  }

  @Override
  public BeerDto getBeerById(UUID id) {
    log.debug("Calling Beer Service ({})", beerServiceHost);

    var responseEntity =
        restTemplate.exchange(
            beerServiceHost + GET_BEER_BY_ID,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<BeerDto>() {},
            id);

    return responseEntity.getBody();
  }
}
