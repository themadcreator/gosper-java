package org.numerics.continuedfranctionlong;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.numerics.FractionNumber;

public class ContinuedFractionLongs {
	public static ContinuedFractionLongNumber fromLongs(long... vals){
		List<Long> ints = new ArrayList<Long>();
		for(long val : vals){
			ints.add(val);
		}
		return new ContinuedFractionLongNumber(ints.iterator());
	}

	public static FractionNumber toFraction(Iterator<Long> iter) {
		final List<Long> terms = new ArrayList<Long>();
		while (iter.hasNext()) {
			terms.add(iter.next());
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

	public static double toDouble(Iterator<Long> iter) {
		return toFraction(iter).doubleValue();
	}
}
