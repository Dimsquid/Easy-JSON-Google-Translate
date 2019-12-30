package com.bis.propertyfiletranslator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.security.GeneralSecurityException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JSONFunctions {

	public static Map<String, String> jsonHashMap = new LinkedHashMap<String, String>();
	public static Map<String, Byte> jsonHashMap2 = new LinkedHashMap<String, Byte>();
	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	public static Object obj;

	public JSONFunctions(String input, String output, String API, String language)
			throws IOException, GeneralSecurityException {
		readJsonFileIntoMap(input, output, API, language);
	}

	// Method that will read all the key variables from the JSON file into a Linked
	// hash map
	@SuppressWarnings("unchecked")
	public static void readJsonFileIntoMap(String input, String output, String API, String language)
			throws IOException, GeneralSecurityException, FileNotFoundException {

		// Defining a new instance of JSON parser which comes from a library called
		// json-simple-1.1
		JSONParser parser = new JSONParser();

		// This try is used to catch any issues with the input file.
		try {

			// Creating a new object instance called object which is eqaul to the JSON
			// parser instance which is reading the input file and trying to convert the
			// file to a JSON object
			obj = parser.parse(new FileReader(input));

		} catch (FileNotFoundException e) {

			// Printing out a console error which will tell the user what is wrong with the
			// file path they entered.
			System.err.println(System.getProperty("line.separator") + "The file in directory : '" + input
					+ "' cannot be found, check the path and try again.");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Making a new json object which is also from json-simple, and it is casting
		// the normal object as a JSONObject

		JSONObject jsonObj = (JSONObject) obj;

		// This for each is used to split the current JSON object into a new Hashmap
		// using the key from the JSONObject as the key for the Hashmap and the Value of
		// the Object as the Value of the Hashmap Value

		jsonObj.forEach((k, v) -> jsonHashMap.put(k.toString(), v.toString()));

		// This catch is to check to see if the file exists.

		// This is now setting the max length that one of the subsidiary arrays can be,
		// reason for this is the google translate api can only take in 128 translations
		// at once so we are spliting it down to 100 so we know we are not going over
		// that number.

		int arrayMaxLength = 100;

		// Here we are making a new list which only contains the values from the hasmap
		// as we don't need to translate the keys, but need them for later.
		List<String> items = new LinkedList<String>(jsonHashMap.values());

		// Creating a new int which casts the size of items.size as an integer which is
		// divided by the max length we defined them cast as a double which is rounded
		// up, this is used to check how many results we have in the array and is used
		// in the for loop so we can work out the correct number of sub arrays we need.
		int subsidiaryArrays = (int) Math.ceil((double) items.size() / arrayMaxLength);

		// This for loop is checking whilst i is smaller than the subsidiaryArrays and
		// will loop through the next bits of code.
		for (int i = 0; i < subsidiaryArrays; i++) {
			// Here we are making a new list which is equal to a sublist of items we use a
			// ternary if statement here to check when items doesn't include anymore
			// results, the first bit is defining the starting index and then the second
			// half after the comma is defining the end index of the last element to
			// include.
			List<String> sub = items.subList(i * arrayMaxLength,
					((i + 1) * arrayMaxLength > items.size() ? items.size() : (i + 1) * arrayMaxLength));

			// After a sublist has been made we then push that over to the actual
			// translation portion of the code.
			Translator.translateJSONMapThroughGoogle(input, output, API, language, sub);

		}
	}

	public static void outputTranslationsBackToJson(String output) {

		// Now we are creating a new File instance which is equal to the output file
		// that the user defines.
		File finalOutput = new File(output);

		try {
			// Then we are making a new file writer instance.

			Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(finalOutput), "UTF8"));
			// Here we are converting the object to a string and then writing that to the
			// new file.
			out.write(gson.toJson(jsonHashMap));

			// This is just to clear the stream this is just good etiquette for writing to a
			// file.
			out.flush();

			// Then you always need to close the file afterwards also
			out.close();
			// This exception is used to check that the file name is just a string and not a
			// path if the user enters a path they will get an error.
		} catch (IOException e) {
			System.err.println(System.getProperty("line.separator") + "The file out name : '" + output
					+ "' cannot be used, make sure it is a file name and not a path.");
		}

	}

}
