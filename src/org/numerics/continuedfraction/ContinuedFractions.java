package org.numerics.continuedfraction;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.numerics.FractionNumber;

public class ContinuedFractions {
	public static ContinuedFractionNumber fromLongs(long... vals){
		List<BigInteger> ints = new ArrayList<BigInteger>();
		for(long val : vals){
			ints.add(BigInteger.valueOf(val));
		}
		return new ContinuedFractionNumber(ints.iterator());
	}
	
	public static String toString(Iterator<BigInteger> iter) {
		final StringBuffer sb = new StringBuffer();
		sb.append("[");
		for (int i = 0; iter.hasNext(); i++) {
			if (i == 1) {
				sb.append("; ");
			} else if (i > 1) {
				sb.append(", ");
			}
			final BigInteger next = iter.next();
			sb.append(String.valueOf(next));
		}
		sb.append("]");
		return sb.toString();
	}

	public static FractionNumber toFraction(Iterator<BigInteger> iter) {
		final List<Long> terms = new ArrayList<Long>();
		while (iter.hasNext()) {
			terms.add(iter.next().longValue());
		}
		Collections.reverse(terms);

		FractionNumber r = null;
		for (Long term : terms) {
			if (r == null) {
				r = new FractionNumber(term);
			} else {
				r = new FractionNumber(term).add(r.reciprocal());
			}
		}
		return r;
	}

	public static double toDouble(Iterator<BigInteger> iter) {
		return toFraction(iter).doubleValue();
	}
}
