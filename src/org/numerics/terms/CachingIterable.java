package org.numerics.terms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CachingIterable<T> implements Iterable<T> {
	private final List<T> terms;
	private final Iterator<T> iter;

	public CachingIterable(Iterator<T> iter) {
		this.terms = new ArrayList<T>();
		this.iter = iter;
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			private int i = 0;

			public boolean hasNext() {
				return i < terms.size() || iter.hasNext();
			}

			public T next() {
				T r;
				if (i < terms.size()) {
					r = terms.get(i);
				} else {
					r = iter.next();
					terms.add(r);
				}
				i++;
				return r;
			}

			public void remove() {
				throw new UnsupportedOperationException(
						"Cannot remove terms from a continued fraction expansion");
			}
		};
	}
}
