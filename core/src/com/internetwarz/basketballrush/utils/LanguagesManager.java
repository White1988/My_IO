package com.internetwarz.basketballrush.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;

import java.util.HashMap;


//http://saltares.com/blog/games/internationalization-for-libgdx-projects/
public class LanguagesManager {
    private static LanguagesManager _instance = null;

    private static final String LANGUAGES_FILE = "media/languages.xml";
    private static final String DEFAULT_LANGUAGE = "EN";
    private final Preferences prefs;

    //private HashMap<String, HashMap<String, String>> _strings = null;
    private HashMap<String, String> _language = null;
    private String _languageName = null;

    private LanguagesManager() {
        prefs = Gdx.app.getPreferences("My Preferences");
        _languageName = prefs.getString("Language");
        System.out.println("Loaded: " + _languageName);
        // Create language map
        _language = new HashMap<String, String>();



        if (!loadLanguage(_languageName)) {
            System.out.println("Wasn't loaded");
            loadLanguage(DEFAULT_LANGUAGE);
            _languageName = DEFAULT_LANGUAGE;
        }
    }

    public static LanguagesManager getInstance() {
        if (_instance == null) {
            _instance = new LanguagesManager();
        }

        return _instance;
    }

    public String getLanguage() {
        return _languageName;
    }

    public String getString(String key) {
        String string;

        if (_language != null) {
            // Look for string in selected language
            string = _language.get(key);

            if (string != null) {
                return string;
            }
        }

        // Key not found, return the key itself
        return key;
    }

//	public String getString(String key, Object... args) {
//		return String.format(getString(key), args);
//	}

    public boolean loadLanguage(String languageName) {
        try {
            XmlReader reader = new XmlReader();
            XmlReader.Element root = reader.parse(Gdx.files.internal(LANGUAGES_FILE).reader("UTF-8"));

            Array<XmlReader.Element> languages = root.getChildrenByName("language");

            for (int i = 0; i < languages.size; ++i) {
                XmlReader.Element language = languages.get(i);

                if (language.getAttribute("name").equals(languageName)) {
                    _language.clear();
                    Array<XmlReader.Element> strings = language.getChildrenByName("string");

                    for (int j = 0; j < strings.size; ++j) {
                        XmlReader.Element string = strings.get(j);
                        String value = string.getAttribute("value");
                        String key = string.getAttribute("key");
                        value = value.replace("&lt;br /&gt;&lt;br /&gt;", "\n");
                        _language.put(key, value);
                    }
                    _languageName = languageName;
                    return true;
                }
            }
        }
        catch (Exception e) {
            System.out.println("Error loading languages file " + LANGUAGES_FILE);
            e.printStackTrace();
            return false;
        }

        return false;
    }
}