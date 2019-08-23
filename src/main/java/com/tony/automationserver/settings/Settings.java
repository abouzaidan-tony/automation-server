package com.tony.automationserver.settings;

import org.json.JSONException;
import org.json.JSONObject;


public class Settings {
	private static Settings instance = new Settings();
	private String _host;
	private String _dbase;
	private String _user;
	private String _password;
    private String _port;
    
    private boolean init;

	public Settings() {
        init = false;
	}

	public static Settings getInstance() {
		return instance;
	}

	public String host() {
		return _host;
	}

	public String database() {
		return _dbase;
	}

	public String user() {
		return _user;
	}

	public String password() {
		return _password;
	}

	public String port()
	{
		return _port;
	}

    public void init() throws JSONException { //inits settings from conf file
        if (init)
            return;
        init = true;
		JSONObject settingsObj = SettingsFileHelper.readSettings();
		_host = settingsObj.getString("host");
		_dbase = settingsObj.getString("database");
		_user = settingsObj.getString("user");
		_password = settingsObj.getString("password");
		_port = settingsObj.getString("port");
	}
}
