package org.numerics.continuedfraction.terms;

import java.math.BigInteger;
import java.util.Iterator;

public abstract class TermIterator implements Iterator<BigInteger> {
	private boolean valid = false;
	private boolean done = false;
	private BigInteger cached = null;

	protected abstract BigInteger makeNext();

	public boolean hasNext() {
		validate();
		return cached != null;
	}

	public BigInteger next() {
		validate();
		valid = false;
		return cached;
	}

	public void remove() {
		throw new UnsupportedOperationException(
				"Cannot remove terms from a continued fraction expansion");
	}

	private void validate() {
		if (!done && !valid) {
			cached = makeNext();
			valid = true;
			if (cached == null) {
				done = true;
			}
		}
	}
}