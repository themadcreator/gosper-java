package org.numerics.terms;

import java.util.Iterator;

public class Terms {
	public static <T> String toString(Iterator<T> iter) {
		final StringBuffer sb = new StringBuffer();
		sb.append("[");
		for (int i = 0; iter.hasNext(); i++) {
			if (i == 1) {
				sb.append("; ");
			} else if (i > 1) {
				sb.append(", ");
			}
			final T next = iter.next();
			sb.append(String.valueOf(next));
		}
		sb.append("]");
		return sb.toString();
	}
}
