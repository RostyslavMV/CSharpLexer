package com.rmv.opk;

import com.rmv.opk.entity.Token;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;

public class LexerFileTokensProcessor {

    public static void processFile(String fileName) throws IOException {
        File fileWithCode = new File(fileName);
        InputStream inputStream = new FileInputStream(fileWithCode);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.defaultCharset());
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        Lexer lexer = new Lexer();
        List<Token> tokens = lexer.getTokens(bufferedReader);

        for (Token token : tokens) {
            convertNewLineSymbolsForOutput(token);
            System.out.println("(" + token.getType() + " " + token.getData() + ")");
        }
    }

    private static void convertNewLineSymbolsForOutput(Token token){
        switch (token.getData()) {
            case "\r" -> token.setData("\\r");
            case "\n" -> token.setData("\\n");
            case "\r\n" -> token.setData("\\r\\n");
        }
    }
}
