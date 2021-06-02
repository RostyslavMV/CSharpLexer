package com.rmv.opk.entity;

import com.rmv.opk.constants.TokenType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Token {
  private TokenType type;
  private String data;
}
