package org.numerics;

import org.numerics.continuedfraction.ContinuedFractionNumber;
import org.numerics.continuedfranctionlong.ContinuedFractionLongNumber;



public class ComplexNumber<T extends Arithmetic<T>> implements Complex<T>, Arithmetic<Complex<T>> {

	public static Complex<DoubleNumber> createDouble(double re, double im) {
		return new ComplexNumber<DoubleNumber>(new DoubleNumber(re), new DoubleNumber(im));
	}

	public static Complex<ContinuedFractionNumber> createContinuedFraction(int re, int im) {
		return createContinuedFraction(new ContinuedFractionNumber(re), new ContinuedFractionNumber(im));
	}
	public static Complex<ContinuedFractionNumber> createContinuedFraction(ContinuedFractionNumber re, ContinuedFractionNumber im) {
		return new ComplexNumber<ContinuedFractionNumber>(re, im);
	}
	
	public static Complex<ContinuedFractionLongNumber> createContinuedFractionLong(int re, int im) {
		return createContinuedFractionLong(new ContinuedFractionLongNumber(re), new ContinuedFractionLongNumber(im));
	}
	public static Complex<ContinuedFractionLongNumber> createContinuedFractionLong(ContinuedFractionLongNumber re, ContinuedFractionLongNumber im) {
		return new ComplexNumber<ContinuedFractionLongNumber>(re, im);
	}

	
	private final T re;
	private final T im;
	private final T two;

	public ComplexNumber(T re, T im) {
		this(re, im, re.valueOf(2));
	}

	private ComplexNumber(T re, T im, T two) {
		this.re = re;
		this.im = im;
		this.two = two;
	}

	public T im() {
		return im;
	}

	public T re() {
		return re;
	}

	public Complex<T> add(Complex<T> c) {
		return valueOf(
				re.add(c.re()),
				im.add(c.im()));
	}

	public Complex<T> subtract(Complex<T> c) {
		return valueOf(
				re.subtract(c.re()),
				im.subtract(c.im()));
	}

	public Complex<T> multiply(Complex<T> c) {
		return valueOf(
				re.multiply(c.re()).subtract(im.multiply(c.im())),
				im.multiply(c.re()).add(re.multiply(c.im())));
	}

	public Complex<T> divide(Complex<T> c) {
		final T reNum = re.multiply(c.re()).add(im.multiply(c.im()));
		final T reDen = c.re().multiply(c.re()).add(c.im().multiply(c.im()));
		final T imNum = im.multiply(c.re()).subtract(re.multiply(c.im()));
		final T imDen = reDen;
		return valueOf(
				reNum.divide(reDen),
				imNum.divide(imDen));
	}
	
	public int compareTo(Complex<T> o) {
		final int cmp = re.compareTo(o.re());
		if (cmp != 0) return cmp;
		return im.compareTo(o.im());
	}
	
	public Complex<T> valueOf(int i) {
		return valueOf(re.valueOf(i), im.valueOf(0));
	}

	public Complex<T> valueOf(T re, T im) {
		return new ComplexNumber(re, im, two);
	}

	public Complex<T> square() {
		return valueOf(re.multiply(re).subtract(im.multiply(im)), two.multiply(re).multiply(im));
	}

	public Complex<T> scale(T scalar) {
		return valueOf(re.multiply(scalar), im.multiply(scalar));
	}

	public T magnitudeSquared() {
		return re.multiply(re).add(im.multiply(im));
	}

	@Override
	public String toString() {
		return String.format("%s + %si", re.toString(), im.toString());
	}
}
