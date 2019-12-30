package com.bis.propertyfiletranslator;

import java.io.FileWriter;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.translate.Translate;
import com.google.api.services.translate.model.TranslationsListResponse;
import com.google.api.services.translate.model.TranslationsResource;

public class Translator {

	public static Translate.Translations.List list;

	public static void translateJSONMapThroughGoogle(String input, String output, String API, String language,
			List<String> subLists) throws IOException, GeneralSecurityException {

		// Here we are creating a new instance of the Translate builder provided by
		// google cloud translation library. This is makes a new certificate each time
		// it is ran so google can authorise and track requests. We then are creating a
		// new JackSonFactory instance this is a JSON library also provided by google,
		// the Application name that is then set is again for tracking implemntation in
		// the console.developers portal from google, its used to keep track of
		// different applications that you may be using.
		Translate t = new Translate.Builder(GoogleNetHttpTransport.newTrustedTransport(),
				JacksonFactory.getDefaultInstance(), null).setApplicationName("PhoenUX-Google-Translate").build();
		try {

			// We are then making a translation list which is equal to one of the sub lists
			// that we created, here we also need to pass in the language that we want to
			// translate to, this is set now so google can build the list. The setFormat is
			// defauolt of html but this can mess with the formatting of some symbols so we
			// have set it to text
			list = t.new Translations().list(subLists, language).setFormat("text");

			// We then set our api key which is passed in through the cli, this is now
			// making a header for our request and the setKey is adding credentials and
			// converting it into a token that can be read by google
			list.setKey(API);

			// This catch will catch any 400 request that is thrown by the Request
		} catch (GoogleJsonResponseException e) {

			// This response is for using an incorrect language code, so we do a search for
			// it as it is quite a generic message and we log our own error.
			if (e.getDetails().getMessage().equals("Invalid Value")) {
				System.err.println(
						"\n Language not currently supported, check the accepted language codes and try again.\n\n Language Requested: "
								+ language);
			} else {
				// The other error messages are not as generic as the wrong language message so
				// we are allowing it to post the message that google supplies by default.
				System.out.println(e.getDetails().getMessage());
			}
		}

		// Now we are creating a response variable which is equal to the response of the
		// executed header request.
		TranslationsListResponse response = list.execute();
		FileWriter test = new FileWriter("text.txt");

		// This for loop is translated to for each response get the response as one of
		// the translations
		for (TranslationsResource translationsResource : response.getTranslations()) {

			// This for loop is basically the same but for the keys in the Hashmap that was
			// set up when reading from the input file earlier.
			for (String key : JSONFunctions.jsonHashMap.keySet()) {

				// Here the value that was set to what ever this key is, is being removed.
				JSONFunctions.jsonHashMap.remove(key);

				// Setting a variable of the current translation.
				String value = translationsResource.getTranslatedText();

				String encoded = new String(value);

				// The translated text is then put back in next to the key it came in with.
				JSONFunctions.jsonHashMap.put(key, encoded);
				break;
			}
		}

		// Once the for loop has been completed and there are no more sub lists to go
		// through we then move onto outputing the results back out to a specified file
		// name.

		JSONFunctions.outputTranslationsBackToJson(output);
		test.close();
	}

}
