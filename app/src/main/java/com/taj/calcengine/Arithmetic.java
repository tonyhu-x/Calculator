package com.taj.calcengine;

import com.taj.calcengine.fundamental.Functions;
import com.taj.calcengine.fundamental.Rational;
import com.taj.calcengine.fundamental.TokenType;
import com.taj.calcengine.lex.LexResult;
import com.taj.calcengine.lex.TextLexer;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * <p>Calculator engine for arithmetic expressions. The parser is Recursive Descent.</p>
 * <p>An invariant is that every time a function is called, {@code index} points to the
 * next token not yet processed, except when {@link #value()} calls {@link #func()}.</p>
 *
 * <p>The following grammar is defined:</p>
 * <ul>
 *     <li>{@code Expression} := {@code Term} [OPRL1 {@code Term}]*</li>
 *     <li>{@code Term}&#9;:= {@code Primary} [OPRL2 {@code Primary}]*</li>
 *     <li>{@code Primary}&#9;:= '(' {@code Expression} ')' | {@code Value}</li>
 *     <li>{@code Value}&#9;:= NUM | {@code Func}</li>
 *     <li>{@code Func}&#9;:= FUNC {{@code EXPRESSION}}*</li>
 * </ul>
 */
public class Arithmetic implements CalcEngine {
    private ArrayList<String> elems = new ArrayList<>();
    private ArrayList<TokenType> types = new ArrayList<>();
    private int length = 0;
    private int index = 0;

    @Override
    public Rational calc(String source) {
        TextLexer tl = new TextLexer(source);
        LexResult lr = new LexResult(tl);
        initRes(lr);
        return expr();
    }

    void initRes(LexResult lx) {
        elems = lx.getElems();
        types = lx.getTypes();
        length = elems.size();
        index = 0;
    }

    private Rational expr() {
        Rational lhs = term();
        while (index < length && types.get(index) == TokenType.OPRL1) {
            switch(elems.get(index++).charAt(0)) {
            case '+':
                lhs = lhs.plus(term());
                break;
            case '-':
                lhs = lhs.minus(term());
                break;
            default:
                break;  // it should never happen
            }
        }
        return lhs;
    }

    private Rational term() {
        Rational lhs = primary();
        while (index < length && types.get(index) == TokenType.OPRL2) {
            switch(elems.get(index++).charAt(0)) {
            case '*':
                lhs = lhs.times(primary());
                break;
            case '/':
                lhs = lhs.dividedBy(primary());
                break;
            default:
                break;  // it should never happen
            }
        }
        return lhs;
    }

    private Rational primary() {
        if (index >= length) {
            throw new SyntaxException(elems.get(elems.size() - 1));
        }
        if (types.get(index) == TokenType.OPAREN) {
            index++;
            Rational result = expr();
            if (types.get(index) != TokenType.CPAREN) {
                throw new SyntaxException(elems.get(index));
            }
            index++;
            return result;
        }
        return value();
    }

    private Rational value() {
        if (index >= length) {
            throw new SyntaxException(elems.get(elems.size() - 1));
        }
        switch (types.get(index)) {
        case NUM:
            return new Rational(Long.parseLong(elems.get(index++)));
        case FUNC:
            return func();
        default:
            throw new SyntaxException(elems.get(index));
        }
    }

    private Rational func() {
        if (index >= length) {
            throw new SyntaxException(elems.get(elems.size() - 1));
        }
        String name = elems.get(index);
        ArrayList<Rational> args = new ArrayList<>();

        while (++index < length && types.get(index) == TokenType.ODLM) {
            index++;
            args.add(expr());
            if (index >= length) {
                throw new SyntaxException(elems.get(elems.size() - 1));
            }
            if (types.get(index) != TokenType.CDLM) {
                throw new SyntaxException(elems.get(index));
            }
        }

        Rational[] arr = new Rational[args.size()];
        args.toArray(arr);
        return Functions.call(name, arr);
    }
}
