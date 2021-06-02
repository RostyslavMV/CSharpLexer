package com.rmv.opk.checkers;

import java.util.regex.Pattern;

public class CharacterTypeChecker {
  public static boolean isSeparator(Character character) {
    return character == '('
        || character == ')'
        || character == '{'
        || character == '}'
        || character == '['
        || character == ']'
        || character == ';'
        || character == ','
        || character == '.';
  }

  public static boolean isOperator(Character character) {
    return character == '='
        || character == '>'
        || character == '<'
        || character == '!'
        || character == '~'
        || character == ':'
        || character == '?'
        || character == '&'
        || character == '|'
        || character == '+'
        || character == '-'
        || character == '*'
        || character == '/'
        || character == '^'
        || character == '%';
  }

  public static boolean isBinary(Character character) {
    return character == '0' || character == '1';
  }

  public static boolean isHex(Character character) {
    return Pattern.matches("\\d|[a-fA-F]", character.toString());
  }

  public static boolean isFloatingPointNumber(Character character) {
    return character == 'f' || character == 'F' || character == 'd' || character == 'D';
  }
}
