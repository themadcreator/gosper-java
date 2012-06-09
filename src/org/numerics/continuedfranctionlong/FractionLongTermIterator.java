package org.numerics.continuedfranctionlong;

import java.math.BigInteger;

import org.numerics.FractionNumber;
import org.numerics.terms.TermIterator;

public class FractionLongTermIterator extends TermIterator<Long> {
	private long num;
	private long den;

	public FractionLongTermIterator(FractionNumber r) {
		// TODO check bit count for overflow
		this.num = r.num().longValue();
		this.den = r.den().longValue();
	}

	protected Long makeNext() {
		if (den == 0) return null;
		long p = num / den;
		if (p == 0) {
			long temp = den;
			den = num;
			num = temp;
		} else {
			long temp = den;
			den = num - p * den;
			num = temp;
		}
		return p;
	}
}