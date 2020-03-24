package com.taj.calcengine;

import com.taj.calcengine.fundamental.Rational;

public interface CalcEngine {

    /**
     * Interface method for the user to call.
     *
     * @param source source string
     * @return the calculation result
     */
    public Rational calc(String source);
}
