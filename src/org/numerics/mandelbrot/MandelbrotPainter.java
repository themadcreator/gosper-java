package org.numerics.mandelbrot;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.numerics.mandelbrot.MandelbrotDemoMain.Progress;
import org.numerics.mandelbrot.MandelbrotDemoMain.ProgressIncrementor;

public final class MandelbrotPainter {
	private final int[] colorTable;
	
	public MandelbrotPainter() {
		this.colorTable = new int[500];
		for (int i = 0; i < 500; i++) {
			int rgb = Color.HSBtoRGB(i / 500.0f, 1.0f, 0.75f);
			colorTable[i] = rgb;
		}
	}

	public void fillBuffer(final MandelbrotContext<?> context, final BufferedImage img, Progress progress) {
		final int w = img.getWidth();
		final int h = img.getHeight();
		
		final int threads = 4;
		final ThreadPoolExecutor executor = makeExecutor(threads);
		final CountDownLatch latch = new CountDownLatch(h);
		final ProgressIncrementor incProgress = new ProgressIncrementor(progress, w * h);

		for (int y = 0; y < h; y++) {
			final int scanlineY = y;
			executor.submit(new Runnable() {
				public void run() {
					final BufferedImage scanline = getScanline(context, incProgress, w, h, scanlineY);
					synchronized (img) {
						img.getGraphics().drawImage(scanline, 0, scanlineY, w, scanlineY + 1, 0, 0, w, 1, null);
					}
					latch.countDown();
				}
			});
		}
		
		try {
			latch.await();
		} catch (InterruptedException e) {
			executor.shutdownNow();
			Thread.currentThread().interrupt();
			return;
		}
	}

	public void fillBufferSingleThreaded(final MandelbrotContext<?> context, final BufferedImage img, Progress progress) {
		final int w = img.getWidth();
		final int h = img.getHeight();

		final ProgressIncrementor incProgress = new ProgressIncrementor(progress, w * h);

		for (int y = 0; y < h; y++) {
			final int scanlineY = y;
			final BufferedImage scanline = getScanline(context, incProgress, w, h, scanlineY);
			img.getGraphics().drawImage(scanline, 0, scanlineY, w, scanlineY + 1, 0, 0, w, 1, null);
		}
	}

	private ThreadPoolExecutor makeExecutor(final int threads) {
		return new ThreadPoolExecutor(threads, threads, 1, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
	}

	private BufferedImage getScanline(
			MandelbrotContext<?> context,
			ProgressIncrementor progress, int w, int h, int y) {
		
		final BufferedImage scanline = new BufferedImage(w, 1, BufferedImage.TYPE_4BYTE_ABGR);
		final Graphics2D g = (Graphics2D) scanline.getGraphics();
		for (int x = 0; x < w; x++) {
			int v = context.getMandelbrotValue(x, y, w, h);
			if (v < 0) {
				g.setColor(Color.BLACK);
			} else {
				g.setColor(new Color(colorTable[v % colorTable.length]));
			}
			g.fillRect(x, 0, 1, 1);
			progress.increment();
		}
		return scanline;
	}
}