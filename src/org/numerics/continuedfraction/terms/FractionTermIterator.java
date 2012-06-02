package org.numerics.continuedfraction.terms;

import java.math.BigInteger;

import org.numerics.FractionNumber;

public class FractionTermIterator extends TermIterator {
	private FractionNumber r;

	public FractionTermIterator(FractionNumber r) {
		this.r = r;
	}

	protected BigInteger makeNext() {
		if (r.den().bitLength() == 0) return null;
		final BigInteger p = r.num().divide(r.den());
		if (p.bitLength() == 0) {
			r = r.reciprocal();
		} else {
			r = new FractionNumber(r.den(), r.num().subtract(p.multiply(r.den())));
		}
		return p;
	}
}