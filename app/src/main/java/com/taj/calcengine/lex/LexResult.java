package com.taj.calcengine.lex;

import com.taj.calcengine.fundamental.TokenType;

import java.util.ArrayList;

/**
 * Receives the result of lexing from {@link TextLexer}.
 *
 * @author Tony Hu
 */
public class LexResult {
    private ArrayList<String> elems = new ArrayList<>();
    private ArrayList<TokenType> types = new ArrayList<>();

    public LexResult(TextLexer tl) {
        while (tl.hasNextToken()) {
            TextLexer.LexedPair lp = tl.nextToken();
            elems.add(lp.elem);
            types.add(lp.type);
        }
    }

    public ArrayList<String> getElems() {
        return elems;
    }

    public ArrayList<TokenType> getTypes() {
        return types;
    }
}
