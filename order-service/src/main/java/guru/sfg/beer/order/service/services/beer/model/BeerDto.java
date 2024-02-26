package guru.sfg.beer.order.service.services.beer.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeerDto {
  private String id;
  private String beerName;
  private String upc;
  private BigDecimal price;
}
