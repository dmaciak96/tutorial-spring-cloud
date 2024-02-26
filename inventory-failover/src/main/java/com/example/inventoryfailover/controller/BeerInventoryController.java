package com.example.inventoryfailover.controller;

import com.example.inventoryfailover.domain.BeerInventoryDto;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class BeerInventoryController {

  @GetMapping("/inventory-failover")
  public List<BeerInventoryDto> listBeersById() {
    return List.of(BeerInventoryDto.builder()
        .id(new UUID(0, 0))
        .beerId(new UUID(0, 0))
        .quantityOnHand(999)
        .lastModifiedDate(OffsetDateTime.now())
        .createdDate(OffsetDateTime.now())
        .build());
  }
}
