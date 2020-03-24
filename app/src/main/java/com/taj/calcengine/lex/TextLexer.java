package com.taj.calcengine.lex;

import com.taj.calcengine.fundamental.TokenType;

import java.util.NoSuchElementException;

/**
 * Lexer for text input from the front end.
 */
public class TextLexer {
    static class LexedPair {
        String elem = "";
        TokenType type = TokenType.NONE;

        LexedPair(String _elem, TokenType _type) {
            elem = _elem;
            type = _type;
        }
    }

    private String source = "";
    private int length;
    private int index = 0;

    public TextLexer(String _source) {
        source = _source;
        length = source.length();
    }

    boolean hasNextToken() {
        return !(index >= length);
    }

    /**
     * Retrieves the next token from <code>source</code>.
     *
     * @return 1
     */
    LexedPair nextToken() {
        int oldInd = index;
        // end of input reached
        if (index >= length) {
            throw new NoSuchElementException();
        }
        char c = source.charAt(index);
        // brackets and delimiters and operators
        if ("(){}+-*/".indexOf(c) != -1) {
            index++;
            return new LexedPair(String.valueOf(c), TokenType.charToTok(c));
        }
        // only integers (floating point numbers are represented by fractions)
        else if (Character.isDigit(c)) {
            StringBuilder sb = new StringBuilder(String.valueOf(c));
            while (++index < length && Character.isDigit((c = source.charAt(index)))) {
                sb.append(c);
            }
            return new LexedPair(sb.toString(), TokenType.NUM);
        }
        // functions
        else if (c == '\\') {
            while (++index < length
                  && (Character.isAlphabetic(source.charAt(index)) || source.charAt(index) == '_'));
            return new LexedPair(source.substring(oldInd, index), TokenType.FUNC);
        }

        return new LexedPair("error", TokenType.NONE);
    }

}
