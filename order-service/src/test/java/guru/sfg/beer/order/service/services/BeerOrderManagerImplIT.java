package guru.sfg.beer.order.service.services;

import static com.github.jenspiegsa.wiremockextension.ManagedWireMockServer.with;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jenspiegsa.wiremockextension.WireMockExtension;
import com.github.tomakehurst.wiremock.WireMockServer;
import guru.sfg.beer.order.service.domain.BeerOrder;
import guru.sfg.beer.order.service.domain.BeerOrderLine;
import guru.sfg.beer.order.service.domain.BeerOrderStatusEnum;
import guru.sfg.beer.order.service.domain.Customer;
import guru.sfg.beer.order.service.repositories.BeerOrderRepository;
import guru.sfg.beer.order.service.repositories.CustomerRepository;
import guru.sfg.beer.order.service.services.beer.BeerServiceRestTemplateImpl;
import guru.sfg.beer.order.service.services.beer.model.BeerDto;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootTest
@ExtendWith(WireMockExtension.class)
public class BeerOrderManagerImplIT {

  @Autowired
  BeerOrderManager beerOrderManager;

  @Autowired
  BeerOrderRepository beerOrderRepository;

  @Autowired
  CustomerRepository customerRepository;

  @Autowired
  WireMockServer server;

  @Autowired
  ObjectMapper objectMapper;

  Customer testCustomer;

  UUID beerId = UUID.randomUUID();

  @TestConfiguration
  static class RestTemplateBuilderProvider {

    @Bean(destroyMethod = "stop")
    public WireMockServer wireMockServer() {
      var wireMockServer = with(wireMockConfig().port(8083));
      wireMockServer.start();
      return wireMockServer;
    }
  }

  @BeforeEach
  void setUp() {
    testCustomer = customerRepository.save(Customer.builder()
        .customerName("Test Customer")
        .build());
  }

  @Test
  void testNewToAllocate() throws JsonProcessingException {
    var order = BeerDto.builder()
        .id(beerId.toString())
        .upc("1234")
        .build();
    server.stubFor(get(BeerServiceRestTemplateImpl.BEER_PATH_V1).willReturn(
        okJson(objectMapper.writeValueAsString(order))));

    var beerOrder = createBeerOrder();
    var savedOrder = beerOrderManager.newBeerOrder(beerOrder);
    assertNotNull(savedOrder);
    assertEquals(BeerOrderStatusEnum.ALLOCATED, savedOrder.getOrderStatus());
  }

  public BeerOrder createBeerOrder() {
    var order = BeerOrder.builder()
        .customer(testCustomer)
        .build();
    order.setBeerOrderLines(Set.of(BeerOrderLine.builder()
        .beerId(beerId)
        .orderQuantity(1)
        .beerOrder(order)
        .build()));
    return order;
  }
}
