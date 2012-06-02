package org.numerics.mandelbrot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class MandelbrotMain {

	public static final class MandelbrotPainter {
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

	public static class PaintController {

		private static ThreadPoolExecutor makeExecutor() {
			return new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
		}
		
		private final MandelbrotPainter painter;
		private final Progress progress;
		private final BufferedJPanel panel;
		private ExecutorService executor;
		private MandelbrotContext<?> context;
		
		public PaintController(BufferedJPanel panel,Progress progress, MandelbrotContext<?> context) {
			this.context = context;
			this.painter = new MandelbrotPainter();
			this.progress = progress;
			this.panel = panel;
			this.panel.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					final int direction;
					if (e.getButton() == MouseEvent.BUTTON1) {
						direction = 1;
					} else if (e.getButton() == MouseEvent.BUTTON3) {
						direction = -1;
					} else {
						direction = 0;
					}

					if (direction != 0) {
						final Point p = e.getPoint();
						final Component c = e.getComponent();
						zoomImage(direction, p.x, p.y, c.getWidth(), c.getHeight());
						PaintController.this.context = PaintController.this.context.zoom(direction, p.x, p.y, c.getWidth(), c.getHeight());
						enqueueJob();
					}
				}
			});
			
			this.panel.addComponentListener(new ComponentListener() {
				public void componentShown(ComponentEvent e) {
					enqueueJob();
				}
				
				public void componentResized(ComponentEvent e) {
					enqueueJob();
				}
				
				public void componentMoved(ComponentEvent e) {}
				public void componentHidden(ComponentEvent e) {}
			});
		}
		
		public void zoomImage(int direction, int x, int y, int w, int h){
			final int dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2;

			if (direction > 0) {
				dx1 = 0;
				dy1 = 0;
				dx2 = w;
				dy2 = h;

				sx1 = x / 2;
				sy1 = y / 2;
				sx2 = (w - x) / 2 + x;
				sy2 = (h - y) / 2 + y;
			} else {
				dx1 = x/2;
				dy1 = y/2;
				dx2 = (w - x) / 2 + x;
				dy2 = (h - y) / 2 + y;

				sx1 = 0;
				sy1 = 0;
				sx2 = w;
				sy2 = h;
			}
			
			final BufferedImage panelBuffer = panel.getBuffer();
			final BufferedImage buffer = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
			final Graphics g = buffer.getGraphics();
			g.setColor(Color.BLACK);
			g.fillRect(0,0,w,h);
			g.drawImage(panelBuffer, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, panel);
			panel.setBuffer(buffer);
		}

		public void enqueueJob() {
			if (executor != null) executor.shutdownNow();
			final MandelbrotContext<?> jobContext = context;
			executor = makeExecutor();
			executor.submit(new Runnable() {
				@Override
				public void run() {
					final int w = panel.getWidth();
					final int h = panel.getHeight();
					final BufferedImage buffer = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
					painter.fillBuffer(jobContext, buffer, progress);
					//painter.fillBufferSingleThreaded(jobContext, buffer, progress);
					progress.setProgress(0);
					if (Thread.currentThread().isInterrupted()) return;
					panel.setBuffer(buffer);
				}
			});
		}
	}

	public static final class BufferedJPanel extends JPanel {
		private BufferedImage buffer;

		public BufferedJPanel() {
			this.buffer = null;
		}

		protected void paintComponent(Graphics g) {
			if (buffer == null) {
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, getWidth(), getHeight());
			} else {
				g.drawImage(buffer, 0, 0, this);
			}
		}
		
		public BufferedImage getBuffer() {
			return buffer;
		}

		public void setBuffer(BufferedImage buffer) {
			this.buffer = buffer;
			repaint();
		}
	}
	
	public static interface Progress {
		public void setProgress(float p);
	}
	
	public static final class ProgressIncrementor {
		private final Progress progress;
		private final float maxValue;
		private float value;

		public ProgressIncrementor(Progress progress, float maxValue) {
			this.progress = progress;
			this.maxValue = maxValue;
			this.value = 0.0f;
		}

		public synchronized void increment() {
			value += 1.0f;
			progress.setProgress(value / maxValue);
		}
	}
	
	public static final class ProgressPanel extends JPanel implements Progress {
		public float progress = 0.0f;

		public synchronized void setProgress(float progress) {
			this.progress = progress;
			repaint();
		}

		protected void paintComponent(Graphics g) {
			g.setColor(Color.BLACK);
			int w = getWidth();
			int h = getHeight();
			g.fillRect(0, 0, w, h);
			g.setColor(Color.GREEN);
			g.fillRect(0, 0, (int) (w * progress), h);
		}
	}

	private final JPanel panel;
	private final BufferedJPanel bufferPanel;
	private final ProgressPanel progress;
	private final PaintController paintManager;

	public MandelbrotMain(MandelbrotContext<?> context) {
		this.panel = new JPanel();
		this.bufferPanel = new BufferedJPanel();
		this.progress = new ProgressPanel();
		this.paintManager = new PaintController(bufferPanel, progress, context);

		panel.setLayout(new BorderLayout());
		panel.add(bufferPanel, BorderLayout.CENTER);
		panel.add(progress, BorderLayout.SOUTH);
	}

	public static void main(String[] args) {
		final MandelbrotContext<?> context = GenericMandelbrotContext.createDouble();
		final MandelbrotMain demo = new MandelbrotMain(context);
		
		final JFrame frame = new JFrame();
		frame.setLocation(0, 0);
		frame.setSize(100, 100);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(demo.panel, BorderLayout.CENTER);
		frame.setVisible(true);
	}
}
