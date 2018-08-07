package HW2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class LZW {

	String fileText;
	ArrayList<String> initial = new ArrayList<String>();

	public void main(String filename) {
		ArrayList<String> dictionary = new ArrayList<String>();
		ArrayList<Integer> data = new ArrayList<Integer>();

		readFile(filename);
		encoder(dictionary, data);

		System.out.println();
		System.out.println("Index		Entry");
		System.out.println("_______________________");

		for (int i = 0; i < dictionary.size(); i++) {
			System.out.print(i + "            ");
			System.out.println(dictionary.get(i));
		}
		System.out.println();
		System.out.println();

		System.out.println("Encoded: ");
		for (int i = 0; i < data.size(); i++) {
			System.out.print(data.get(i) + " ");
		}
		System.out.println();

		// Compression Ratio
		double bits = Math.ceil(Math.log(dictionary.size()) / Math.log(2));
		double compRatio = (fileText.length() * 8) / (data.size() * bits);
		System.out.println("\nCompression Ratio:	" + compRatio);

		System.out.println();
		ArrayList<String> decoded = new ArrayList<String>();
		decoder(data, decoded);
		System.out.println("Decoded: ");
		for (int i = 0; i < decoded.size(); i++) {
			System.out.print(decoded.get(i));
		}
		System.out.println();

		writeToFile(data, decoded, dictionary, compRatio, filename);
	}

	public void writeToFile(ArrayList<Integer> data, ArrayList<String> decoded, ArrayList<String> dictionary,
			double compRatio, String filename) {
		String name = new String(filename);
		name = name.split("\\.", 2)[0];

		String newFile = name + "_output.txt";
		FileOutputStream fos = null;
		PrintWriter dos = null;

		try {
			fos = new FileOutputStream(newFile);
			dos = new PrintWriter(fos);

			System.out.println("Writing into " + newFile + "...");

			dos.print(filename + " Results:\n\n");
			dos.print("Original Test:\n");
			dos.print(fileText + "\n\n");
			dos.print("Index		Entry\n");
			dos.print("_______________________\n");
			dos.flush();

			// write data

			for (int i = 0; i < dictionary.size(); i++) {
				dos.print(i + "            ");
				dos.print(dictionary.get(i) + "\n");
			}
			dos.flush();

			dos.print("\nEncoded Text:\n");
			dos.flush();

			for (int i = 0; i < data.size(); i++) {
				dos.print(data.get(i) + " ");
			}
			dos.flush();

			dos.print("\n\nDecoded Text:\n");
			dos.flush();

			for (int i = 0; i < decoded.size(); i++) {
				dos.print(decoded.get(i));
			}

			dos.print("\n\nCompression Ration:	" + compRatio);
			dos.flush();
			dos.close();

			System.out.println("Wrote into " + newFile + " Successfully.");

		} // try
		catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public void decoder(ArrayList<Integer> data, ArrayList<String> decoded) {
		ArrayList<String> newDict = new ArrayList<String>();
		newDict.addAll(initial);

		for (int i = 0; i < data.size(); i++) {
			int k = data.get(i);
			decoded.add(newDict.get(k));

			if (i + 1 < data.size()) {
				if (newDict.contains(newDict.get(data.get(i + 1)))) {
					String nextChar = newDict.get(data.get(i + 1));
					nextChar = nextChar.charAt(0) + "";
					newDict.add(newDict.get(k) + nextChar);
				}
			} else {
				String nextChar = newDict.get(k);
				nextChar = nextChar.charAt(0) + "";
				newDict.add(newDict.get(k) + nextChar);
			}

		}

	}

	public void encoder(ArrayList<String> dictionary, ArrayList<Integer> data) {
		// initialize dictionary
		for (int i = 0; i < fileText.length(); i++) {
			if (!initial.contains("" + fileText.charAt(i))) {
				initial.add("" + fileText.charAt(i));
			}
		}
		dictionary.addAll(initial);

		String s = "";
		for (int i = 0; i < fileText.length(); i++) {

			String c = "" + fileText.charAt(i);
			if (dictionary.contains(s + c)) {
				s = s + c;
			} else {
				data.add(dictionary.indexOf(s));
				if (dictionary.size() < 256) {
					dictionary.add(s + c);
				}
				s = c;
			}

		}
		data.add(dictionary.indexOf(s));

	}

	public void readFile(String filename) {

		File file = new File(filename);
		try {
			String string = "";
			Scanner sc = new Scanner(file);
			while (sc.hasNextLine()) {
				string += sc.nextLine();
			}

			fileText = string;
			sc.close();

		} catch (Exception e) {
			System.out.println(e);
		}

	}

}
