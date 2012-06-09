package org.numerics.terms;

import java.util.Iterator;

public abstract class TermIterator<T> implements Iterator<T> {
	private boolean valid = false;
	private boolean done = false;
	private T cached = null;

	protected abstract T makeNext();

	public boolean hasNext() {
		validate();
		return cached != null;
	}

	public T next() {
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