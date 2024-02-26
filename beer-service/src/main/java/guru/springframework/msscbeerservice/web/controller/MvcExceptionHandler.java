package guru.springframework.msscbeerservice.web.controller;

import java.util.List;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Created by jt on 2019-05-25.
 */
@ControllerAdvice
public class MvcExceptionHandler {

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<List> validationErrorHandler(ConstraintViolationException ex) {
    return new ResponseEntity<>(List.of(), HttpStatus.BAD_REQUEST);
  }

}
