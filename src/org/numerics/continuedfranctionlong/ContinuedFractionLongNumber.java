package org.numerics.continuedfranctionlong;

import java.util.Iterator;

import org.numerics.Arithmetic;
import org.numerics.FractionNumber;
import org.numerics.terms.CachingIterable;
import org.numerics.terms.Terms;

public class ContinuedFractionLongNumber implements
	Iterable<Long>,
	Arithmetic<ContinuedFractionLongNumber>,
	Comparable<ContinuedFractionLongNumber> {
	
	private final CachingIterable<Long> iterable;

	public ContinuedFractionLongNumber(int l) {
		this(new FractionNumber(l));
	}

	public ContinuedFractionLongNumber(FractionNumber r) {
		this(new FractionLongTermIterator(r));
	}

	public ContinuedFractionLongNumber(Iterator<Long> iter) {
		this.iterable = new CachingIterable<Long>(iter);
	}

	public ContinuedFractionLongNumber add(ContinuedFractionLongNumber o) {
		return new ContinuedFractionLongNumber(GosperLongTermIterator.add(this.iterator(), o.iterator()));
	}

	public ContinuedFractionLongNumber subtract(ContinuedFractionLongNumber o) {
		return new ContinuedFractionLongNumber(GosperLongTermIterator.subtract(this.iterator(), o.iterator()));
	}

	public ContinuedFractionLongNumber divide(ContinuedFractionLongNumber o) {
		return new ContinuedFractionLongNumber(GosperLongTermIterator.divide(this.iterator(), o.iterator()));
	}

	public ContinuedFractionLongNumber multiply(ContinuedFractionLongNumber o) {
		return new ContinuedFractionLongNumber(GosperLongTermIterator.multiply(this.iterator(), o.iterator()));
	}
	
	public ContinuedFractionLongNumber valueOf(int i) {
		return new ContinuedFractionLongNumber(i);
	}

	public ContinuedFractionLongNumber valueOf(int num, int den) {
		return new ContinuedFractionLongNumber(new FractionNumber(num, den));
	}

	public int compareTo(ContinuedFractionLongNumber o) {
		Iterator<Long> iter0 = iterator();
		Iterator<Long> iter1 = o.iterator();
		while (true) {
			if (!iter0.hasNext() && !iter1.hasNext()) {
				return 0;
			}
			if (iter0.hasNext() && !iter1.hasNext()) {
				return 1;
			}
			if (!iter0.hasNext() && iter1.hasNext()) {
				return -1;
			}

			final Long i0 = iter0.next();
			final Long i1 = iter1.next();
			final int cmp = i0.compareTo(i1);
			if (cmp != 0) return cmp;
		}
	}

	public Iterator<Long> iterator() {
		return iterable.iterator();
	}

	@Override
	public String toString() {
		return Terms.toString(iterator());
	}

	public static void main(String[] args) {
		/*
		ContinuedFractionNumber cf0 = new ContinuedFractionNumber(new FractionNumber(143, 143));
		for (int i = 0; i < 1000; i++) {
			ContinuedFractionNumber cf1 = new ContinuedFractionNumber(new FractionNumber(i, 143));
			System.out.println(i + ": " + cf0.compareTo(cf1));
		}
		*/
		
		/*
		ContinuedFractionNumber cf = new ContinuedFractionNumber(0);
		for (int i = 0; i < 10000; i++) {
			final FractionNumber r = new FractionNumber((long) (Math.random() * 100 + 1), (long) (Math.random() * 100 + 1));
			final ContinuedFractionNumber operand = new ContinuedFractionNumber(r);
			final int operator = (int) Math.random() * 4;
			if (operator % 4 == 0) {
				cf = cf.add(operand);
			} else if (operator % 4 == 1) {
				cf = cf.subtract(operand);
			} else if (operator % 4 == 2) {
				cf = cf.multiply(operand);
			} else {
				cf = cf.divide(operand);
			}
			System.out.println(cf.toString());
		}
		*/
		
		for (int i = 0; i < 1<<15; i++) {
			ContinuedFractionLongNumber cf = new ContinuedFractionLongNumber(new FractionNumber(i, 1<<15));
			System.out.println(cf.toString());
		}
	}
	
}
