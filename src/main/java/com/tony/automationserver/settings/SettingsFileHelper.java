package com.tony.automationserver.settings;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.JSONException;
import org.json.JSONObject;

public class SettingsFileHelper {

	private static String SETTINGS_FILE_PATH= "settings.json";
	
	public static JSONObject readSettings() throws JSONException {
		String data = null;
		Path path = Paths.get(SETTINGS_FILE_PATH);
		try {
			if (Files.notExists(path)) {
				BufferedWriter writer = new BufferedWriter(new FileWriter(SETTINGS_FILE_PATH));
				writer.write(sampleSettings());
				writer.close();
//				Logger.getInstance().log("Failed to read settings. Please update the settings file and try again");
			}
			data = new String(Files.readAllBytes(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new JSONObject(data);
	}

	private static String sampleSettings() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("host", "insert host name here");
			jsonObject.put("port", "insert port here");
			jsonObject.put("database", "insert db name here");
			jsonObject.put("user", "insert db user here");
			jsonObject.put("password", "insert db password here");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject.toString();
	}
}
