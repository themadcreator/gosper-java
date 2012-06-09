package org.numerics.mandelbrot;

import org.numerics.Arithmetic;
import org.numerics.Complex;

public class ComplexNumberFactory<T extends Arithmetic<T>> {
	private final Complex<T> zero;

	public ComplexNumberFactory(Complex<T> zero) {
		this.zero = zero;
	}

	public Complex<T> getZero() {
		return zero;
	}

	public Complex<T> valueOf(int re, int im) {
		return valueOf(valueOf(re), valueOf(im));
	}

	public Complex<T> valueOf(T re, T im) {
		return zero.valueOf(re, im);
	}
	
	public Complex<T> fractionalValueOf(int reNum, int reDen, int imNum, int imDen) {
		return valueOf(fractionalValueOf(reNum, reDen), fractionalValueOf(imNum, imDen));
	}

	public T valueOf(int i) {
		return zero.re().valueOf(i);
	}

	public T fractionalValueOf(int num, int den) {
		return valueOf(num).divide(valueOf(den));
	}
}