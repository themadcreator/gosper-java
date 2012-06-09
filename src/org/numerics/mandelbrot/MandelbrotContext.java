package org.numerics.mandelbrot;

import org.numerics.Arithmetic;
import org.numerics.Complex;

public interface MandelbrotContext<T extends Arithmetic<T>> {
	public int getMandelbrotValue(int x, int y, int w, int h);
	public Complex<T> getLocation(int x, int y, int w, int h);
	public boolean hasEscaped(Complex<T> c);

	public ComplexNumberFactory<T> getFactory();
	public MandelbrotContext<T> zoom(int amount, int x, int y, int w, int h);

	public Complex<T> getCenter();
	public void setCenter(Complex<T> c);
	
	public Complex<T> getScale();
	public void setScale(Complex<T> c);
	
 	public void setMaximumIterations(int i);
	public int getMaximumIterations();
}