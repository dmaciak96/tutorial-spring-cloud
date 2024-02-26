package guru.sfg.beer.inventory.service.services;

import guru.sfg.beer.inventory.service.config.JmsConfig;
import guru.sfg.beer.inventory.service.domain.AllocationRequest;
import guru.sfg.beer.inventory.service.domain.AllocationResult;
import guru.sfg.beer.inventory.service.domain.BeerInventory;
import guru.sfg.beer.inventory.service.domain.BeerOrderDto;
import guru.sfg.beer.inventory.service.domain.BeerOrderLineDto;
import guru.sfg.beer.inventory.service.repositories.BeerInventoryRepository;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AllocationServiceImpl implements
    AllocationService {

  private final BeerInventoryRepository beerInventoryRepository;
  private final JmsTemplate jmsTemplate;


  @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_QUEUE_NAME)
  public void listenBrewBeerEvent(AllocationRequest event) {
    var result = allocateOrder(event.getBeerOrderDto());
    var allocationResult = new AllocationResult(event.getBeerOrderDto(), !result, result);
    jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_RESULT, allocationResult);
  }


  @Override
  public Boolean allocateOrder(BeerOrderDto beerOrderDto) {
    log.debug("Allocating OrderId: " + beerOrderDto.getId());

    AtomicInteger totalOrdered = new AtomicInteger();
    AtomicInteger totalAllocated = new AtomicInteger();

    beerOrderDto.getBeerOrderLines().forEach(beerOrderLine -> {
      if ((((beerOrderLine.getOrderQuantity() != null ? beerOrderLine.getOrderQuantity() : 0)
          - (beerOrderLine.getQuantityAllocated() != null ? beerOrderLine.getQuantityAllocated()
          : 0)) > 0)) {
        allocateBeerOrderLine(beerOrderLine);
      }
      totalOrdered.set(totalOrdered.get() + beerOrderLine.getOrderQuantity());
      totalAllocated.set(totalAllocated.get() + (beerOrderLine.getQuantityAllocated() != null
          ? beerOrderLine.getQuantityAllocated() : 0));
    });

    log.debug("Total Ordered: " + totalOrdered.get() + " Total Allocated: " + totalAllocated.get());

    return totalOrdered.get() == totalAllocated.get();
  }

  private void allocateBeerOrderLine(BeerOrderLineDto beerOrderLine) {
    List<BeerInventory> beerInventoryList = beerInventoryRepository.findAllByUpc(
        beerOrderLine.getUpc());

    beerInventoryList.forEach(beerInventory -> {
      int inventory =
          (beerInventory.getQuantityOnHand() == null) ? 0 : beerInventory.getQuantityOnHand();
      int orderQty =
          (beerOrderLine.getOrderQuantity() == null) ? 0 : beerOrderLine.getOrderQuantity();
      int allocatedQty =
          (beerOrderLine.getQuantityAllocated() == null) ? 0 : beerOrderLine.getQuantityAllocated();
      int qtyToAllocate = orderQty - allocatedQty;

      if (inventory >= qtyToAllocate) { // full allocation
        inventory = inventory - qtyToAllocate;
        beerOrderLine.setQuantityAllocated(orderQty);
        beerInventory.setQuantityOnHand(inventory);

        beerInventoryRepository.save(beerInventory);
      } else if (inventory > 0) { //partial allocation
        beerOrderLine.setQuantityAllocated(allocatedQty + inventory);
        beerInventory.setQuantityOnHand(0);

      }

      if (beerInventory.getQuantityOnHand() == 0) {
        beerInventoryRepository.delete(beerInventory);
      }
    });

  }

  @Override
  public void deallocateOrder(BeerOrderDto beerOrderDto) {
    beerOrderDto.getBeerOrderLines().forEach(beerOrderLineDto -> {
      BeerInventory beerInventory = BeerInventory.builder()
          .beerId(beerOrderLineDto.getBeerId())
          .upc(beerOrderLineDto.getUpc())
          .quantityOnHand(beerOrderLineDto.getQuantityAllocated())
          .build();

      BeerInventory savedInventory = beerInventoryRepository.save(beerInventory);

      log.debug("Saved Inventory for beer upc: " + savedInventory.getUpc() + " inventory id: "
          + savedInventory.getId());
    });
  }
}
