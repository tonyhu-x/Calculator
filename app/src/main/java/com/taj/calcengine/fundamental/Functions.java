package com.taj.calcengine.fundamental;

import java.util.HashMap;
import java.util.function.Function;

public class Functions {
    private static final HashMap<String, Integer> funArgNum = new HashMap<>();
    private static final HashMap<String, Function<Rational[], Rational>> funcs = new HashMap<>();
    static {
        funArgNum.put("\\frac", 2);
        funArgNum.put("\\pow", 2);
        funcs.put("\\frac", Functions::frac);
        funcs.put("\\pow", Functions::pow);
    }

    public static Rational call(String name, Rational[] args) {
        if (!funArgNum.containsKey(name)) {
            throw new IllegalArgumentException("No such function.");
        }
        return funcs.get(name).apply(args);
    }

    static Rational frac(Rational[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("Wrong number of arguments.");
        }
        return new Rational(args[0].nu(), args[1].nu());
    }

    static Rational pow(Rational[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("Wrong number of arguments.");
        }
        Rational r = args[0];
        Rational power = args[1];

        // 0 ^ 0 and any negative number
        if (r.nu() == 0 && power.nu() <= 0) {
            throw new ArithmeticException("Undefined power.");
        }
        // x ^ 0
        if (power.nu() == 0) {
            return new Rational(1);
        }
        // negative base
        if (r.nu() < 0 && (power.de() & 1) == 0) {
            throw new ArithmeticException("Invalid power for a negative base.");
        }
        // negative power
        if (power.nu() < 0) {
            r = r.reciprocal();
            power = power.negate();
        }

        // currently it does not prevent overflow
        long newNum = pow(r.nu(), power.nu());
        long newDen = pow(r.de(), power.nu());
        if (power.de() == 1) {
            return new Rational(newNum, newDen);
        }

        double nextNum = Math.pow(newNum, 1.0 / power.de());
        double nextDen = Math.pow(newDen, 1.0 / power.de());
        if (((long) nextNum) == nextNum && ((long) nextDen) == nextDen) {
            return new Rational((long) nextNum, (long) nextDen);
        }
        String tmp = Double.toString(nextNum / nextDen);
        return Rational.fromDoubleStr(tmp);
    }

    private static long pow(long val, long power) {
        if (power < 0) {
            throw new UnsupportedOperationException();
        }
        if (val == power && power == 0) {
            throw new ArithmeticException("Undefined power.");
        }
        if (power == 0) {
            return 1;
        }
        if (val == 0) {
            return 0;
        }
        long oldVal = val;
        for (; power > 1; val *= oldVal, power--) ;
        return val;
    }

}
