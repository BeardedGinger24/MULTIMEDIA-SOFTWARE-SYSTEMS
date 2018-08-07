package HW3;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class Menu extends CS4551_Oganesyan {

	final static double[][] lumQuant = { { 4, 4, 4, 8, 8, 16, 16, 32 }, { 4, 4, 4, 8, 8, 16, 16, 32 },
			{ 4, 4, 8, 8, 16, 16, 16, 32 }, { 8, 8, 8, 16, 16, 32, 32, 32 }, { 8, 8, 16, 16, 32, 32, 32, 32 },
			{ 16, 16, 16, 32, 32, 32, 32, 32 }, { 16, 16, 32, 32, 32, 32, 32, 32 },
			{ 32, 32, 32, 32, 32, 32, 32, 32 } };

	final static double[][] chromQuant = { { 8, 8, 8, 16, 32, 32, 32, 32 }, { 8, 8, 8, 16, 32, 32, 32, 32 },
			{ 8, 8, 16, 32, 32, 32, 32, 32 }, { 16, 16, 32, 32, 32, 32, 32, 32 }, { 32, 32, 32, 32, 32, 32, 32, 32 },
			{ 32, 32, 32, 32, 32, 32, 32, 32 }, { 32, 32, 32, 32, 32, 32, 32, 32 },
			{ 32, 32, 32, 32, 32, 32, 32, 32 } };

	private static ArrayList<Tuple> tuple = new ArrayList<Tuple>();
	private static double D;
	private static int n;

	public static void main(String[] args) {
		// tuple.add(new Tuple(1, 2));

		System.out.println("--Welcome to Multimedia Software System--");

		int menu;
		String name = new String(args[0]);
		name = name.split("\\.", 2)[0];

		do {
			System.out.println("Main Menu----------------------------------- \n 1. DCT \n 2. Quit");
			menu = sc.nextInt();
			switch (menu) {
			case 1:
				Boolean quality = true;

				double compRatio;
				double S;

				do {
					System.out.println("\nSelect a number for the quality, 0 (highest) throught 5 (loswest):");
					n = sc.nextInt();
					if (n < 0 || n > 5) {
						quality = false;
						System.out.println("Error: Input value 0 - 5:");
					} else {
						quality = true;
					}
				} while (quality != true);

				Image img = new Image(args[0]);
				int curH = img.getH();
				int curW = img.getW();

				S = curH * curW * 24;

				if (curH % 8 != 0) {
					curH = curH + (8 - (curH % 8));
				}
				if (curW % 8 != 0) {
					curW = curW + (8 - (curW % 8));
				}

				int croH = curH / 2;
				int croW = curW / 2;

				if (croH % 8 != 0) {
					croH = croH + (8 - (croH % 8));
				}
				if (croW % 8 != 0) {
					croW = croW + (8 - (croW % 8));
				}

				// System.out.println(curW + " x " + curH);

				// Image dct = new Image(curW, curH);
				double[][] R = new double[curW][curH];
				double[][] G = new double[curW][curH];
				double[][] B = new double[curW][curH];

				double[][] Y = new double[curW][curH];
				double[][] Cb = new double[curW][curH];
				double[][] Cr = new double[curW][curH];

				double[][] y = new double[curW][curH];
				double[][] cb = new double[croW][croH];
				double[][] cr = new double[croW][croH];

				for (int j = 0; j < croH; j++) {
					for (int i = 0; i < croW; i++) {
						cb[i][j] = 0;
						cr[i][j] = 0;
					}
				}

				background(R, G, B, curW, curH);
				copyOver(img, R, G, B);
				cstHelper(R, G, B, Y, Cb, Cr, curW, curH);
				subsample(Y, Cb, Cr, y, cb, cr, curW, curH);
				dctMain(y, cb, cr, curW, curH, croW, croH);
				quant(y, cb, cr, curW, curH, croW, croH, n);
				compressHelper(y, cb, cr, curW, curH, croW, croH);
				decode(y, cb, cr, curW, curH, croW, croH);
				deQuant(y, cb, cr, curW, curH, croW, croH, n);
				idctMain(y, cb, cr, curW, curH, croW, croH);
				supersample(Y, Cb, Cr, y, cb, cr, curW, curH);
				icstHelper(R, G, B, Y, Cb, Cr, curW, curH);
				recover(img, R, G, B);
				System.out.println("\nCompression Ration: " + S / D + "\n");
				img.display();
				img.write2PPM(name + "_" + n + ".ppm");
				D = 0;
				tuple.clear();
				break;
			case 2:
				break;
			}

		} while (menu != 2);
		System.out.println("--Good Bye--");
	}

	private static void compressHelper(double[][] y, double[][] cb, double[][] cr, int curW, int curH, int croW,
			int croH) {
		double[][] temp = new double[8][8];
		int ybr = 0;

		for (int j = 0; j < curH; j += 8) {
			for (int i = 0; i < curW; i += 8) {
				for (int b = 0; b < 8; b++) {
					for (int a = 0; a < 8; a++) {
						temp[a][b] = y[i + a][j + b];
					}
				}
				compress(temp, ybr);
			}
		}

		ybr = 1;
		for (int j = 0; j < croH; j += 8) {
			for (int i = 0; i < croW; i += 8) {
				for (int b = 0; b < 8; b++) {
					for (int a = 0; a < 8; a++) {
						temp[a][b] = cb[i + a][j + b];
					}
				}

				compress(temp, ybr);
			}
		}

		ybr = 2;
		for (int j = 0; j < croH; j += 8) {
			for (int i = 0; i < croW; i += 8) {
				for (int b = 0; b < 8; b++) {
					for (int a = 0; a < 8; a++) {
						temp[a][b] = cr[i + a][j + b];
					}
				}

				compress(temp, ybr);
			}
		}
	}

	private static void compress(double[][] temp, int ybr) {
		int m = temp.length;
		int n = temp[0].length;
		double[] result = new double[n * m];
		int t = 0;

		for (int i = 0; i < n + m - 1; i++) {
			if (i % 2 == 1) {
				// down left
				int a = i < n ? 0 : i - n + 1;
				int b = i < n ? i : n - 1;
				while (a < m && b >= 0) {
					result[t++] = temp[a++][b--];
				}
			} else {
				// up right
				int a = i < m ? i : m - 1;
				int b = i < m ? 0 : i - m + 1;
				while (a >= 0 && b < n) {
					result[t++] = temp[a--][b++];
				}
			}
		}
		encode(result, ybr);
	}

	private static void encode(double[] result, int ybr) {
		for (int i = 0; i < result.length; i++) {
			double runLength = 1;
			while (i + 1 < result.length && result[i] == result[i + 1]) {
				runLength++;
				i++;
			}
			tuple.add(new Tuple(result[i], runLength));
			if (ybr == 0) {
				if (i == 0) {
					D += (10 - n);
				} else {
					D += (10 - n) + 6;
				}
			}
			if (ybr == 1 || ybr == 2) {
				if (i == 0) {
					D += (9 - n);
				} else {
					D += (9 - n) + 6;
				}
			}
		}
	}

	private static void restore(double[][] temp, double[][] y, double[][] cb, double[][] cr, int curW, int curH,
			int croW, int croH, int count) {

		int select = 0;
		int placement = 0;
		int c = 0;
		int d = 0;

		if (count < (curW * curH) / 64) {
			select = 1;
			for (int j = 0; j < curH; j += 8) {
				for (int i = 0; i < curW; i += 8) {
					if (count == placement) {
						c = i;
						d = j;
					}
					placement++;
				}
			}
		}
		if (count >= (curW * curH) / 64 && count < ((curW * curH) / 64) + ((croW * croH) / 64)) {
			select = 2;
			count -= ((croW * croH) / 64);
			for (int j = 0; j < croH; j += 8) {
				for (int i = 0; i < croW; i += 8) {
					if (count == placement) {
						c = i;
						d = j;
					}
					placement++;
				}
			}
		}
		if (count >= ((curW * curH) / 64) + ((croW * croH) / 64)) {
			select = 3;
			count -= (((curW * curH) / 64) + ((croW * croH) / 64));
			for (int j = 0; j < croH; j += 8) {
				for (int i = 0; i < croW; i += 8) {
					if (count == placement) {
						c = i;
						d = j;
					}
					placement++;
				}
			}
		}

		switch (select) {
		case 1:
			for (int b = d; b < d + 8; b++) {
				for (int a = c; a < c + 8; a++) {
					y[a][b] = temp[a % 8][b % 8];
				}
			}
			break;
		case 2:
			for (int b = d; b < d + 8; b++) {
				for (int a = c; a < c + 8; a++) {
					cb[a][b] = temp[a % 8][b % 8];
				}
			}
			break;
		case 3:
			for (int b = d; b < d + 8; b++) {
				for (int a = c; a < c + 8; a++) {
					cr[a][b] = temp[a % 8][b % 8];
				}
			}
			break;
		}

	}

	private static void deCompress(double[] result, double[][] y, double[][] cb, double[][] cr, int curW, int curH,
			int croW, int croH, int count) {
		double[][] temp = new double[8][8];
		int m = temp.length;
		int n = temp[0].length;
		int t = 0;

		for (int i = 0; i < n + m - 1; i++) {
			if (i % 2 == 1) {
				// down left
				int a = i < n ? 0 : i - n + 1;
				int b = i < n ? i : n - 1;
				while (a < m && b >= 0) {
					temp[a++][b--] = result[t++];
				}
			} else {
				// up right
				int a = i < m ? i : m - 1;
				int b = i < m ? 0 : i - m + 1;
				while (a >= 0 && b < n) {
					temp[a--][b++] = result[t++];
				}
			}
		}
		restore(temp, y, cb, cr, curW, curH, croW, croH, count);
	}

	private static void decode(double[][] y, double[][] cb, double[][] cr, int curW, int curH, int croW, int croH) {
		double[] result = new double[64];
		int i = 0;
		int count = 0;

		for (int j = 0; j < tuple.size(); j++) {
			for (int a = 0; a < tuple.get(j).Length; a++) {
				result[a + i] = tuple.get(j).Value;
			}

			i += tuple.get(j).Length;
			if (i > 63) {
				deCompress(result, y, cb, cr, curW, curH, croW, croH, count);
				count++;
				i = 0;
			}
		}
	}

	private static void background(double[][] r, double[][] g, double[][] b, int curW, int curH) {
		for (int i = 0; i < curH; i++) {
			for (int j = 0; j < curW; j++) {
				r[j][i] = 0;
				g[j][i] = 0;
				b[j][i] = 0;
			}
		}

	}

	private static void deQuant(double[][] y, double[][] cb, double[][] cr, int curW, int curH, int croW, int croH,
			int n) {
		double[][] temp = new double[8][8];
		double[][] B = new double[8][8];

		for (int j = 0; j < curH; j += 8) {
			for (int i = 0; i < curW; i += 8) {

				for (int b = 0; b < 8; b++) {
					for (int a = 0; a < 8; a++) {
						temp[a][b] = y[i + a][j + b];
						B[a][b] = temp[a][b] * (lumQuant[a][b] * Math.pow(2, n));
						y[i + a][j + b] = B[a][b];
					}
				}
			}
		}

		for (int j = 0; j < croH; j += 8) {
			for (int i = 0; i < croW; i += 8) {
				for (int b = 0; b < 8; b++) {
					for (int a = 0; a < 8; a++) {
						temp[a][b] = cb[i + a][j + b];
						B[a][b] = temp[a][b] * (chromQuant[a][b] * Math.pow(2, n));
						cb[i + a][j + b] = B[a][b];
					}
				}
			}
		}

		for (int j = 0; j < croH; j += 8) {
			for (int i = 0; i < croW; i += 8) {
				for (int b = 0; b < 8; b++) {
					for (int a = 0; a < 8; a++) {
						temp[a][b] = cr[i + a][j + b];
						B[a][b] = temp[a][b] * (chromQuant[a][b] * Math.pow(2, n));
						cr[i + a][j + b] = B[a][b];
					}
				}
			}
		}
	}

	private static void quant(double[][] y, double[][] cb, double[][] cr, int curW, int curH, int croW, int croH,
			int n) {
		double[][] temp = new double[8][8];
		double[][] B = new double[8][8];

		for (int j = 0; j < curH; j += 8) {
			for (int i = 0; i < curW; i += 8) {

				for (int b = 0; b < 8; b++) {
					for (int a = 0; a < 8; a++) {
						temp[a][b] = y[i + a][j + b];
						B[a][b] = Math.round(temp[a][b] / (lumQuant[a][b] * Math.pow(2, n)));
						y[i + a][j + b] = B[a][b];
					}
				}
			}
		}

		for (int j = 0; j < croH; j += 8) {
			for (int i = 0; i < croW; i += 8) {
				for (int b = 0; b < 8; b++) {
					for (int a = 0; a < 8; a++) {
						temp[a][b] = cb[i + a][j + b];
						B[a][b] = Math.round(temp[a][b] / (chromQuant[a][b] * Math.pow(2, n)));
						cb[i + a][j + b] = B[a][b];
					}
				}
			}
		}

		for (int j = 0; j < croH; j += 8) {
			for (int i = 0; i < croW; i += 8) {
				for (int b = 0; b < 8; b++) {
					for (int a = 0; a < 8; a++) {
						temp[a][b] = cr[i + a][j + b];
						B[a][b] = Math.round(temp[a][b] / (chromQuant[a][b] * Math.pow(2, n)));
						cr[i + a][j + b] = B[a][b];
					}
				}
			}
		}
	}

	private static void dctMain(double[][] y, double[][] cb, double[][] cr, int curW, int curH, int croW, int croH) {
		double[][] temp = new double[8][8];
		double[][] B = new double[8][8];

		for (int j = 0; j < curH; j += 8) {
			for (int i = 0; i < curW; i += 8) {
				for (int b = 0; b < 8; b++) {
					for (int a = 0; a < 8; a++) {
						temp[a][b] = y[i + a][j + b];
					}
				}

				dct(temp, B);

				for (int b = 0; b < 8; b++) {
					for (int a = 0; a < 8; a++) {
						y[i + a][j + b] = B[a][b];
					}
				}
			}
		}

		for (int j = 0; j < croH; j += 8) {
			for (int i = 0; i < croW; i += 8) {
				for (int b = 0; b < 8; b++) {
					for (int a = 0; a < 8; a++) {
						temp[a][b] = cb[i + a][j + b];
					}
				}

				dct(temp, B);

				for (int b = 0; b < 8; b++) {
					for (int a = 0; a < 8; a++) {
						cb[i + a][j + b] = B[a][b];
					}
				}
			}
		}

		for (int j = 0; j < croH; j += 8) {
			for (int i = 0; i < croW; i += 8) {
				for (int b = 0; b < 8; b++) {
					for (int a = 0; a < 8; a++) {
						temp[a][b] = cr[i + a][j + b];
					}
				}

				dct(temp, B);

				for (int b = 0; b < 8; b++) {
					for (int a = 0; a < 8; a++) {
						cr[i + a][j + b] = B[a][b];
					}
				}
			}
		}
	}

	private static void idctMain(double[][] y, double[][] cb, double[][] cr, int curW, int curH, int croW, int croH) {
		double[][] temp = new double[8][8];
		double[][] B = new double[8][8];

		for (int j = 0; j < curH; j += 8) {
			for (int i = 0; i < curW; i += 8) {
				for (int b = 0; b < 8; b++) {
					for (int a = 0; a < 8; a++) {
						temp[a][b] = y[i + a][j + b];
					}
				}

				idct(B, temp);

				for (int b = 0; b < 8; b++) {
					for (int a = 0; a < 8; a++) {
						y[i + a][j + b] = B[a][b];
					}
				}
			}
		}

		for (int j = 0; j < croH; j += 8) {
			for (int i = 0; i < croW; i += 8) {
				for (int b = 0; b < 8; b++) {
					for (int a = 0; a < 8; a++) {
						temp[a][b] = cb[i + a][j + b];
					}
				}

				idct(B, temp);

				for (int b = 0; b < 8; b++) {
					for (int a = 0; a < 8; a++) {
						cb[i + a][j + b] = B[a][b];
					}
				}
			}
		}

		for (int j = 0; j < croH; j += 8) {
			for (int i = 0; i < croW; i += 8) {
				for (int b = 0; b < 8; b++) {
					for (int a = 0; a < 8; a++) {
						temp[a][b] = cr[i + a][j + b];
					}
				}

				idct(B, temp);

				for (int b = 0; b < 8; b++) {
					for (int a = 0; a < 8; a++) {
						cr[i + a][j + b] = B[a][b];
					}
				}
			}
		}
	}

	private static void idct(double[][] C, double[][] B) {
		double Cu = 0;
		double Cv = 0;
		double dct, sum;

		for (int j = 0; j < B.length; j++) {
			for (int i = 0; i < B.length; i++) {
				sum = 0;
				for (int v = 0; v < B.length; v++) {
					for (int u = 0; u < B.length; u++) {
						if (u == 0) {
							Cu = 1 / Math.sqrt(2);
						} else {
							Cu = 1;
						}
						if (v == 0) {
							Cv = 1 / Math.sqrt(2);
						} else {
							Cv = 1;
						}

						dct = (Cu * Cv * 0.25) * (Math.cos(((2 * i + 1) * u * Math.PI) / 16)
								* Math.cos(((2 * j + 1) * v * Math.PI) / 16) * B[u][v]);
						sum += dct;

					}
				}
				C[i][j] = sum + 128;
			}
		}
	}

	private static void dct(double[][] A, double[][] B) {
		double Cu = 0;
		double Cv = 0;
		double dct, sum;

		for (int v = 0; v < A.length; v++) {
			for (int u = 0; u < A.length; u++) {

				sum = 0;
				for (int j = 0; j < A.length; j++) {
					for (int i = 0; i < A.length; i++) {
						A[i][j] -= 128;
						dct = (Math.cos(((2 * i + 1) * u * Math.PI) / 16) * Math.cos(((2 * j + 1) * v * Math.PI) / 16)
								* A[i][j]);
						sum += dct;

					}
				}
				if (u == 0) {
					Cu = 1 / Math.sqrt(2);
				} else {
					Cu = 1;
				}
				if (v == 0) {
					Cv = 1 / Math.sqrt(2);
				} else {
					Cv = 1;
				}

				B[u][v] = Cu * Cv * 0.25 * sum;

				// if (B[u][v] > 1024) {
				// B[u][v] = 1024;
				// }
				// if (B[u][v] < -1024) {
				// B[u][v] = -1024;
				// }

			}
		}
	}

	private static void supersample(double[][] Y, double[][] Cb, double[][] Cr, double[][] y, double[][] cb,
			double[][] cr, int curW, int curH) {

		for (int i = 0; i < curH; i++) {
			for (int j = 0; j < curW; j++) {
				Y[j][i] = y[j][i];
			}
		}

		for (int i = 0; i < curH; i += 2) {
			for (int j = 0; j < curW; j += 2) {
				// Cb
				Cb[j][i] = cb[j / 2][i / 2];
				Cb[j + 1][i] = cb[j / 2][i / 2];
				Cb[j][i + 1] = cb[j / 2][i / 2];
				Cb[j + 1][i + 1] = cb[j / 2][i / 2];

				// Cr
				Cr[j][i] = cr[j / 2][i / 2];
				Cr[j + 1][i] = cr[j / 2][i / 2];
				Cr[j][i + 1] = cr[j / 2][i / 2];
				Cr[j + 1][i + 1] = cr[j / 2][i / 2];

			}
		}
	}

	private static void subsample(double[][] Y, double[][] Cb, double[][] Cr, double[][] y, double[][] cb,
			double[][] cr, int curW, int curH) {
		double one, two, three, four;

		for (int i = 0; i < curH; i++) {
			for (int j = 0; j < curW; j++) {
				y[j][i] = Y[j][i];
			}
		}

		for (int i = 0; i < curH; i += 2) {
			for (int j = 0; j < curW; j += 2) {
				// Cb
				one = Cb[j][i];
				two = Cb[j + 1][i];
				three = Cb[j][i + 1];
				four = Cb[j + 1][i + 1];

				double avg = (one + two + three + four) / 4;
				cb[j / 2][i / 2] = avg;

				// Cr
				one = Cr[j][i];
				two = Cr[j + 1][i];
				three = Cr[j][i + 1];
				four = Cr[j + 1][i + 1];

				avg = (one + two + three + four) / 4;
				cr[j / 2][i / 2] = avg;
			}
		}

	}

	private static void icstHelper(double[][] R, double[][] G, double[][] B, double[][] Y, double[][] Cb, double[][] Cr,
			int curW, int curH) {
		double[][] rgb = new double[3][1];

		for (int y = 0; y < curH; y++) {
			for (int x = 0; x < curW; x++) {
				Y[x][y] += 128;
				Cb[x][y] += 0.5;
				Cr[x][y] += 0.5;

				rgb[0][0] = Y[x][y];
				rgb[1][0] = Cb[x][y];
				rgb[2][0] = Cr[x][y];

				mainICST(rgb);

				R[x][y] = rgb[0][0];
				G[x][y] = rgb[1][0];
				B[x][y] = rgb[2][0];

				if (R[x][y] > 255) {
					R[x][y] = 255;
				}
				if (R[x][y] < 0) {
					R[x][y] = 0;
				}
				if (G[x][y] > 255) {
					G[x][y] = 255;
				}
				if (G[x][y] < 0) {
					G[x][y] = 0;
				}
				if (B[x][y] > 255) {
					B[x][y] = 255;
				}
				if (B[x][y] < 0) {
					B[x][y] = 0;
				}
			}
		}
	}

	private static void mainICST(double[][] rgb) {
		double[][] YCbCr = new double[3][1];

		double[][] transformMatrix = { { 1.0000, 0, 1.4020 }, { 1.0000, -0.3441, -0.7141 }, { 1.000, 1.7720, 0 } };

		transform(YCbCr, rgb, transformMatrix);

		for (int j = 0; j < YCbCr.length; j++) {
			for (int i = 0; i < 1; i++) {
				rgb[j][i] = YCbCr[j][i];

			}
		}

	}

	private static void cstHelper(double[][] R, double[][] G, double[][] B, double[][] Y, double[][] Cb, double[][] Cr,
			int curW, int curH) {
		double[][] rgb = new double[3][1];

		for (int y = 0; y < curH; y++) {
			for (int x = 0; x < curW; x++) {
				rgb[0][0] = R[x][y];
				rgb[1][0] = G[x][y];
				rgb[2][0] = B[x][y];

				mainCST(rgb);
				Y[x][y] = rgb[0][0];
				Cb[x][y] = rgb[1][0];
				Cr[x][y] = rgb[2][0];
			}
		}
	}

	private static void mainCST(double[][] rgb) {
		double[][] YCbCr = new double[3][1];

		double[][] transformMatrix = { { 0.2990, 0.5870, 0.1140 }, { -0.1687, -0.3313, 0.5000 },
				{ 0.5000, -0.4187, -0.0813 } };

		transform(YCbCr, rgb, transformMatrix);

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 1; j++) {
				if (i == 0) {
					YCbCr[i][j] = round(YCbCr[i][j] - 128, 1);
				} else {
					YCbCr[i][j] = round(YCbCr[i][j] - 0.5, 1);
				}
			}
		}

		for (int j = 0; j < YCbCr.length; j++) {
			for (int i = 0; i < 1; i++) {
				rgb[j][i] = YCbCr[j][i];

			}
		}

	}

	private static void transform(double[][] YCbCr, double[][] rgb, double[][] transformMatrix) {
		for (int i = 0; i < transformMatrix.length; i++) {
			for (int j = 0; j < 1; j++) {
				for (int k = 0; k < transformMatrix.length; k++) {
					YCbCr[i][j] = (YCbCr[i][j] + transformMatrix[i][k] * rgb[k][j]);
				}
			}
		}
	}

	private static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		BigDecimal bd = new BigDecimal(Double.toString(value));
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	private static void recover(Image img, double[][] R, double[][] G, double[][] B) {
		int[] rgb = new int[3];

		for (int y = 0; y < img.getH(); y++) {
			for (int x = 0; x < img.getW(); x++) {
				rgb[0] = (int) R[x][y];
				rgb[1] = (int) G[x][y];
				rgb[2] = (int) B[x][y];
				img.setPixel(x, y, rgb);
			}
		}
	}

	private static void copyOver(Image img, double[][] R, double[][] G, double[][] B) {
		int[] rgb = new int[3];

		for (int y = 0; y < img.getH(); y++) {
			for (int x = 0; x < img.getW(); x++) {
				img.getPixel(x, y, rgb);
				R[x][y] = rgb[0];
				G[x][y] = rgb[1];
				B[x][y] = rgb[2];
			}
		}
	}

}
