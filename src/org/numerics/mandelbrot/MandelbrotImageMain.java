package org.numerics.mandelbrot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.numerics.Arithmetic;
import org.numerics.mandelbrot.MandelbrotDemoMain.Progress;

public class MandelbrotImageMain {
	private static final int WIDTH = 500;
	private static final int HEIGHT = 3000;

	public static void main(String[] args) throws IOException {
		// Initialize Context
		MandelbrotContext<?> context = GenericMandelbrotContext.createDouble();
		initLocation(context);

		// Create image
		BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
		MandelbrotPainter painter = new MandelbrotPainter();
		painter.fillBuffer(context, img, new Progress() {
			@Override
			public void setProgress(float p) {
				System.out.println(String.format("%.2f%%", p * 100.0));
			}
		});

		// Write image
		ImageIO.write(img, "png", new File("out.png"));
		
		// Kill all background threads
		System.exit(0);
	}

	private static <T extends Arithmetic<T>> void initLocation(MandelbrotContext<T> context) {
		context.setCenter(context.getFactory().valueOf(context.getFactory().fractionalValueOf(-3, 4), context.getFactory().fractionalValueOf(-1, 1 << 9)));
		context.setScale(context.getFactory().valueOf(context.getFactory().fractionalValueOf(1, 1 << 18), context.getFactory().valueOf(0)));
		context.setMaximumIterations(1 << 15);
	}
}
