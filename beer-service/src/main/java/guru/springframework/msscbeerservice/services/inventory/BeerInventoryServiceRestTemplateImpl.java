package guru.springframework.msscbeerservice.services.inventory;

import guru.springframework.msscbeerservice.services.inventory.model.BeerInventoryDto;
import java.util.List;
import java.util.Objects;
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
public class BeerInventoryServiceRestTemplateImpl implements BeerInventoryService {

  public static final String INVENTORY_PATH = "/api/v1/beer/{beerId}/inventory";
  private final RestTemplate restTemplate;

  @Setter private String beerInventoryServiceHost;

  public BeerInventoryServiceRestTemplateImpl(RestTemplateBuilder restTemplateBuilder) {
    this.restTemplate = restTemplateBuilder.build();
  }

  @Override
  public Integer getOnHandInventory(UUID beerId) {
    log.debug("Calling Inventory Service ({})", beerInventoryServiceHost);

    var responseEntity =
        restTemplate.exchange(
            beerInventoryServiceHost + INVENTORY_PATH,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<BeerInventoryDto>>() {},
            (Object) beerId);

    return Objects.requireNonNull(responseEntity.getBody()).stream()
        .mapToInt(BeerInventoryDto::getQuantityOnHand)
        .sum();
  }
}
