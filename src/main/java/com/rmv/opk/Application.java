package com.rmv.opk;

import java.io.File;
import java.io.IOException;

public class Application {
  public static void main(String[] args) throws IOException {
    LexerFileTokensProcessor.processFile("csharp.cs");
    System.out.println("\n------\n");
    LexerFileTokensProcessor.processFile("csharp_corner_cases.cs");
  }
}
