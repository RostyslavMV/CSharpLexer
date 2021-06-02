package com.rmv.opk.checkers;

import com.rmv.opk.constants.Keywords;

public class StringTypeChecker {
  public static boolean isKeyword(String string) {
    return Keywords.getKeywordsList().contains(string);
  }

  public static boolean isSpecial(String string) {
    return "\\b".equals(string)
        || "\\t".equals(string)
        || "\\n".equals(string)
        || "\\".equals(string)
        || "'".equals(string)
        || "\"".equals(string)
        || "\\r".equals(string)
        || "\\f".equals(string);
  }

  public static boolean isBoolean(String string) {
    return "true".equals(string) || "false".equals(string);
  }

  public static boolean isNull(String string) {
    return "null".equals(string);
  }
}
