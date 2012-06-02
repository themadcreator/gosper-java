package org.numerics;


public class DoubleNumber implements Arithmetic<DoubleNumber> {
	private final double d;

	public DoubleNumber(double d) {
		this.d = d;
	}

	public DoubleNumber add(DoubleNumber o) {
		return new DoubleNumber(d + o.d);
	}

	public DoubleNumber subtract(DoubleNumber o) {
		return new DoubleNumber(d - o.d);
	}

	public DoubleNumber multiply(DoubleNumber o) {
		return new DoubleNumber(d * o.d);
	}

	public DoubleNumber divide(DoubleNumber o) {
		return new DoubleNumber(d / o.d);
	}

	public DoubleNumber valueOf(int i) {
		return new DoubleNumber(i);
	}

	public int compareTo(DoubleNumber o) {
		return Double.valueOf(d).compareTo(o.d);
	}
}
