package guru.springframework.msscbeerservice.events;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BeerOrderValidationResultEvent {

  public UUID beerOrderId;
  public boolean isValid;
}
