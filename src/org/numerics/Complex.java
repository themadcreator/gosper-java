package org.numerics;


public interface Complex<T> extends Arithmetic<Complex<T>> {
	public T re();
	public T im();
	public Complex<T> scale(T scalar);
	public Complex<T> square();
	public T magnitudeSquared();
	public Complex<T> valueOf(T re, T im);
}