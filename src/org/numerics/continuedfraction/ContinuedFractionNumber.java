package org.numerics.continuedfraction;

import java.math.BigInteger;
import java.util.Iterator;

import org.numerics.Arithmetic;
import org.numerics.FractionNumber;
import org.numerics.terms.CachingIterable;
import org.numerics.terms.Terms;

public class ContinuedFractionNumber implements
	Iterable<BigInteger>,
	Arithmetic<ContinuedFractionNumber>,
	Comparable<ContinuedFractionNumber> {
	
	private final CachingIterable<BigInteger> iterable;

	public ContinuedFractionNumber(int l) {
		this(new FractionNumber(l));
	}

	public ContinuedFractionNumber(FractionNumber r) {
		this(new FractionTermIterator(r));
	}

	public ContinuedFractionNumber(Iterator<BigInteger> iter) {
		this.iterable = new CachingIterable<BigInteger>(iter);
	}

	public ContinuedFractionNumber add(ContinuedFractionNumber o) {
		return new ContinuedFractionNumber(GosperTermIterator.add(this.iterator(), o.iterator()));
	}

	public ContinuedFractionNumber subtract(ContinuedFractionNumber o) {
		return new ContinuedFractionNumber(GosperTermIterator.subtract(this.iterator(), o.iterator()));
	}

	public ContinuedFractionNumber divide(ContinuedFractionNumber o) {
		return new ContinuedFractionNumber(GosperTermIterator.divide(this.iterator(), o.iterator()));
	}

	public ContinuedFractionNumber multiply(ContinuedFractionNumber o) {
		return new ContinuedFractionNumber(GosperTermIterator.multiply(this.iterator(), o.iterator()));
	}
	
	public ContinuedFractionNumber valueOf(int i) {
		return new ContinuedFractionNumber(i);
	}

	public ContinuedFractionNumber valueOf(int num, int den) {
		return new ContinuedFractionNumber(new FractionNumber(num, den));
	}

	public int compareTo(ContinuedFractionNumber o) {
		Iterator<BigInteger> iter0 = iterator();
		Iterator<BigInteger> iter1 = o.iterator();
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

			final BigInteger i0 = iter0.next();
			final BigInteger i1 = iter1.next();
			final int cmp = i0.compareTo(i1);
			if (cmp != 0) return cmp;
		}
	}

	public Iterator<BigInteger> iterator() {
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
			ContinuedFractionNumber cf = new ContinuedFractionNumber(new FractionNumber(i, 1<<15));
			System.out.println(cf.toString());
		}
	}
	
}
