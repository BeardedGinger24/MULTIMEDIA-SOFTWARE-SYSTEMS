package HW4;

/*******************************************************
 CS4551 Multimedia Software Systems
 @ Author: Elaine Kang

 This image class is for a 24bit RGB image only.
 *******************************************************/

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import javax.imageio.stream.FileImageInputStream;

// A wrapper class of BufferedImage
// Provide a couple of utility functions such as reading from and writing to PPM file

public class Image {
	private BufferedImage img;
	private String fileName; // Input file name
	private int pixelDepth = 3; // pixel depth in byte
	private static int[][] lookUpTable = new int[3][256];

	public Image(int w, int h)
	// create an empty image with w(idth) and h(eight)
	{
		fileName = "";
		img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		System.out.println("Created an empty image with size " + w + "x" + h);
	}

	public Image(String fn)
	// Create an image and read the data from the file
	{
		fileName = fn;
		readPPM(fileName);
		System.out.println("Created an image from " + fileName + " with size " + getW() + "x" + getH());
	}

	public int getW() {
		return img.getWidth();
	}

	public int getH() {
		return img.getHeight();
	}

	public int getSize()
	// return the image size in byte
	{
		return getW() * getH() * pixelDepth;
	}

	public void setPixel(int x, int y, byte[] rgb)
	// set byte rgb values at (x,y)
	{
		int pix = 0xff000000 | ((rgb[0] & 0xff) << 16) | ((rgb[1] & 0xff) << 8) | (rgb[2] & 0xff);
		img.setRGB(x, y, pix);
	}

	public void setPixel(int x, int y, int[] irgb)
	// set int rgb values at (x,y)
	{
		byte[] rgb = new byte[3];

		for (int i = 0; i < 3; i++)
			rgb[i] = (byte) irgb[i];

		setPixel(x, y, rgb);
	}

	public void getPixel(int x, int y, byte[] rgb)
	// retreive rgb values at (x,y) and store in the byte array
	{
		int pix = img.getRGB(x, y);

		rgb[2] = (byte) pix;
		rgb[1] = (byte) (pix >> 8);
		rgb[0] = (byte) (pix >> 16);
	}

	public void getPixel(int x, int y, int[] rgb)
	// retreive rgb values at (x,y) and store in the int array
	{
		int pix = img.getRGB(x, y);

		byte b = (byte) pix;
		byte g = (byte) (pix >> 8);
		byte r = (byte) (pix >> 16);

		// converts singed byte value (~128-127) to unsigned byte value (0~255)
		rgb[0] = (int) (0xFF & r);
		rgb[1] = (int) (0xFF & g);
		rgb[2] = (int) (0xFF & b);
	}

	public void displayPixelValue(int x, int y)
	// Display rgb pixel in unsigned byte value (0~255)
	{
		int pix = img.getRGB(x, y);

		byte b = (byte) pix;
		byte g = (byte) (pix >> 8);
		byte r = (byte) (pix >> 16);

		System.out.println(
				"RGB Pixel value at (" + x + "," + y + "):" + (0xFF & r) + "," + (0xFF & g) + "," + (0xFF & b));
	}

