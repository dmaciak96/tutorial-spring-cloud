package guru.sfg.beer.order.service.domain;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeerOrderValidationResultEvent {

  public UUID beerOrderId;
  public boolean isValid;
}
