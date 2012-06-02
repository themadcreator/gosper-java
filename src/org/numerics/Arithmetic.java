package org.numerics;

public interface Arithmetic<T extends Arithmetic<T>> extends Comparable<T> {
	public T add(T o);
	public T subtract(T o);
	public T multiply(T o);
	public T divide(T o);
	public T valueOf(int i);
}