	public void readPPM(String fileName)
	// read a data from a PPM file
	{
		File fIn = null;
		FileImageInputStream fis = null;

		try {
			fIn = new File(fileName);
			fis = new FileImageInputStream(fIn);

			System.out.println("Reading " + fileName + "...");

			// read Identifier
			if (!fis.readLine().equals("P6")) {
				System.err.println("This is NOT P6 PPM. Wrong Format.");
				System.exit(0);
			}

			// read Comment line
			String commentString = fis.readLine();

			// read width & height
			String[] WidthHeight = fis.readLine().split(" ");
			int width = Integer.parseInt(WidthHeight[0]);
			int height = Integer.parseInt(WidthHeight[1]);

			// read maximum value
			int maxVal = Integer.parseInt(fis.readLine());

			if (maxVal != 255) {
				System.err.println("Max val is not 255");
				System.exit(0);
			}

			// read binary data byte by byte and save it into BufferedImage object
			int x, y;
			byte[] rgb = new byte[3];
			img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

			for (y = 0; y < getH(); y++) {
				for (x = 0; x < getW(); x++) {
					rgb[0] = fis.readByte();
					rgb[1] = fis.readByte();
					rgb[2] = fis.readByte();
					setPixel(x, y, rgb);
				}
			}

			fis.close();

			System.out.println("Read " + fileName + " Successfully.");

		} // try
		catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public void write2PPM(String fileName)
	// wrrite the image data in img to a PPM file
	{
		FileOutputStream fos = null;
		PrintWriter dos = null;

		try {
			fos = new FileOutputStream(fileName);
			dos = new PrintWriter(fos);

			System.out.println("Writing the Image buffer into " + fileName + "...");

			// write header
			dos.print("P6" + "\n");
			dos.print("#CS451" + "\n");
			dos.print(getW() + " " + getH() + "\n");
			dos.print(255 + "\n");
			dos.flush();

			// write data
			int x, y;
			byte[] rgb = new byte[3];
			for (y = 0; y < getH(); y++) {
				for (x = 0; x < getW(); x++) {
					getPixel(x, y, rgb);
					fos.write(rgb[0]);
					fos.write(rgb[1]);
					fos.write(rgb[2]);

				}
				fos.flush();
			}
			dos.close();
			fos.close();

			System.out.println("Wrote into " + fileName + " Successfully.");

		} // try
		catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public void grayScale() {
		int[] rgb = new int[3];
		for (int i = 0; i < getH(); i++) {
			for (int j = 0; j < getW(); j++) {
				getPixel(j, i, rgb);
				int gray = (int) Math.round(0.299 * rgb[0] + 0.587 * rgb[1] + 0.114 * rgb[2]);
				if (gray > 255) {
					gray = 255;
				}
				for (int k = 0; k < rgb.length; k++) {
					rgb[k] = gray;
				}
				setPixel(j, i, rgb);
			}
		}
	}

	public void nValue(int[] rgb, int N) {
		int gray = rgb[0];
		if (N == 2) {
			if (gray <= 127) {
				for (int k = 0; k < rgb.length; k++) {
					rgb[k] = 0;
				}
			} else {
				for (int k = 0; k < rgb.length; k++) {
					rgb[k] = 255;
				}
			}
		} else if (N == 4) {
			if (gray < 42) {
				for (int k = 0; k < rgb.length; k++) {
					rgb[k] = 0;
				}
			} else if (gray >= 42 && gray < 127) {
				for (int k = 0; k < rgb.length; k++) {
					rgb[k] = 85;
				}
			} else if (gray >= 127 && gray < 212) {
				for (int k = 0; k < rgb.length; k++) {
					rgb[k] = 170;
				}
			} else {
				for (int k = 0; k < rgb.length; k++) {
					rgb[k] = 255;
				}
			}
		} else if (N == 8) {
			if (gray < 18) {
				for (int k = 0; k < rgb.length; k++) {
					rgb[k] = 0;
				}
			} else if (gray >= 18 && gray < 54) {
				for (int k = 0; k < rgb.length; k++) {
					rgb[k] = 36;
				}
			} else if (gray >= 54 && gray < 90) {
				for (int k = 0; k < rgb.length; k++) {
					rgb[k] = 72;
				}
			} else if (gray >= 90 && gray < 126) {
				for (int k = 0; k < rgb.length; k++) {
					rgb[k] = 108;
				}
			} else if (gray >= 126 && gray < 162) {
				for (int k = 0; k < rgb.length; k++) {
					rgb[k] = 144;
				}
			} else if (gray >= 162 && gray < 198) {
				for (int k = 0; k < rgb.length; k++) {
					rgb[k] = 180;
				}
			} else if (gray >= 198 && gray < 234) {
				for (int k = 0; k < rgb.length; k++) {
					rgb[k] = 216;
				}
			} else {
				for (int k = 0; k < rgb.length; k++) {
					rgb[k] = 255;
				}
			}
		} else if (N == 16) {
			if (gray < 8.5) {
				for (int k = 0; k < rgb.length; k++) {
					rgb[k] = 0;
				}
			} else if (gray >= 8.5 && gray < 25.5) {
				for (int k = 0; k < rgb.length; k++) {
					rgb[k] = 17;
				}
			} else if (gray >= 25.5 && gray < 42.5) {
				for (int k = 0; k < rgb.length; k++) {
					rgb[k] = 34;
				}
			} else if (gray >= 42.5 && gray < 59.5) {
				for (int k = 0; k < rgb.length; k++) {
					rgb[k] = 51;
				}
			} else if (gray >= 59.5 && gray < 76.5) {
				for (int k = 0; k < rgb.length; k++) {
					rgb[k] = 68;
				}
			} else if (gray >= 76.5 && gray < 93.5) {
				for (int k = 0; k < rgb.length; k++) {
					rgb[k] = 85;
				}
			} else if (gray >= 93.5 && gray < 110.5) {
				for (int k = 0; k < rgb.length; k++) {
					rgb[k] = 102;
				}
			} else if (gray >= 110.5 && gray < 127.5) {
				for (int k = 0; k < rgb.length; k++) {
					rgb[k] = 119;
				}
			} else if (gray >= 127.5 && gray < 144.5) {
				for (int k = 0; k < rgb.length; k++) {
					rgb[k] = 136;
				}
			} else if (gray >= 144.5 && gray < 161.5) {
				for (int k = 0; k < rgb.length; k++) {
					rgb[k] = 153;
				}
			} else if (gray >= 161.5 && gray < 178.5) {
				for (int k = 0; k < rgb.length; k++) {
					rgb[k] = 170;
				}
			} else if (gray >= 178.5 && gray < 195.5) {
				for (int k = 0; k < rgb.length; k++) {
					rgb[k] = 187;
				}
			} else if (gray >= 195.5 && gray < 212.5) {
				for (int k = 0; k < rgb.length; k++) {
					rgb[k] = 204;
				}
			} else if (gray >= 212.5 && gray < 229.5) {
				for (int k = 0; k < rgb.length; k++) {
					rgb[k] = 221;
				}
			} else if (gray >= 229.5 && gray < 246.5) {
				for (int k = 0; k < rgb.length; k++) {
					rgb[k] = 238;
				}
			} else {
				for (int k = 0; k < rgb.length; k++) {
					rgb[k] = 255;
				}
			}
		}
	}

	public void threshold(int N) {
		int[] rgb = new int[3];
		for (int i = 0; i < getH(); i++) {
			for (int j = 0; j < getW(); j++) {
				getPixel(j, i, rgb);
				nValue(rgb, N);
				setPixel(j, i, rgb);
			}
		}
	}

	public void errorDiffusion(int N) {
		float[][] temp = new float[getW()][getH()];
		int[] rgb2 = new int[3];

		for (int i = 0; i < getH(); i++) {
			for (int j = 0; j < getW(); j++) {
				int[] color = new int[3];
				getPixel(j, i, color);
				temp[j][i] = color[0];
			}
		}

		for (int i = 0; i < getH(); i++) {
			for (int j = 0; j < getW(); j++) {
				int[] color = new int[3];
				color[0] = (int) temp[j][i];
				nValue(color, N);
				float output = color[0];

				float error = temp[j][i] - output;
				if ((j + 1) < getW()) {
					getPixel(j + 1, i, rgb2);
					temp[j + 1][i] = rgb2[0] + (error * 7 / 16);
				}
				if ((i + 1) < getH() && (j - 1) >= 0) {
					getPixel(j - 1, i + 1, rgb2);
					temp[j - 1][i + 1] = rgb2[0] + (error * 3 / 16);
				}
				if ((i + 1) < getH()) {
					getPixel(j, i + 1, rgb2);
					temp[j][i + 1] = rgb2[0] + (error * (5 / 16));
				}
				if ((i + 1) < getH() && (j + 1) < getW()) {
					getPixel(j + 1, i + 1, rgb2);
					temp[j + 1][i + 1] = rgb2[0] + (error * 1 / 16);
				}
				int[] updated = new int[3];
				for (int k = 0; k < 3; k++) {
					updated[k] = (int) output;
				}
				setPixel(j, i, updated);
			}
		}

	}

	public void index() {
		for (int j = 0; j < getH(); j++) {
			for (int i = 0; i < getW(); i++) {
				int[] rgb = new int[3];

				getPixel(i, j, rgb);

				int minIndex = 0;
				int minDist = 256 * 256 * 256;

				for (int y = 0; y < 256; y++) {
					int distR = rgb[0] - lookUpTable[0][y];
					int distG = rgb[1] - lookUpTable[1][y];
					int distB = rgb[2] - lookUpTable[2][y];

					int distance = (int) Math.sqrt((distR * distR) + (distG * distG) + (distB * distB));

					if (distance < minDist) {
						minDist = (int) distance;
						minIndex = y;
					}
				}
				rgb[0] = minIndex;
				rgb[1] = minIndex;
				rgb[2] = minIndex;
				setPixel(i, j, rgb);
			}
		}

	}

	public void qt8() {
		for (int j = 0; j < getH(); j++) {
			for (int i = 0; i < getW(); i++) {
				int[] rgb = new int[3];
				getPixel(i, j, rgb);
				int[] output = new int[3];
				for (int k = 0; k < rgb.length; k++) {
					output[k] = lookUpTable[k][rgb[0]];
				}
				setPixel(i, j, output);
			}
		}
	}

	public void lut() {
		int[] bi = { 0, 0, 0, 0, 0, 0, 0, 0 };
		for (int j = 0; j < 256; j++) {
			for (int i = 0; i < 3; i++) {
				decimalToBinary(bi, j);
				int[] rgb = segments(bi);
				lookUpTable[i][j] = rgb[i];
			}
		}

		System.out.println("Index	        R  G  B");
		System.out.println("_______________________");
		for (int j = 0; j < 256; j++) {
			System.out.print(j + "		");
			for (int i = 0; i < 3; i++) {
				System.out.print(lookUpTable[i][j] + " ");
			}
			System.out.println();
		}
	}

	private static int[] segments(int[] bi) {
		// Split 8-bit binary into 3 color range
		int[] red = Arrays.copyOfRange(bi, 0, 3);
		int[] green = Arrays.copyOfRange(bi, 3, 6);
		int[] blue = Arrays.copyOfRange(bi, 6, bi.length);

		// Get the new value for each segment
		int r = (32 * segmentsVal(red)) + 16;
		int g = (32 * segmentsVal(green)) + 16;
		int b = (64 * segmentsVal(blue)) + 32;
		int[] vals = { r, g, b };
		return vals;

	}

	private static int segmentsVal(int[] color) {
		// Take each color's binary value and give it its segment number
		int value = 0;
		for (int i = 0; i < color.length; i++) {
			if (color[i] == 1) {
				value += Math.pow(2, (color.length - 1) - i);
			}
		}
		return value;
	}

	private static void decimalToBinary(int[] bi, int deci) {
		// Given a number from 0 to 255, make it into an 8-bit binary
		int x = deci;
		// String s = "";
		if (x >= 256) {
			System.out.println("Number too big for 8 bits.");
			return;
		}

		for (int i = 0; i < 8; i++) {
			if (x % 2 == 1) {
				// s = '1' + s;
				bi[(bi.length - 1) - i] = 1;
			}
			if (x % 2 == 0) {
				// s = '0' + s;
				bi[(bi.length - 1) - i] = 0;
			}
			x /= 2;
		}
	}

	public void display()
	// display the image on the screen
	{
		// Use a label to display the image
		// String title = "Image Name - " + fileName;
		String title = fileName;
		JFrame frame = new JFrame(title);
		JLabel label = new JLabel(new ImageIcon(img));
		frame.add(label, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	public void background() {
		int[] rgb = new int[3];
		rgb[0] = 255;
		rgb[1] = 255;
		rgb[2] = 255;
		for (int i = 0; i < getH(); i++) {
			for (int j = 0; j < getW(); j++) {
				setPixel(j, i, rgb);
			}
		}
	}
	
	public void backgroundBlack() {
		int[] rgb = new int[3];
		rgb[0] = 0;
		rgb[1] = 0;
		rgb[2] = 0;
		for (int i = 0; i < getH(); i++) {
			for (int j = 0; j < getW(); j++) {
				setPixel(j, i, rgb);
			}
		}
	}

	public void createCircles(int M, int N) {
		int[] rgb = new int[3];
		rgb[0] = 0;
		rgb[1] = 0;
		rgb[2] = 0;
		int radius = N;
		int centerX = getW() / 2;
		int centerY = getH() / 2;
		boolean cont = true;

		for (int j = N; cont != false; j++) {
			if ((centerX + radius + M <= getW()) && (centerY + radius + M <= getH())) {
				for (int i = M; i > 0; i--) {
					for (int theta = 0; theta < 360; theta++) {
						int x = (int) (radius * Math.cos(theta * Math.PI / 180) + centerX);
						int y = (int) (radius * Math.sin(theta * Math.PI / 180) + centerY);
						setPixel(x, y, rgb);

						int x2 = (int) (radius * Math.cos((theta + 0.5) * Math.PI / 180) + centerX);
						int y2 = (int) (radius * Math.sin((theta + 0.5) * Math.PI / 180) + centerY);
						setPixel(x2, y2, rgb);

						int x3 = (int) (radius * Math.cos((theta + 0.25) * Math.PI / 180) + centerX);
						int y3 = (int) (radius * Math.sin((theta + 0.25) * Math.PI / 180) + centerY);
						setPixel(x3, y3, rgb);

						int x4 = (int) (radius * Math.cos((theta + 0.75) * Math.PI / 180) + centerX);
						int y4 = (int) (radius * Math.sin((theta + 0.75) * Math.PI / 180) + centerY);
						setPixel(x4, y4, rgb);

						int x5 = (int) (radius * Math.cos((theta + 0.125) * Math.PI / 180) + centerX);
						int y5 = (int) (radius * Math.sin((theta + 0.125) * Math.PI / 180) + centerY);
						setPixel(x5, y5, rgb);

						int x6 = (int) (radius * Math.cos((theta + 0.875) * Math.PI / 180) + centerX);
						int y6 = (int) (radius * Math.sin((theta + 0.875) * Math.PI / 180) + centerY);
						setPixel(x6, y6, rgb);

						x6 = (int) (radius * Math.cos((theta + 0.375) * Math.PI / 180) + centerX);
						y6 = (int) (radius * Math.sin((theta + 0.375) * Math.PI / 180) + centerY);
						setPixel(x6, y6, rgb);

						x6 = (int) (radius * Math.cos((theta + 0.625) * Math.PI / 180) + centerX);
						y6 = (int) (radius * Math.sin((theta + 0.625) * Math.PI / 180) + centerY);
						setPixel(x6, y6, rgb);

						x6 = (int) (radius * Math.cos((theta + 0.8125) * Math.PI / 180) + centerX);
						y6 = (int) (radius * Math.sin((theta + 0.8125) * Math.PI / 180) + centerY);
						setPixel(x6, y6, rgb);

						x6 = (int) (radius * Math.cos((theta + 0.6875) * Math.PI / 180) + centerX);
						y6 = (int) (radius * Math.sin((theta + 0.6875) * Math.PI / 180) + centerY);
						setPixel(x6, y6, rgb);
					}
					radius++;
				}

			} else {
				cont = false;
			}
			radius = (radius - M) + N;
		}
	}

} // Image class