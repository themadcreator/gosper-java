package org.numerics.mandelbrot;

import org.numerics.Arithmetic;
import org.numerics.Complex;
import org.numerics.ComplexNumber;
import org.numerics.DoubleNumber;
import org.numerics.continuedfraction.ContinuedFractionNumber;
import org.numerics.continuedfranctionlong.ContinuedFractionLongNumber;


public class GenericMandelbrotContext<T extends Arithmetic<T>> implements MandelbrotContext<T> {

	public static MandelbrotContext<DoubleNumber> createDouble() {
		final Complex<DoubleNumber> zero = ComplexNumber.createDouble(0.0, 0.0);
		final NumberFactory<DoubleNumber> factory = new NumberFactory<DoubleNumber>(zero);
		return new GenericMandelbrotContext<DoubleNumber>(factory);
	}

	public static MandelbrotContext<ContinuedFractionNumber> createContinuedFraction() {
		final Complex<ContinuedFractionNumber> zero = ComplexNumber.createContinuedFraction(0, 0);
		final NumberFactory<ContinuedFractionNumber> factory = new NumberFactory<ContinuedFractionNumber>(zero);
		return new GenericMandelbrotContext<ContinuedFractionNumber>(factory);
	}
	
	public static MandelbrotContext<ContinuedFractionLongNumber> createContinuedFractionLong() {
		final Complex<ContinuedFractionLongNumber> zero = ComplexNumber.createContinuedFractionLong(0, 0);
		final NumberFactory<ContinuedFractionLongNumber> factory = new NumberFactory<ContinuedFractionLongNumber>(zero);
		return new GenericMandelbrotContext<ContinuedFractionLongNumber>(factory);
	}

	public static class NumberFactory<T extends Arithmetic<T>> {
		private final Complex<T> zero;

		public NumberFactory(Complex<T> zero) {
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

		public T valueOf(int i) {
			return zero.re().valueOf(i);
		}

		public T fractionalValueOf(int num, int den) {
			return valueOf(num).divide(valueOf(den));
		}
	}

	private final NumberFactory<T> factory;
	private Complex<T> center;
	private Complex<T> scale;
	private int maximumIterations = 1 << 8;
	private final T half;
	private final T two;
	private final T four;

	public GenericMandelbrotContext(NumberFactory<T> factory) {
		this.factory = factory;
		this.center = factory.valueOf(-1, 0);
		this.scale = factory.valueOf(factory.fractionalValueOf(1, 100), factory.valueOf(0));

		this.half = factory.fractionalValueOf(1, 2);
		this.two = factory.fractionalValueOf(2, 1);
		this.four = factory.valueOf(4);
	}

	public int getMaximumIterations() {
		return maximumIterations;
	}

	public void setMaximumIterations(int maximumIterations) {
		this.maximumIterations = maximumIterations;
	}

	public Complex<T> getCenter() {
		return center;
	}

	public void setCenter(Complex<T> c) {
		this.center = c;
	}

	public Complex<T> getScale() {
		return scale;
	}

	public void setScale(Complex<T> scale) {
		this.scale = scale;
	}

	/**
	 * Mandelbrot calculation. From definition.
	 */
	public int getMandelbrotValue(int x, int y, int w, int h) {
		Complex<T> z = factory.getZero();
		Complex<T> c = getLocation(x, y, w, h);
		//System.out.println(String.format("%d %d %s", x, y, c));
		int i = 0;
		while (!hasEscaped(z) && i < maximumIterations) {
			z = z.square().add(c);
			i++;
		}

		if (i == maximumIterations) {
			return -1;
		} else {
			return i;
		}
	}

	/**
	 * Calculates the position on the complex plain given the screen
	 * coordinates.
	 */
	public Complex<T> getLocation(int x, int y, int w, int h) {
		final T xx = factory.valueOf(x).add(half).subtract(factory.valueOf(w).multiply(half));
		final T yy = factory.valueOf(y).add(half).subtract(factory.valueOf(h).multiply(half));
		
		Complex<T> c = scale.valueOf(xx, yy);
		c = c.scale(scale.re());
		c = c.add(center);
		return c;
	}

	public boolean hasEscaped(Complex<T> c) {
		return c.magnitudeSquared().compareTo(four) > 0;
	}

	/**
	 * Returns a new MandelbrotContext with a new scale and center determined by
	 * the coordinates x,y in a window of size w,h.
	 */
	public MandelbrotContext<T> zoom(int direction, int x, int y, int w, int h) {
		final MandelbrotContext<T> c = create();

		final T factor = direction < 0 ? two : half;
		Complex<T> u = getLocation(x, y, w, h);
		Complex<T> v = getCenter().subtract(u);
		v = v.scale(factor);
		v = u.add(v);

		Complex<T> s = getScale();
		s = s.scale(factor);

		c.setCenter(v);
		c.setScale(s);
		return c;
	}

	@SuppressWarnings("unchecked")
	private MandelbrotContext<T> create() {
		return new GenericMandelbrotContext(factory);
	}
}
