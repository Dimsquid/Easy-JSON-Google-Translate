package com.bis.propertyfiletranslator;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class Main {
	public static void main(String[] args) throws IOException, GeneralSecurityException {

		// Variables which hold the values that come in from the arguments
		String inputPath = "";
		String outputPath = "";
		String apiKey = "";
		String language = "";
		String cmd = "";

		// For loop which will run for however many arguments are passed in through the
		// cli.
		for (String arg : args) {

			// Switch statement which is checking the value of the cli arguments and will
			// set the variables to what ever comes after the arg value
			switch (cmd.toLowerCase()) {
			case "-f":
				inputPath = arg;
				break;
			case "-o":
				outputPath = arg;
				break;
			case "-l":
				language = arg;
				break;
			case "-a":
				apiKey = arg;
				break;
			default:
				break;
			}
			cmd = arg;
		}

		new JSONFunctions(inputPath, outputPath, apiKey, language);
	}

}
