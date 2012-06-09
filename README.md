gosper
======

This package includes classes for Rational arithmetic using a variety of numerical methods:

* Doubles
* Fractions (a quotient of BigIntegers)
* Continued Fractions (with BigInteger or Long terms)
* Complex Rational Numbers (generic on the other rational numbers)

The string representation for continued fractions is a list of the terms, the first term separated by a semicolon being the integer part. For example:
* [1; 2, 3] = 1 + 1 / (2 + 1 / 3)

### Gosper's method ###
The continued fraction implementation uses an as-needed term iterator. That means that we do not compute the fully expanded continued fraction during arithmetic, and can generate individual terms iteratively.

This makes arithmetic methods very fast, but evaluation slow. For example, comparison between continued fractions will only compute the terms necessary to determine the comparison. To compare the following two continued fracitons:

* [1; 2, 42, 87, 1, 23]
* [1; 2, 42, 9, 32, 4, 12, 6]

Only the first *four* terms will be generated before we determine the comparison.

Check out this excellent talk about the details of continued fraction arithmetic and the gosper algorithm:
http://perl.plover.com/classes/cftalk/

### Mandelbrot Demo ###

This package also include a mandelbrot set renderer demo, which uses the numerics classes.

* MandelbrotDemoMain.java - an interactive pan-and-zoom Mandelbrot demo.
* MandelbrotImageMain.java - a standalone Mandelbrot image renderer.