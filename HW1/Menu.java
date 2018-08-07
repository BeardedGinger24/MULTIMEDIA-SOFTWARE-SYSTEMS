package HW1;

public class Menu extends CS4551_Oganesyan {

	public static void main(String[] args) {

		System.out.println("--Welcome to Multimedia Software System--");

		int menu;
		String name = new String(args[0]);
		name = name.split("\\.", 2)[0];

		do {
			System.out.println(
					"Main Menu----------------------------------- \n 1. Conversion to Gray-scale Image (24bits->8bits) \n 2. Conversion to N-level Image \n 3. Conversion to 8bit Indexed Color Image using Uniform Color Quantization (24bits->8bits) \n 4. Quit \n\n Please enter the task number [1-4]:");
			menu = sc.nextInt();
			switch (menu) {
			case 1:
				Image img = new Image(args[0]);
				img.grayScale();
				img.display();
				img.write2PPM(name + "-gray.ppm");
				break;
			case 2:
				Image img2 = new Image(args[0]);
				Image img3 = new Image(args[0]);
				System.out.println("Select 2 (1bit), 4 (2bits), 8 (3bits), or 16 (4 bits):");
				int N = sc.nextInt();
				if ((N == 2) || (N == 4) || (N == 8) || (N == 16)) {
					img2.grayScale();
					img2.threshold(N);
					img2.display();
					if (N == 2) {
						img2.write2PPM(name + "-threshold-2level.ppm");
					} else if (N == 4) {
						img2.write2PPM(name + "-threshold-4level.ppm");
					} else if (N == 8) {
						img2.write2PPM(name + "-threshold-8level.ppm");
					} else if (N == 16) {
						img2.write2PPM(name + "-threshold-16level.ppm");
					}

					img3.grayScale();
					img3.errorDiffusion(N);
					img3.display();
					if (N == 2) {
						img3.write2PPM(name + "-errorDuffusion-2level.ppm");
					} else if (N == 4) {
						img3.write2PPM(name + "-errorDuffusion-4level.ppm");
					} else if (N == 8) {
						img3.write2PPM(name + "-errordiffusion-8level.ppm");
					} else if (N == 16) {
						img3.write2PPM(name + "-errordiffusion-16level.ppm");
					}

				} else {
					System.out.println("N-value entered is not supproted");
				}
				break;
			case 3:
				Image img4 = new Image(args[0]);
				img4.lut();
				img4.index();
				img4.display();
				img4.write2PPM(name + "-index.ppm");
				img4.qt8();
				img4.display();
				img4.write2PPM(name + "-QT8.ppm");
				break;
			case 4:
				break;
			}

		} while (menu != 4);
		System.out.println("--Good Bye--");
	}

}
