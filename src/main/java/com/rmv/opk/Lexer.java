package com.rmv.opk;

import com.rmv.opk.checkers.CharacterTypeChecker;
import com.rmv.opk.checkers.StringTypeChecker;
import com.rmv.opk.constants.State;
import com.rmv.opk.constants.TokenType;
import com.rmv.opk.entity.Token;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class Lexer {
  private final List<Token> tokens = new ArrayList<>();
  private StringBuilder buffer = new StringBuilder();
  private State state = State.INITIAL;

  public List<Token> getTokens(Reader reader) throws IOException {
    int lastReadInt;
    while ((lastReadInt = reader.read()) != -1) {
      Character character = (char) lastReadInt;
      processCharacter(character);
    }
    processCharacter(' ');
    return tokens;
  }

  private void processCharacter(Character character) {
    buffer.append(character);
    switch (state) {
      case INITIAL -> initialState(character);
      case ERROR -> errorState(character);
      case SLASH -> slashState(character);
      case IDENTIFIER -> identifierState(character);
      case ZERO_FIRST -> zeroFirstState(character);
      case NON_ZERO_DIGIT -> nonZeroDigitState(character);
      case CHAR_LITERAL -> charLiteralState(character);
      case STRING_LITERAL -> stringLiteralState(character);
      case DOT -> dotState(character);
      case GREATER -> greaterState(character);
      case LESS -> lessState(character);
      case AMPERSAND -> ampersandState(character);
      case SINGLE_OPERATOR -> singleOperatorState(character);
      case COLON -> colonState(character);
      case PLUS -> plusState(character);
      case MINUS -> minusState(character);
      case PIPE -> pipeState(character);
      case SINGLE_LINE_COMMENT -> singleLineCommentState(character);
      case MULTI_LINE_COMMENT -> multilineCommentState(character);
      case BINARY_DIGITS -> binaryDigitState(character);
      case HEX_DIGITS -> hexDigitState(character);
      case INTEGER_SUFFIX -> integerSuffixState(character);
      case POINT_IN_DIGIT -> pointInDigitState(character);
      case POSSIBLE_ESCAPE_SEQUENCE_CHAR -> possibleEscapeSequenceCharState(character);
      case EXPECT_END_OF_CHAR -> expectEndOfCharState(character);
      case POSSIBLE_ESCAPE_SEQUENCE -> possibleEscapeSequenceState(character);
      case DOUBLE_GREATER -> doubleGreaterState(character);
      case OPERATOR_AND_EQUAL -> operatorAndEqualState(character);
      case STAR_IN_MULTI_LINE_COMMENT -> starInMultilineCommentState(character);
      case FLOAT_SUFFIX -> floatSuffixState(character);
      case DOUBLE_DOT -> doubleDotState(character);
      default -> System.out.println("No state determined for this case");
    }
  }

  private void createToken(TokenType tokenType) {
    tokens.add(new Token(tokenType, buffer.toString()));
    buffer = new StringBuilder();
  }

  private void createPreviousDataToken(TokenType tokenType) {
    String previousData = buffer.substring(0, buffer.length() - 1);
    Character lastSymbol = buffer.charAt(buffer.length() - 1);
    tokens.add(new Token(tokenType, previousData));
    buffer = new StringBuilder();
    buffer.append(lastSymbol);
  }

  private void initialState(Character character) {
    if (character == '/') {
      state = State.SLASH;
    } else if (Character.isWhitespace(character)) {
      createToken(TokenType.WHITESPACE);
      state = State.INITIAL;
    } else if (Character.isJavaIdentifierStart(character)) {
      state = State.IDENTIFIER;
    } else if (character == '0') {
      state = State.ZERO_FIRST;
    } else if (Character.isDigit(character)) {
      state = State.NON_ZERO_DIGIT;
    } else if (character == '\'') {
      state = State.CHAR_LITERAL;
    } else if (character == '\"') {
      state = State.STRING_LITERAL;
    } else if (character == '.') {
      state = State.DOT;
    } else if (CharacterTypeChecker.isSeparator(character)) {
      createToken(TokenType.SEPARATOR);
      state = State.INITIAL;
    } else if (character == '>') {
      state = State.GREATER;
    } else if (character == '<') {
      state = State.LESS;
    } else if (character == '&') {
      state = State.AMPERSAND;
    } else if (character == '^' || character == '!' || character == '*' || character == '=' || character == '%') {
      state = State.SINGLE_OPERATOR;
    } else if (character == ':') {
      state = State.COLON;
    } else if (character == '+') {
      state = State.PLUS;
    } else if (character == '-') {
      state = State.MINUS;
    } else if (character == '?' || character == '~') {
      createToken(TokenType.OPERATOR);
      state = State.INITIAL;
    } else if (character == '#') {
      state = State.ERROR;
    } else if (character == '|') {
      state = State.PIPE;
    } else {
      state = State.ERROR;
    }
  }

  private void errorState(Character character) {
    createPreviousDataToken(TokenType.ERROR);
    state = State.INITIAL;
    initialState(character);
  }


  private void slashState(Character character) {
    if (character == '/') {
      state = State.SINGLE_LINE_COMMENT;
    } else if (character == '*') {
      state = State.MULTI_LINE_COMMENT;
    } else if (character == '=') {
      state = State.OPERATOR_AND_EQUAL;
    } else if (CharacterTypeChecker.isOperator(character)) {
      state = State.ERROR;
    } else {
      createPreviousDataToken(TokenType.OPERATOR);
      state = State.INITIAL;
      initialState(character);
    }
  }

  private void identifierState(Character character) {
    if (!Character.isJavaIdentifierPart(character)) {
      if (character == '#') {
        state = State.ERROR;
      } else if (Character.isWhitespace(character)
          || CharacterTypeChecker.isOperator(character)
          || CharacterTypeChecker.isSeparator(character)
          || character == '/') {
        buffer.deleteCharAt(buffer.length() - 1);
        if (StringTypeChecker.isNull(buffer.toString())) {
          createToken(TokenType.NULL);
        } else if (StringTypeChecker.isBoolean(buffer.toString())) {
          createToken(TokenType.BOOLEAN);
        } else if (StringTypeChecker.isKeyword(buffer.toString())) {
          createToken(TokenType.KEYWORD);
        } else {
          createToken(TokenType.IDENTIFIER);
        }
        buffer.append(character);
        state = State.INITIAL;
        initialState(character);
      } else {
        state = State.ERROR;
      }
    }
  }

  private void zeroFirstState(Character character) {
    if (character == 'b' || character == 'B') {
      state = State.BINARY_DIGITS;
    } else if (character == 'x' || character == 'X') {
      state = State.HEX_DIGITS;
    } else if (character == '.') {
      state = State.POINT_IN_DIGIT;
    } else if (character == 'l' || character == 'L') {
      state = State.INTEGER_SUFFIX;
    } else if (Character.isJavaIdentifierPart(character) || character == '8' || character == '9') {
      state = State.ERROR;
    } else {
      createPreviousDataToken(TokenType.INT);
      state = State.INITIAL;
      initialState(character);
    }
  }

  private void nonZeroDigitState(Character character) {
    if (!Character.isDigit(character)) {
      if (character == '.') {
        state = State.POINT_IN_DIGIT;
      } else if (character == 'l' || character == 'L') {
        state = State.INTEGER_SUFFIX;
      } else if (character == 'f' || character == 'F') {
        state = State.FLOAT_SUFFIX;
      } else if (Character.isJavaIdentifierPart(character)) {
        state = State.ERROR;
      } else {
        createPreviousDataToken(TokenType.INT);
        state = State.INITIAL;
        initialState(character);
      }
    }
  }

  private void charLiteralState(Character character) {
    if (character == '\\') {
      state = State.POSSIBLE_ESCAPE_SEQUENCE_CHAR;
    } else if (Character.isWhitespace(character) && character != ' ' && character != '\t') {
      createPreviousDataToken(TokenType.ERROR);
      state = State.INITIAL;
      initialState(character);
    } else {
      state = State.EXPECT_END_OF_CHAR;
    }
  }

  private void stringLiteralState(Character character) {
    if (character == '\"') {
      if (buffer.charAt(buffer.length() - 2) != '\\') {
        createToken(TokenType.STRING);
        state = State.INITIAL;
      } else {
        buffer.deleteCharAt(buffer.length() - 2);
      }
    }
  }

  private void dotState(Character character) {
    if (Character.isDigit(character)) {
      state = State.POINT_IN_DIGIT;
    } else if (character == '.') {
      state = State.DOUBLE_DOT;
    } else {
      createPreviousDataToken(TokenType.SEPARATOR);
      state = State.INITIAL;
      initialState(character);
    }
  }

  private void doubleDotState(Character character) {
    if (character != '.') {
      buffer = new StringBuilder();
      buffer.append(".");
      createToken(TokenType.SEPARATOR);
      buffer.append(".");
    }
    createToken(TokenType.SEPARATOR);
    state = State.INITIAL;
  }

  private void greaterState(Character character) {
    if (character == '=') {
      createToken(TokenType.OPERATOR);
      state = State.INITIAL;
    } else if (character == '>') {
      state = State.DOUBLE_GREATER;
    } else if (CharacterTypeChecker.isOperator(character)) {
      state = State.ERROR;
    } else {
      createPreviousDataToken(TokenType.OPERATOR);
      state = State.INITIAL;
      initialState(character);
    }
  }

  private void doubleGreaterState(Character character) {
    if (character == '>') {
      createToken(TokenType.OPERATOR);
      state = State.INITIAL;
    } else if (CharacterTypeChecker.isOperator(character)) {
      state = State.ERROR;
    } else {
      createPreviousDataToken(TokenType.OPERATOR);
      state = State.INITIAL;
      initialState(character);
    }
  }

  private void lessState(Character character) {
    if (character == '=') {
      createToken(TokenType.OPERATOR);
      state = State.INITIAL;
    } else if (character == '>') {
      createToken(TokenType.OPERATOR);
      state = State.INITIAL;
    } else if (character == '<') {
      createToken(TokenType.OPERATOR);
      state = State.INITIAL;
    } else if (CharacterTypeChecker.isOperator(character)) {
      state = State.ERROR;
    } else {
      createPreviousDataToken(TokenType.OPERATOR);
      state = State.INITIAL;
      initialState(character);
    }
  }

  private void ampersandState(Character character) {
    if (character == '&') {
      createToken(TokenType.OPERATOR);
      state = State.INITIAL;
    } else if (character == '=') {
      state = State.OPERATOR_AND_EQUAL;
    } else if (CharacterTypeChecker.isOperator(character)) {
      state = State.ERROR;
    } else {
      createPreviousDataToken(TokenType.OPERATOR);
      state = State.INITIAL;
      initialState(character);
    }
  }

  private void singleOperatorState(Character character) {
    if (character == '=') {
      state = State.OPERATOR_AND_EQUAL;
    } else if (CharacterTypeChecker.isOperator(character)) {
      state = State.ERROR;
    } else {
      createPreviousDataToken(TokenType.OPERATOR);
      state = State.INITIAL;
      initialState(character);
    }
  }

  private void colonState(Character character) {
    if (character == ':') {
      createToken(TokenType.SEPARATOR);
      state = State.INITIAL;
    } else if (CharacterTypeChecker.isOperator(character)) {
      state = State.ERROR;
    } else {
      createPreviousDataToken(TokenType.OPERATOR);
      state = State.INITIAL;
      initialState(character);
    }
  }

  private void plusState(Character character) {
    if (character == '+') {
      state = State.SINGLE_OPERATOR;
    } else if (character == '=') {
      state = State.OPERATOR_AND_EQUAL;
    } else if (CharacterTypeChecker.isOperator(character)) {
      state = State.INITIAL;
    } else {
      createPreviousDataToken(TokenType.OPERATOR);
      state = State.INITIAL;
      initialState(character);
    }
  }

  private void minusState(Character character) {
    if (character == '-') {
      state = State.SINGLE_OPERATOR;
    } else if (character == '=') {
      state = State.OPERATOR_AND_EQUAL;
    } else if (CharacterTypeChecker.isOperator(character)) {
      state = State.INITIAL;
    } else {
      createPreviousDataToken(TokenType.OPERATOR);
      state = State.INITIAL;
      initialState(character);
    }
  }

  private void singleLineCommentState(Character character) {
    if (Character.isWhitespace(character) && character != '\t' && character != ' ') {
      createPreviousDataToken(TokenType.COMMENT);
      state = State.INITIAL;
      initialState(character);
    }
  }

  private void multilineCommentState(Character character) {
    if (character == '*') {
      state = State.STAR_IN_MULTI_LINE_COMMENT;
    }
  }

  private void starInMultilineCommentState(Character character) {
    if (character == '/') {
      createToken(TokenType.COMMENT);
    } else {
      state = State.MULTI_LINE_COMMENT;
    }
  }

  private void operatorAndEqualState(Character character) {
    if (CharacterTypeChecker.isOperator(character)) {
      state = State.ERROR;
    } else {
      createPreviousDataToken(TokenType.OPERATOR);
      state = State.INITIAL;
      initialState(character);
    }
  }

  private void pipeState(Character character) {
    if (character == '|') {
      createToken(TokenType.OPERATOR);
      state = State.INITIAL;
    } else if (character == '=') {
      state = State.OPERATOR_AND_EQUAL;
    } else {
      createPreviousDataToken(TokenType.OPERATOR);
      state = State.INITIAL;
      initialState(character);
    }
  }

  private void pointInDigitState(Character character) {
    if (!Character.isDigit(character)) {
      if (CharacterTypeChecker.isFloatingPointNumber(character)) {
        state = State.FLOAT_SUFFIX;
      } else if (Character.isJavaIdentifierPart(character) || character == '.') {
        state = State.ERROR;
      } else {
        createPreviousDataToken(TokenType.FLOAT);
        state = State.INITIAL;
        initialState(character);
      }
    }
  }

  private void possibleEscapeSequenceState(Character character) {
    if (StringTypeChecker.isSpecial("\\" + character)) {
      state = State.STRING_LITERAL;
    } else {
      state = State.ERROR;
    }
  }

  private void possibleEscapeSequenceCharState(Character character) {
    if (StringTypeChecker.isSpecial("\\" + character)) {
      state = State.EXPECT_END_OF_CHAR;
    } else {
      state = State.ERROR;
    }
  }

  private void expectEndOfCharState(Character character) {
    if (character == '\'') {
      createToken(TokenType.CHAR);
      state = State.INITIAL;
    } else {
      state = State.ERROR;
    }
  }

  private void binaryDigitState(Character character) {
    if (!CharacterTypeChecker.isBinary(character)) {
      if (character != '_') {
        if (character == 'l' || character == 'L') {
          state = State.INTEGER_SUFFIX;
        } else if (character == 'f' || character == 'F') {
          state = State.FLOAT_SUFFIX;
        } else if (Character.isJavaIdentifierPart(character)) {
          state = State.ERROR;
        } else {
          createPreviousDataToken(TokenType.INT);
          state = State.INITIAL;
          initialState(character);
        }
      }
    }
  }

  private void hexDigitState(Character character) {
    if (!CharacterTypeChecker.isHex(character)) {
      if (character != '_') {
        if (character == 'l' || character == 'L') {
          state = State.INTEGER_SUFFIX;
        } else if (character == 'f' || character == 'F') {
          state = State.FLOAT_SUFFIX;
        } else if (Character.isJavaIdentifierPart(character)) {
          state = State.ERROR;
        } else {
          createPreviousDataToken(TokenType.INT);
          state = State.INITIAL;
          initialState(character);
        }
      }
    }
  }

  private void integerSuffixState(Character character) {
    if (Character.isJavaIdentifierPart(character)) {
      state = State.ERROR;
    } else {
      createPreviousDataToken(TokenType.INT);
      state = State.INITIAL;
      initialState(character);
    }
  }

  private void floatSuffixState(Character character) {
    if (Character.isJavaIdentifierPart(character)) {
      state = State.ERROR;
    } else {
      createPreviousDataToken(TokenType.FLOAT);
      state = State.INITIAL;
      initialState(character);
    }
  }
}
