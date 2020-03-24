package com.taj.calcengine.fundamental;

import java.util.Scanner;

/*******************************************************************************
 *  Compilation:  javac Rational.java
 *  Execution:    java Rational
 *
 *  Immutable ADT for Rational numbers.
 *
 *  Invariants
 *  -----------
 *   - gcd(num, den) = 1, i.e, the rational number is in reduced form
 *   - den >= 1, the denominator is always a positive integer
 *   - 0/1 is the unique representation of 0
 *
 *  We employ some tricks to stave of overflow, but if you
 *  need arbitrary precision rationals, use BigRational.java.
 *
 ******************************************************************************/

public class Rational implements Comparable<Rational> {

    private static final Rational zero = new Rational(0, 1);

    private long num;   // the numerator
    private long den;   // the denominator

    public static Rational zero() {
        return zero;
    }

    public Rational(long numerator) {
        this(numerator, 1);
    }

    // create and initialize a new Rational object
    public Rational(long numerator, long denominator) {

        if (denominator == 0) {
            throw new ArithmeticException("denominator is zero");
        }
        
        // reduce fraction
        long g = gcd(numerator, denominator);
        num = numerator / g;
        den = denominator / g;

        // needed only for negative numbers
        if (den < 0) {
            den = -den;
            num = -num;
        }
    }

    public static Rational fromDoubleStr(String s) {
        int ind = s.indexOf('.');
        if (ind == -1) {
            return new Rational(Long.parseLong(s));
        }
        int exp = s.length() - ind;
        long de = 1;
        while (exp-- > 1) {
            de *= 10;
        }
        return new Rational(Long.parseLong(s.replace(".", "")), de);
    }

    // return the numerator and denominator of (this)
    public long nu() {
        return num;
    }

    public long de() {
        return den;
    }

    // return double precision representation of (this)
    public double toDouble() {
        return (double) num / den;
    }

    // return string representation of (this)
    public String toString() {
        if (den == 1) return num + "";
        else return num + "/" + den;
    }

    // return { -1, 0, +1 } if a < b, a = b, or a > b
    public int compareTo(Rational b) {
        Rational a = this;
        long lhs = a.num * b.den;
        long rhs = a.den * b.num;
        if (lhs < rhs) return -1;
        if (lhs > rhs) return +1;
        return 0;
    }

    // is this Rational object equal to y?
    public boolean equals(Object y) {
        if (y == null) return false;
        if (y.getClass() != this.getClass()) return false;
        Rational b = (Rational) y;
        return compareTo(b) == 0;
    }

    // hashCode consistent with equals() and compareTo()
    // (better to hash the numerator and denominator and combine)
    public int hashCode() {
        return this.toString().hashCode();
    }


    // create and return a new rational (r.num + s.num) / (r.den + s.den)
    public static Rational mediant(Rational r, Rational s) {
        return new Rational(r.num + s.num, r.den + s.den);
    }

    // return gcd(|m|, |n|)
    private static long gcd(long m, long n) {
        if (m < 0) m = -m;
        if (n < 0) n = -n;
        if (0 == n) return m;
        else return gcd(n, m % n);
    }

    // return lcm(|m|, |n|)
    private static long lcm(long m, long n) {
        if (m < 0) m = -m;
        if (n < 0) n = -n;
        return m * (n / gcd(m, n));    // parentheses important to avoid overflow
    }

    // return a * b, staving off overflow as much as possible by cross-cancellation
    public Rational times(Rational b) {
        Rational a = this;

        // reduce p1/q2 and p2/q1, then multiply, where a = p1/q1 and b = p2/q2
        Rational c = new Rational(a.num, b.den);
        Rational d = new Rational(b.num, a.den);
        return new Rational(c.num * d.num, c.den * d.den);
    }

    // return a + b, staving off overflow
    public Rational plus(Rational b) {
        Rational a = this;

        // special cases
        if (a.compareTo(zero) == 0) return b;
        if (b.compareTo(zero) == 0) return a;

        // Find gcd of numerators and denominators
        long f = gcd(a.num, b.num);
        long g = gcd(a.den, b.den);

        // add cross-product terms for numerator
        Rational s = new Rational((a.num / f) * (b.den / g) + (b.num / f) * (a.den / g),
                lcm(a.den, b.den));

        // multiply back in
        s.num *= f;
        return s;
    }

    // return -a
    public Rational negate() {
        return new Rational(-num, den);
    }

    // return |a|
    public Rational abs() {
        if (num >= 0) return this;
        else return negate();
    }

    // return a - b
    public Rational minus(Rational b) {
        Rational a = this;
        return a.plus(b.negate());
    }

    public Rational reciprocal() {
        return new Rational(den, num);
    }

    // return a / b
    public Rational dividedBy(Rational b) {
        Rational a = this;
        return a.times(b.reciprocal());
    }
}