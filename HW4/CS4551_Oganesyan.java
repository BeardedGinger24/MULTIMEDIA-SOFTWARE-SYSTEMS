package HW4;

import java.util.Scanner;

/*******************************************************
 * CS4551 Multimedia Software Systems @ Author: Elaine Kang
 *******************************************************/

public class CS4551_Oganesyan {
	static Scanner sc = new Scanner(System.in);
	static Menu menu = new Menu();

	public static void main(String[] args) {
		// if (args.length != 0) {
		// usage();
		// System.exit(1);
		// }

		Menu.main();
		System.exit(1);
	}

	public static void usage() {
		System.out.println("\nUsage: java CS4551_Main [input_ppm_file]\n");
	}
}