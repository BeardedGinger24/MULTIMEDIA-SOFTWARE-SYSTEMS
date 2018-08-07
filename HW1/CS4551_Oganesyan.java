package HW1;

import java.util.Scanner;

/*******************************************************
 * CS4551 Multimedia Software Systems @ Author: Elaine Kang
 *******************************************************/

public class CS4551_Oganesyan {
	static Scanner sc = new Scanner(System.in);
	static Menu menu = new Menu();

	public static void main(String[] args) {
		// if there is no commandline argument, exit the program
		if (args.length != 1) {
			usage();
			System.exit(1);
		}

		Menu.main(args);
		System.exit(1);
	}

	public static void usage() {
		System.out.println("\nUsage: java CS4551_Main [input_ppm_file]\n");
	}
}