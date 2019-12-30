package com.bis.propertyfiletranslator.test;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;

import com.bis.propertyfiletranslator.JSONFunctions;
import com.bis.propertyfiletranslator.Translator;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;

public class PropertyFileTranslatorTest {

	private String input = "Test.JSON";
	private String output = "/output/Test.JSON";
	private String api = "ADD APIKEY";
	private String language = "fr";

	@Test
	public void testingTheFileGoesInAsEnglishAndReturnsAsFrench()
			throws IOException, GeneralSecurityException, ParseException {
		JSONFunctions instance = new JSONFunctions(input, output, api, language);
		;
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(new FileReader(input));

		assertEquals(
				"{\"snail_count\":\"%1 has eaten %2 snails out of %3 snails\",\"snail_count2\":\"1 % has eaten 1 % snails out of 3 % snails\",\"labelID2\":\"test\",\"labelID1\":\"hello\"}",
				obj.toString());
		assertEquals(
				"{snail_count=% 1 a mang�% 2 escargots sur% 3 escargots, snail_count2=1% a mang� 1% d'escargots sur 3% d'escargots, labelID2=tester, labelID1=Bonjour}",
				instance.jsonHashMap.toString());

	}

	@Test
	public void testingErrorMessageForIncorrectAPIKey() throws IOException, GeneralSecurityException {
		List<String> subListMock = new ArrayList<String>();
		subListMock.add("test string");
		try {
			Translator.translateJSONMapThroughGoogle(input, output, "AIzaSyBon48cdlCWaRuJEbsZFOshcBmeyTGiT7", language,
					subListMock);
			fail("GoogleJsonResponseException Not Met");
		} catch (GoogleJsonResponseException e) {
			assertEquals("API key not valid. Please pass a valid API key.", e.getDetails().getMessage());
		}

	}

	@Test
	public void testingErrorMessageForIncorrectLanguageCode() throws IOException, GeneralSecurityException {
		List<String> subListMock = new ArrayList<String>();
		subListMock.add("test string");

		try {
			Translator.translateJSONMapThroughGoogle(input, output, api, "FROG", subListMock);
			fail("GoogleJsonResponseException Not Met");
		} catch (GoogleJsonResponseException e) {
			assertEquals("Invalid Value", e.getDetails().getMessage());

		}

	}

	@Test
	public void testingErrorMessageForIncorrectInputFile() throws IOException, GeneralSecurityException {

		ByteArrayOutputStream errContent = new ByteArrayOutputStream();
		System.setErr(new PrintStream(errContent));
		String errorFileName = "NotARealFile.JSON";
		JSONFunctions.readJsonFileIntoMap(errorFileName, output, api, language);
		assertEquals(
				System.getProperty("line.separator") + "The file in directory : '" + errorFileName
						+ "' cannot be found, check the path and try again." + System.getProperty("line.separator"),
				errContent.toString());

	}

	@Test
	public void testingErrorMessageForIncorrectOutputFile() throws IOException, GeneralSecurityException {

		ByteArrayOutputStream errContent = new ByteArrayOutputStream();
		System.setErr(new PrintStream(errContent));
		String errorFileName = "Test/NotARealFile.JSON";
		JSONFunctions.outputTranslationsBackToJson(errorFileName);
		assertEquals(System.getProperty("line.separator") + "The file out name : '" + errorFileName
				+ "' cannot be used, make sure it is a file name and not a path."
				+ System.getProperty("line.separator"), errContent.toString());

	}
}
