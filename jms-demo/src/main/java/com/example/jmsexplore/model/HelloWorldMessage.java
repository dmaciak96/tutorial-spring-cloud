package com.example.jmsexplore.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HelloWorldMessage implements Serializable {

  @Serial private static final long serialVersionUID = -1167369009711532638L;

  private UUID id;
  private String message;
}
