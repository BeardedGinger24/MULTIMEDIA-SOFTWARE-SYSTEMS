package HW2;

public class Menu extends CS4551_Oganesyan {

	public static void main() {

		System.out.println("--Welcome to Multimedia Software System--");

		int menu;

		do {
			System.out.println("Main Menu-----------------------------------\n" + "1. Aliasing\n"
					+ "2. Dictionary Coding\n" + "3. Quit\n\n" + "Please enter the task number [1-3]:");
			menu = sc.nextInt();
			switch (menu) {
			case 1:
				System.out.println("Give a thickeness, M, for a circle in pixels:");
				int M = sc.nextInt();
				System.out.println("Give a difference, N, for the successive radii:");
				int N = sc.nextInt();
				System.out.println("Give a subsample value, K:");
				int K = sc.nextInt();
				System.out.println("M: " + M);
				System.out.println("N: " + N);
				System.out.println("K: " + K);

				Image circle = new Image(512, 512);
				circle.background();
				circle.createCircles(M, N);
				circle.display();
				circle.write2PPM("Cirlce_" + M + "_" + N + ".ppm");

				noFilter(circle, K, M, N);
				filterOne(circle, K, M, N);
				filterTwo(circle, K, M, N);

				break;
			case 2:
				LZW compression = new LZW();
				System.out.println("Please input a filename (including the extension, e.g. '.txt'):");
				String filename = sc.next();
				compression.main(filename);

				break;
			case 3:
				break;
			}

		} while (menu != 3);
		System.out.println("--Good Bye--");

	}

	static void filterTwo(Image img, int K, int M, int N) {
		Image newimage = new Image(img.getW() / K, img.getH() / K);
		int[] rgb = new int[3];

		for (int y = 0; y < img.getH(); y += K) {
			for (int x = 0; x < img.getW(); x += K) {
				double avg = 0;
				double denom = 0;
				double f1 = 0, f2 = 0, f3 = 0, f4 = 0, c = 0, f5 = 0, f6 = 0, f7 = 0, f8 = 0;
				if (x - 1 >= 0 && y - 1 >= 0) {
					img.getPixel(x - 1, y - 1, rgb);
					f1 += rgb[0];
					denom++;
				}
				if (y - 1 >= 0) {
					img.getPixel(x, y - 1, rgb);
					f2 += rgb[0];
					denom += 2;
				}
				if (x + 1 < img.getW() && y - 1 >= 0) {
					img.getPixel(x + 1, y - 1, rgb);
					f3 += rgb[0];
					denom++;
				}
				if (x - 1 >= 0) {
					img.getPixel(x - 1, y, rgb);
					f4 += rgb[0];
					denom += 2;
				}
				{
					img.getPixel(x, y, rgb);
					c += rgb[0];
					denom += 4;
				}
				if (x + 1 < img.getW()) {
					img.getPixel(x + 1, y, rgb);
					f5 += rgb[0];
					denom += 2;
				}
				if (x - 1 >= 0 && y + 1 < img.getH()) {
					img.getPixel(x - 1, y + 1, rgb);
					f6 += rgb[0];
					denom++;
				}
				if (y + 1 < img.getH()) {
					img.getPixel(x, y + 1, rgb);
					f7 += rgb[0];
					denom += 2;
				}
				if (x + 1 < img.getW() && y + 1 < img.getH()) {
					img.getPixel(x + 1, y + 1, rgb);
					f8 += rgb[0];
					denom++;
				}

				avg = (1 / denom * f1) + (2 / denom * f2) + (1 / denom * f3) + (2 / denom * f4) + (4 / denom * c)
						+ (2 / denom * f5) + (1 / denom * f6) + (2 / denom * f7) + (1 / denom * f8);
				rgb[0] = (int) avg;
				rgb[1] = (int) avg;
				rgb[2] = (int) avg;
				newimage.setPixel(x / K, y / K, rgb);
			}
		}
		newimage.display();
		newimage.write2PPM("Cirlce_" + M + "_" + N + "_K" + K + "_FilterTwo.ppm");
	}

	static void filterOne(Image img, int K, int M, int N) {
		Image newimage = new Image(img.getW() / K, img.getH() / K);
		int[] rgb = new int[3];

		for (int y = 0; y < img.getH(); y += K) {
			for (int x = 0; x < img.getW(); x += K) {
				double avg = 0;
				double denom = 0;
				if (x - 1 >= 0 && y - 1 >= 0) {
					img.getPixel(x - 1, y - 1, rgb);
					avg += rgb[0];
					denom++;
				}
				if (y - 1 >= 0) {
					img.getPixel(x, y - 1, rgb);
					avg += rgb[0];
					denom++;
				}
				if (x + 1 < img.getW() && y - 1 >= 0) {
					img.getPixel(x + 1, y - 1, rgb);
					avg += rgb[0];
					denom++;
				}
				if (x - 1 >= 0) {
					img.getPixel(x - 1, y, rgb);
					avg += rgb[0];
					denom++;
				}
				{
					img.getPixel(x, y, rgb);
					avg += rgb[0];
					denom++;
				}
				if (x + 1 < img.getW()) {
					img.getPixel(x + 1, y, rgb);
					avg += rgb[0];
					denom++;
				}
				if (x - 1 >= 0 && y + 1 < img.getH()) {
					img.getPixel(x - 1, y + 1, rgb);
					avg += rgb[0];
					denom++;
				}
				if (y + 1 < img.getH()) {
					img.getPixel(x, y + 1, rgb);
					avg += rgb[0];
					denom++;
				}
				if (x + 1 < img.getW() && y + 1 < img.getH()) {
					img.getPixel(x + 1, y + 1, rgb);
					avg += rgb[0];
					denom++;
				}

				avg /= denom;
				rgb[0] = (int) avg;
				rgb[1] = (int) avg;
				rgb[2] = (int) avg;
				newimage.setPixel(x / K, y / K, rgb);
			}
		}
		newimage.display();
		newimage.write2PPM("Cirlce_" + M + "_" + N + "_K" + K + "_FilterOne.ppm");
	}

	static void noFilter(Image img, int K, int M, int N) {
		// ask user for k
		Image newimage = new Image(img.getW() / K, img.getH() / K);
		int[] rgb = new int[3];

		for (int y = 0; y < img.getH(); y += K) {
			for (int x = 0; x < img.getW(); x += K) {
				img.getPixel(x, y, rgb);
				newimage.setPixel(x / K, y / K, rgb);
			}
		}
		newimage.display();
		newimage.write2PPM("Cirlce_" + M + "_" + N + "_K" + K + "_NoFilter.ppm");

	}

}
