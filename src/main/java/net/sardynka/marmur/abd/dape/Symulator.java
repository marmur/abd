package net.sardynka.marmur.abd.dape;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class Symulator {
	public static void main(String[] args) throws InterruptedException,
			IOException {
		if (args.length != 2) {
			printUsage("Wrong number of parameters");
		}

		String host = args[0];
		String fileName = args[1];
		List<String[]> coms = new ArrayList<String[]>(10);

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File(fileName)));
		} catch (FileNotFoundException e) {
			printUsage("File does no exist");
		}
		String line;
		try {
			while ((line = br.readLine()) != null) {
				if (line.trim().length() == 0) {
					// empty line
					continue;
				} else if (line.startsWith("#")) {
					// comment line
					continue;
				}
				String[] command = line.split(",");
				String c = command[0].trim();
				if (c.equalsIgnoreCase("send")) {
					if (command.length != 4) {
						printUsage("Error parsing file");
					}
				} else if (c.equalsIgnoreCase("getRead")) {
					if (command.length != 5) {
						printUsage("Error parsing file");
					}
				} else if (c.equalsIgnoreCase("getWrite")) {
					if (command.length != 5) {
						printUsage("Error parsing file");
					}
				} else if (c.equalsIgnoreCase("sleep")) {
					if (command.length != 2) {
						printUsage("Error parsing file");
					}
				}
				coms.add(command);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (String[] strings : coms) {
			process(host, strings);
		}
	}

	private static void process(String host, String[] command)
			throws InterruptedException, IOException {
		String c = command[0].trim();
		long result = -1;
		if (c.equalsIgnoreCase("send")) {
			send(host, command[1].trim(), ctl(command[2]), ctl(command[3]));
		} else if (c.equalsIgnoreCase("getRead")) {
			result = getRead(host, command[1].trim(), ctl(command[2]));
		} else if (c.equalsIgnoreCase("getWrite")) {
			result = getWrite(host, command[1].trim(), ctl(command[2]));
		} else if (c.equalsIgnoreCase("sleep")) {
			Thread.sleep(ctl(command[1]));
		}

		if (result >= 0) {
			long expected = ctl(command[3]);
			long perc = ctl(command[4]);

			long minexp = (expected * (100 - perc)) / 100;
			long maxexp = (expected * (100 + perc)) / 100 + 1;
			
			System.out.println(minexp);
			System.out.println(maxexp);

			if (result < minexp || result > maxexp) {
				System.out.print("Result of operation ");
				System.out.print(c);
				System.out.println(" does not match expected value");
				System.out.print(" Got:      ");
				System.out.println(result);
				System.out.print(" Expected: ");
				System.out.println(ctl(command[3]));
			} else {
				System.out.print("Result of operation ");
				System.out.print(c);
				System.out.println(" does match expected value");
			}
		}
	}

	private static long getWrite(String host, String h, long w)
			throws IOException {
		// http://127.0.0.1:63563/e/write/host/{host}/size/{fileSize}
		String spec = host + "e/write/host/" + URLEncoder.encode(h, "UTF-8")
				+ "/size/" + w;
		String t = getContent(spec);

		return ctl(t);
	}

	private static long getRead(String host, String h, long w)
			throws IOException {
		// http://127.0.0.1:63563/e/read/host/{host}/size/{fileSize}
		String spec = host + "e/read/host/" + URLEncoder.encode(h, "UTF-8")
				+ "/size/" + w;
		String t = getContent(spec);

		return ctl(t);
	}

	private static void send(String host, String h, long r, long w)
			throws IOException {
		// http://127.0.0.1:63563/e/push/host/{host}/written/{written}/read/{read}
		String spec = host + "e/push/host/" + URLEncoder.encode(h, "UTF-8")
				+ "/written/" + w + "/read/" + r;
		getContent(spec);
	}

	private static long ctl(String s) {
		return Long.parseLong(s.trim());
	}

	private static String getContent(String spec) throws MalformedURLException,
			IOException {
		URL yahoo = new URL(spec);
		BufferedReader in = new BufferedReader(new InputStreamReader(
				yahoo.openStream()));

		String inputLine;
		String result = "";

		while ((inputLine = in.readLine()) != null) {
			//System.out.println(inputLine);
			result += inputLine;
		}

		in.close();
		return result;
	}

	private static void printUsage(String message) {
		System.out.println(message);
		System.out.println();
		System.out
				.println("Usage: java Symulator.java 'service address' 'input file'");
		System.exit(13);
	}
}
