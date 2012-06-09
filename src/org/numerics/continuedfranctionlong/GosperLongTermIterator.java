package org.numerics.continuedfranctionlong;

import java.util.Iterator;

import org.numerics.FractionNumber;
import org.numerics.terms.TermIterator;
import org.numerics.terms.Terms;

public class GosperLongTermIterator extends TermIterator<Long> {
	private static final Long o = 0L;
	private static final Long l = 1L;
	private static final Long n = -1L;

	public static Iterator<Long> add(Iterator<Long> x, Iterator<Long> y) {
		final State s = new State(
				o, l, l, o,
				l, o, o, o);
		return new GosperLongTermIterator(x, y, s);
	}

	public static Iterator<Long> subtract(Iterator<Long> x, Iterator<Long> y) {
		final State s = new State(
				o, l, n, o,
				l, o, o, o);
		return new GosperLongTermIterator(x, y, s);
	}

	public static Iterator<Long> multiply(Iterator<Long> x, Iterator<Long> y) {
		final State s = new State(
				o, o, o, l,
				l, o, o, o);
		return new GosperLongTermIterator(x, y, s);
	}

	public static Iterator<Long> divide(Iterator<Long> x, Iterator<Long> y) {
		final State s = new State(
				o, l, o, o,
				o, o, l, o);
		return new GosperLongTermIterator(x, y, s);
	}

	private static Long absDifference(Long a, Long b) {
		if (a == null) return b == null ? 0L : null;
		if (b == null) return null;
		return Math.abs(a - b);
	}

	private static Long divide(Long num, Long den) {
		if (den == 0) return null;
		return num / den;
	}

	private static int compare(Long a, Long b) {
		if (a == null) return b == null ? 0 : 1;
		if (b == null) return -1;
		return a.compareTo(b);
	}

	private static class State {
		private final Long a, b, c, d, e, f, g, h;

		private State(
				Long a, Long b, Long c, Long d,
				Long e, Long f, Long g, Long h) {
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

	private Iterator<Long> x;
	private Iterator<Long> y;
	private State s;

	private GosperLongTermIterator(Iterator<Long> x, Iterator<Long> y, State s) {
		this.x = x;
		this.y = y;
		this.s = s;
	}

	public Long makeNext() {
		while (true) {
			if (isDone()) return null;

			final Long r = getAgreeingR();
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
		final Long n0 = divide(s.a, s.e);
		final Long n1 = divide(s.b, s.f);
		final Long n2 = divide(s.c, s.g);
		final Long n3 = divide(s.d, s.h);
		return (n0 == null && n1 == null && n2 == null && n3 == null);
	}

	private void outputR(Long r) {
		this.s = new State(
				s.e, s.f, s.g, s.h,
				s.a - s.e * r, s.b - s.f * r, s.c - s.g * r, s.d - s.h * r);
	}

	private void inputX() {
		Long p;
		if (!x.hasNext() || (p = x.next()) == null) {
			this.s = new State(
					s.b, s.b, s.d, s.d,
					s.f, s.f, s.h, s.h);
		} else {
			this.s = new State(
					s.b, s.a + s.b * p, s.d, s.c + s.d * p,
					s.f, s.e + s.f * p, s.h, s.g + s.h * p);
		}
	}

	private void inputY() {
		Long q;
		if (!y.hasNext() || (q = y.next()) == null) {
			this.s = new State(
					s.c, s.d, s.c, s.d,
					s.g, s.h, s.g, s.h);
		} else {
			this.s = new State(
					s.c, s.d, s.a + s.c * q, s.b + s.d * q,
					s.g, s.h, s.e + s.g * q, s.f + s.h * q);
		}
	}

	private Long getAgreeingR() {
		final Long n0 = divide(s.a, s.e);
		final Long n1 = divide(s.b, s.f);
		final Long n2 = divide(s.c, s.g);
		final Long n3 = divide(s.d, s.h);
		if (compare(n0, n1) != 0 || compare(n1, n2) != 0 || compare(n2, n3) != 0) return null;
		return n0;
	}

	private boolean shouldInputX() {
		final Long bf = divide(s.b, s.f);
		final Long ae = divide(s.a, s.e);
		final Long cg = divide(s.c, s.g);
		return compare(absDifference(bf, ae), absDifference(cg, ae)) > 0;
	}
	
	/**
	 * http://perl.plover.com/classes/cftalk/TALK/slide039.html
	 * @param args
	 */
	public static void main(String[] args) {
		final TermIterator<Long> r0 = new FractionLongTermIterator(new FractionNumber(13, 11));
		final TermIterator<Long> r1 = new FractionLongTermIterator(new FractionNumber(1, 2));
		final ContinuedFractionLongNumber cf = new ContinuedFractionLongNumber(add(r0, r1));
		System.out.println("cf: " + Terms.toString(cf.iterator()));
		System.out.println("f: " + ContinuedFractionLongs.toFraction(cf.iterator()));
		System.out.println("d: " + ContinuedFractionLongs.toDouble(cf.iterator()));
	}
}
