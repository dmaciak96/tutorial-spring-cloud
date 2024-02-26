package guru.springframework.msscbeerservice.services.inventory;

import guru.springframework.msscbeerservice.services.inventory.model.BeerInventoryDto;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "inventory-failover")
public interface InventoryFailoverFeignClient {
  @RequestMapping(method = RequestMethod.GET, value = "/inventory-failover")
  ResponseEntity<BeerInventoryDto> getOnHandInventory();
}
