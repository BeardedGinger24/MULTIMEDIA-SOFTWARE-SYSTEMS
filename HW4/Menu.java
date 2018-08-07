package HW4;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Menu extends CS4551_Oganesyan {

	public static void main() {

		System.out.println("--Welcome to Multimedia Software System--");
		System.out.println();
		int menu;

		String timg, rimg, name, name2;
		int n, p;
		Image targetImg, refImg, residual, grayTImg, grayRImg;
		int[][] G, G2, mx, my;
		Integer targetNumber, timgNumber, rimgNumber;

		// Use green for hands on, for hw use gray

		do {
			System.out.println("Main Menu-----------------------------------\n" + "1. Block-Based Motion Compensation\n"
					+ "2. Removing Moving Objects\n" + "3. Quit\n" + "Please enter the task number [1-3]:");
			menu = sc.nextInt();
			switch (menu) {
			case 1:
				System.out.println("Please input the image number of the target image:");
				timgNumber = sc.nextInt();
				String timgN = timgNumber + "";
				if (timgN.length() == 1) {
					timgN = "00" + timgN;
				} else if (timgN.length() == 2) {
					timgN = "0" + timgN;
				}
				timg = "Walk_" + timgN + ".ppm";

				System.out.println("Please input the image number of the reference image:");
				rimgNumber = sc.nextInt();
				String rimgN = rimgNumber + "";
				if (rimgN.length() == 1) {
					rimgN = "00" + rimgN;
				} else if (rimgN.length() == 2) {
					rimgN = "0" + rimgN;
				}
				rimg = "Walk_" + rimgN + ".ppm";

				name = new String(timg);
				name = name.split("\\.", 2)[0];
				name2 = new String(rimg);
				name2 = name2.split("\\.", 2)[0];

				targetImg = new Image(timg);
				refImg = new Image(rimg);

				residual = new Image(targetImg.getW(), targetImg.getH());

				grayTImg = new Image(timg);
				grayRImg = new Image(rimg);
				grayTImg.grayScale();
				grayRImg.grayScale();

				System.out.println("\nSelect a number 'n' for the macro block size (8, 16, or 24):");
				n = sc.nextInt();

				System.out.println("\nSelect a number 'p' for the search window (4, 8, 12, or 16):");
				p = sc.nextInt();

				G = new int[targetImg.getW()][targetImg.getH()];
				G2 = new int[refImg.getW()][refImg.getH()];

				copyOver(grayTImg, G);
				copyOver(grayRImg, G2);

				mx = new int[targetImg.getW() / n][targetImg.getH() / n];
				my = new int[targetImg.getW() / n][targetImg.getH() / n];

				MSD(G, G2, p, n, p, mx, my);

				errorBlock(residual, G, G2, mx, my, n);
				writeToFile(timg, rimg, grayTImg.getW(), grayTImg.getH(), n, mx, my, 1);
				residual.display();
				residual.write2PPM("error_" + name + "-" + name2 + ".ppm");

				break;
			case 2:

				Boolean quality = true;
				do {
					System.out.println("Please input the image number of the target image (onyl frames for 19 ~ 179):");
					targetNumber = sc.nextInt();
					if (targetNumber < 19 || targetNumber > 179) {
						quality = false;
						System.out.println("Error: Input value 19 - 179:");
					} else {
						quality = true;
					}
				} while (quality != true);

				String targetN = targetNumber + "";

				if (targetN.length() == 1) {
					targetN = "00" + targetN;
				} else if (targetN.length() == 2) {
					targetN = "0" + targetN;
				}

				timg = "Walk_" + targetN + ".ppm";

				targetNumber -= 2;
				rimg = targetNumber + "";
				if (rimg.length() == 1) {
					rimg = "00" + rimg;
				} else if (rimg.length() == 2) {
					rimg = "0" + rimg;
				}
				rimg = "Walk_" + rimg + ".ppm";

				String rimg5 = "Walk_005.ppm";
				Image fifth = new Image(rimg5);

				name = new String(timg);
				name = name.split("\\.", 2)[0];
				name2 = new String(rimg);
				name2 = name2.split("\\.", 2)[0];

				targetImg = new Image(timg);
				Image targetCopy = new Image(timg);
				refImg = new Image(rimg);

				residual = new Image(targetImg.getW(), targetImg.getH());

				grayTImg = new Image(timg);
				grayRImg = new Image(rimg);
				grayTImg.grayScale();
				grayRImg.grayScale();

				System.out.println("\nSelect a number 'n' for the macro block size (8, 16, or 24):");
				n = sc.nextInt();

				System.out.println("\nSelect a number 'p' for the search window (4, 8, 12, or 16):");
				p = sc.nextInt();

				G = new int[targetImg.getW()][targetImg.getH()];
				G2 = new int[refImg.getW()][refImg.getH()];

				copyOver(grayTImg, G);
				copyOver(grayRImg, G2);

				mx = new int[targetImg.getW() / n][targetImg.getH() / n];
				my = new int[targetImg.getW() / n][targetImg.getH() / n];

				MSD(G, G2, p, n, p, mx, my);

				errorBlock(residual, G, G2, mx, my, n);
				dynamic(residual, mx, my, n);
				self(targetCopy, mx, my, n);
				removeWithFifth(targetImg, fifth, residual, n);
				writeToFile(timg, rimg, grayTImg.getW(), grayTImg.getH(), n, mx, my, 2);
				residual.display();
				residual.write2PPM("dynamic_" + name + ".ppm");
				targetCopy.display();
				targetCopy.write2PPM("obj_remove1_" + name + ".ppm");
				targetImg.display();
				targetImg.write2PPM("obj_remove2_" + name + ".ppm");

				break;
			}

		} while (menu != 3);
		System.out.println("--Good Bye--");
	}

	private static void self(Image targetCopy, int[][] mx, int[][] my, int n) {
		int[] rgb = new int[3];

		for (int y = 0; y < targetCopy.getH() / n; y++) {
			for (int x = 0; x < targetCopy.getW() / n; x++) {
				if (mx[x][y] != 0 || my[x][y] != 0) {
					int a = 0, b = 0;
					if (y - 1 >= 0) {
						if (mx[x][y - 1] == 0 && my[x][y - 1] == 0) {
							a = x;
							b = y - 1;
						}
					} else if (x - 1 >= 0) {
						if (mx[x - 1][y] == 0 && my[x - 1][y] == 0) {
							a = x - 1;
							b = y;
						}
					} else if (x + 1 <= targetCopy.getW() / n) {
						if (mx[x + 1][y] == 0 && my[x + 1][y] == 0) {
							a = x + 1;
							b = y;
						}
					} else if (y + 1 <= targetCopy.getH() / n) {
						if (mx[x][y + 1] == 0 && my[x][y + 1] == 0) {
							a = x;
							b = y + 1;
						}
					}

					int c = x * n;
					int d = y * n;
					for (int j = b * n; j < b * n + n; j++) {
						for (int i = a * n; i < a * n + n; i++) {
							targetCopy.getPixel(i, j, rgb);
							targetCopy.setPixel(c, d, rgb);
							c++;
						}
						d++;
						c = x * n;
					}
				}
			}
		}

	}

	private static void removeWithFifth(Image targetImg, Image fifth, Image residual, int n) {
		int[] rgb = new int[3];

		for (int y = 0; y < residual.getH(); y += n) {
			for (int x = 0; x < residual.getW(); x += n) {
				residual.getPixel(x, y, rgb);
				if (rgb[0] == 255) {
					for (int j = y; j < y + n; j++) {
						for (int i = x; i < x + n; i++) {
							fifth.getPixel(i, j, rgb);
							targetImg.setPixel(i, j, rgb);
						}
					}
				}
			}
		}

	}

	private static void dynamic(Image residual, int[][] mx, int[][] my, int n) {
		int[] rgb = new int[3];
		rgb[0] = 255;
		rgb[1] = 0;
		rgb[2] = 0;

		for (int j = 0; j < mx[0].length; j++) {
			for (int i = 0; i < mx.length; i++) {
				if (mx[i][j] != 0 || my[i][j] != 0) {
					for (int y = j * n; y < j * n + n; y++) {
						for (int x = i * n; x < i * n + n; x++) {
							if (x == i * n || y == j * n || x == i * n + n - 1 || y == j * n + n - 1) {
								residual.setPixel(x, y, rgb);
							}
						}
					}
				}
			}
		}

	}

	private static void errorBlock(Image residual, int[][] g, int[][] g2, int[][] mx, int[][] my, int n) {
		int[] rgb = new int[3];
		int moveX;
		int moveY;
		int[][] error = new int[g.length][g[0].length];

		for (int y = 0; y < g[0].length; y += n) {
			for (int x = 0; x < g.length; x += n) {
				moveX = mx[x / n][y / n];
				moveY = my[x / n][y / n];

				for (int j = y + moveY; j < y + moveY + n; j++) {
					for (int i = x + moveX; i < x + moveX + n; i++) {
						error[i - moveX][j - moveY] = Math.abs(g[i - moveX][j - moveY] - g2[i][j]);
					}
				}
			}
		}

		int minIndex = 0;
		List<Integer> minList = new ArrayList<>();
		List<Integer> maxList = new ArrayList<>();
		int[] error1d = new int[g.length * g[0].length];

		// Put error image into 1D array
		int index = 0;
		for (int j = 0; j < error[0].length; j++) {
			for (int i = 0; i < error.length; i++) {
				error1d[index++] = error[i][j];
			}
		}

		// Find all min values by index
		for (int i = 0; i < error1d.length; i++) {
			if (error1d[i] == error1d[minIndex]) {
				minList.add(i);
			} else if (error1d[minIndex] > error1d[i]) {
				minList.clear();
				minList.add(i);

				minIndex = i;
			}
		}

		// Find all max values by index
		int maxIndex = 0;
		for (int i = 0; i < error1d.length; i++) {
			if (error1d[i] == error1d[maxIndex]) {
				maxList.add(i);
			} else if (error1d[maxIndex] < error1d[i]) {
				maxList.clear();
				maxList.add(i);

				maxIndex = i;
			}
		}

		// Set all min values to 0
		for (int i = 0; i < minList.size(); i++) {
			error1d[minList.get(i)] = 0;
		}

		// Set all max values to 255
		for (int i = 0; i < maxList.size(); i++) {
			error1d[maxList.get(i)] = 255;
		}

		// Put 1D back into 2D error
		index = 0;
		for (int j = 0; j < error[0].length; j++) {
			for (int i = 0; i < error.length; i++) {
				error[i][j] = error1d[index++];
			}
		}

		for (int y = 0; y < error[0].length; y++) {
			for (int x = 0; x < error.length; x++) {
				rgb[0] = error[x][y];
				rgb[1] = error[x][y];
				rgb[2] = error[x][y];
				residual.setPixel(x, y, rgb);
			}
		}
	}

	private static void MSD(int[][] g, int[][] g2, int p, int n, int P, int[][] mx2, int[][] my2) {
		int[][] temp = new int[n][n];
		int mx = 0;
		int my = 0;
		int sum = 0;
		double msd;
		double prevMSD = 999999999;
		int height = g[0].length;
		int width = g.length;

		int count = ((2 * P) + 1) * (2 * P + 1);
		int c = 0;

		for (int j = 0; j < height; j += n) {
			for (int i = 0; i < width; i += n) {

				for (int b = 0; b < n; b++) {
					for (int a = 0; a < n; a++) {
						temp[a][b] = g[a + i][b + j];
					}
				}

				for (int y = j - p; y <= j + p; y++) {
					for (int x = i - p; x <= i + p; x++) {

						if (y >= 0 && x >= 0 && y + n <= height && x + n <= width) {

							for (int m = 0; m < n; m++) {
								for (int l = 0; l < n; l++) {
									int p1 = temp[l][m];
									int p2 = g2[l + x][m + y];
									int err = p2 - p1;
									sum += (err * err);
								}
							}
							msd = sum / (n * n);
							sum = 0;

							if (msd <= prevMSD) {
								if (msd == prevMSD) {
									c++;
								}
								prevMSD = msd;
								mx = x - i;
								my = y - j;
								mx2[(i / n)][(j / n)] = mx;
								my2[(i / n)][(j / n)] = my;
							}
						} else {
							c++;
						}

						if (c == count) {
							mx2[(i / n)][(j / n)] = 0;
							my2[(i / n)][(j / n)] = 0;
						}
					}
				}
				msd = 0;
				prevMSD = 999999999;
				c = 0;
			}
		}

	}

	private static void copyOver(Image img, int[][] G) {
		int[] rgb = new int[3];

		for (int y = 0; y < img.getH(); y++) {
			for (int x = 0; x < img.getW(); x++) {
				img.getPixel(x, y, rgb);
				G[x][y] = rgb[1];
			}
		}
	}

	private static void writeToFile(String timg, String rimg, int j, int k, int n, int[][] mx, int[][] my, int part) {

		String newFile = "mv_part" + part + ".txt";
		FileOutputStream fos = null;
		PrintWriter dos = null;

		try {
			fos = new FileOutputStream(newFile);
			dos = new PrintWriter(fos);

			System.out.println("Writing into " + newFile + "...");

			dos.print("# Name: Mher Oganesyan\n");
			dos.print("# Target image name: " + timg + "\n");
			dos.print("# Reference image name: " + rimg + "\n");
			dos.print("# Number of target macro blocks: " + j / n + " x " + k / n + " (Image size is: " + j + " x " + k
					+ ")\n\n");
			dos.flush();

			// write data
			for (int y = 0; y < k / n; y++) {
				for (int x = 0; x < j / n; x++) {
					dos.print("[ " + mx[x][y] + ", " + my[x][y] + "] ");
				}
				dos.print("\n");
			}
			dos.flush();
			dos.close();

			System.out.println("Wrote into " + newFile + " Successfully.");

		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

}
