package guru.sfg.beer.order.service.domain;

public enum BeerOrderEventEnum {
  VALIDATE_ORDER, VALIDATION_PASSED, VALIDATION_FAILED, ALLOCATED_ORDER,
  ALLOCATION_SUCCESS, ALLOCATION_NO_INVENTORY, ALLOCATION_FAILED,
  BEER_ORDER_PICKER_UP, CANCEL_ORDER
}