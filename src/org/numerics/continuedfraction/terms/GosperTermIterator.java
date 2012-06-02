package org.numerics.continuedfraction.terms;

import java.math.BigInteger;
import java.util.Iterator;

import org.numerics.FractionNumber;
import org.numerics.continuedfraction.ContinuedFractionNumber;
import org.numerics.continuedfraction.ContinuedFractions;

public class GosperTermIterator extends TermIterator {
	private static final BigInteger o = BigInteger.ZERO;
	private static final BigInteger l = BigInteger.ONE;
	private static final BigInteger n = BigInteger.ONE.negate();

	public static Iterator<BigInteger> add(Iterator<BigInteger> x, Iterator<BigInteger> y) {
		final State s = new State(
				o, l, l, o,
				l, o, o, o);
		return new GosperTermIterator(x, y, s);
	}

	public static Iterator<BigInteger> subtract(Iterator<BigInteger> x, Iterator<BigInteger> y) {
		final State s = new State(
				o, l, n, o,
				l, o, o, o);
		return new GosperTermIterator(x, y, s);
	}

	public static Iterator<BigInteger> multiply(Iterator<BigInteger> x, Iterator<BigInteger> y) {
		final State s = new State(
				o, o, o, l,
				l, o, o, o);
		return new GosperTermIterator(x, y, s);
	}

	public static Iterator<BigInteger> divide(Iterator<BigInteger> x, Iterator<BigInteger> y) {
		final State s = new State(
				o, l, o, o,
				o, o, l, o);
		return new GosperTermIterator(x, y, s);
	}

	private static BigInteger absDifference(BigInteger a, BigInteger b) {
		if (a == null) return b == null ? BigInteger.ZERO : null;
		if (b == null) return null;
		return a.subtract(b).abs();
	}

	private static BigInteger divide(BigInteger num, BigInteger den) {
		if (den.abs().bitCount() == 0) return null;
		return num.divide(den);
	}

	private static int compare(BigInteger a, BigInteger b) {
		if (a == null) return b == null ? 0 : 1;
		if (b == null) return -1;
		return a.compareTo(b);
	}

	private static class State {
		private final BigInteger a, b, c, d, e, f, g, h;

		private State(
				BigInteger a, BigInteger b, BigInteger c, BigInteger d,
				BigInteger e, BigInteger f, BigInteger g, BigInteger h) {
			this.a = a;
			this.b = b;
			this.c = c;
			this.d = d;
			this.e = e;
			this.f = f;
			this.g = g;
			this.h = h;
		}

		@Override
		public String toString() {
			return String.format("[ %4d %4d  %4d  %4d ]\n[ %4d %4d  %4d  %4d ]",
					a.intValue(), b.intValue(), c.intValue(), d.intValue(),
					e.intValue(), f.intValue(), g.intValue(), h.intValue());
		}
	}

	private Iterator<BigInteger> x;
	private Iterator<BigInteger> y;
	private State s;

	private GosperTermIterator(Iterator<BigInteger> x, Iterator<BigInteger> y, State s) {
		this.x = x;
		this.y = y;
		this.s = s;
	}

	public BigInteger makeNext() {
		while (true) {
			if (isDone()) return null;

			final BigInteger r = getAgreeingR();
			if (r != null) {
				outputR(r);
				return r;
			} else if (shouldInputX()) {
				inputX();
			} else {
				inputY();
			}
		}
	}

	private boolean isDone() {
		final BigInteger n0 = divide(s.a, s.e);
		final BigInteger n1 = divide(s.b, s.f);
		final BigInteger n2 = divide(s.c, s.g);
		final BigInteger n3 = divide(s.d, s.h);
		return (n0 == null && n1 == null && n2 == null && n3 == null);
	}

	private void outputR(BigInteger r) {
		this.s = new State(
				s.e, s.f, s.g, s.h,
				s.a.subtract(s.e.multiply(r)), s.b.subtract(s.f.multiply(r)), s.c.subtract(s.g.multiply(r)), s.d.subtract(s.h.multiply(r)));
	}

	private void inputX() {
		BigInteger p;
		if (!x.hasNext() || (p = x.next()) == null) {
			this.s = new State(
					s.b, s.b, s.d, s.d,
					s.f, s.f, s.h, s.h);
		} else {
			this.s = new State(
					s.b, s.a.add(s.b.multiply(p)), s.d, s.c.add(s.d.multiply(p)),
					s.f, s.e.add(s.f.multiply(p)), s.h, s.g.add(s.h.multiply(p)));
		}
	}

	private void inputY() {
		BigInteger q;
		if (!y.hasNext() || (q = y.next()) == null) {
			this.s = new State(
					s.c, s.d, s.c, s.d,
					s.g, s.h, s.g, s.h);
		} else {
			this.s = new State(
					s.c, s.d, s.a.add(s.c.multiply(q)), s.b.add(s.d.multiply(q)),
					s.g, s.h, s.e.add(s.g.multiply(q)), s.f.add(s.h.multiply(q)));
		}
	}

	private BigInteger getAgreeingR() {
		final BigInteger n0 = divide(s.a, s.e);
		final BigInteger n1 = divide(s.b, s.f);
		final BigInteger n2 = divide(s.c, s.g);
		final BigInteger n3 = divide(s.d, s.h);
		if (compare(n0, n1) != 0 || compare(n1, n2) != 0 || compare(n2, n3) != 0) return null;
		return n0;
	}

	private boolean shouldInputX() {
		final BigInteger bf = divide(s.b, s.f);
		final BigInteger ae = divide(s.a, s.e);
		final BigInteger cg = divide(s.c, s.g);
		return compare(absDifference(bf, ae), absDifference(cg, ae)) > 0;
	}
	
	/**
	 * http://perl.plover.com/classes/cftalk/TALK/slide039.html
	 * @param args
	 */
	public static void main(String[] args) {
		final TermIterator r0 = new FractionTermIterator(new FractionNumber(13, 11));
		final TermIterator r1 = new FractionTermIterator(new FractionNumber(1, 2));
		final ContinuedFractionNumber cf = new ContinuedFractionNumber(add(r0, r1));
		System.out.println("cf: " + ContinuedFractions.toString(cf.iterator()));
		System.out.println("f: " + ContinuedFractions.toFraction(cf.iterator()));
		System.out.println("d: " + ContinuedFractions.toDouble(cf.iterator()));
	}
}
