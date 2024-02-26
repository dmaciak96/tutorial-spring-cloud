package guru.springframework.msscbeerservice.events;

import guru.springframework.msscbeerservice.web.model.BeerOrderDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeerOrderValidationEvent {

  public BeerOrderDto beerOrderDto;
}
