package org.numerics;

import java.math.BigInteger;


public class FractionNumber implements Arithmetic<FractionNumber> {
	private BigInteger num;
	private BigInteger den;
	
	public FractionNumber(long num) {
		this(num, 1);
	}

	public FractionNumber(long num, long den) {
		this(BigInteger.valueOf(num), BigInteger.valueOf(den));
	}

	public FractionNumber(BigInteger num, BigInteger den) {
		this.num = num;
		this.den = den;
		reduce();
	}

	public BigInteger num() {
		return num;
	}

	public BigInteger den() {
		return den;
	}

	protected void reduce() {
		final BigInteger gcd = num.gcd(den);
		num = num.divide(gcd);
		den = den.divide(gcd);

		// keep negative on top
		if (den.signum() == -1) {
			den.negate();
			num.negate();
		}
	}

	public FractionNumber pow(int pow) {
		return new FractionNumber(num.pow(pow), den.pow(pow));
	}

	public FractionNumber add(FractionNumber r) {
		return new FractionNumber(
				(num.multiply(r.den)).add(r.num.multiply(den)),
				den.multiply(r.den));
	}

	public FractionNumber subtract(FractionNumber r) {
		return new FractionNumber(
				(num.multiply(r.den)).subtract(r.num.multiply(den)),
				den.multiply(r.den));	
	}

	public FractionNumber multiply(FractionNumber r) {
		return new FractionNumber(
				num.multiply(r.num),
				den.multiply(r.den));
	}

	public FractionNumber divide(FractionNumber r) {
		return new FractionNumber(
				den.multiply(r.den),
				num.multiply(r.num));
	}

	public FractionNumber valueOf(int i) {
		return new FractionNumber(i);
	}

	public FractionNumber valueOf(int num, int den) {
		return new FractionNumber(num, den);
	}
	
	public FractionNumber reciprocal() {
		return new FractionNumber(den, num);
	}
	
	public int powerDifference(){
		return num.bitLength() - den.bitLength();
	}
	
	public int maxBitLength() {
		return Math.max(num.bitLength(), den.bitLength());
	}
	
	public double doubleValue() {
		return (double) num.longValue() / den.longValue();
	}
	
	public int compareTo(FractionNumber o) {
		return this.subtract(o).num().compareTo(BigInteger.ZERO);
	}
	
	@Override
	public String toString() {
		return String.format("%s/%s", num.toString(), den.toString());
	}

	public static void main(String[] args) {
		FractionNumber r = new FractionNumber(1, 2);
		r = r.add(new FractionNumber(1,3));
		r = r.add(new FractionNumber(3,2));
		r = r.subtract(new FractionNumber(3,2));
		r = r.subtract(new FractionNumber(1,3));
		r = r.multiply(new FractionNumber(2,1));
		System.out.println("1 == " + r.doubleValue());

		System.out.println("2 == " + new FractionNumber(4,1).powerDifference());
		System.out.println("2 == " + new FractionNumber(1<<10,1<<8).powerDifference());
	}
}
