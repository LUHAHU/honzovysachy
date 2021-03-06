package cz.honzovysachy.resouces;

import java.net.URL;
import java.util.Properties;
import java.io.InputStreamReader;

public class S {
	private static Properties strings;

	public static void init(String language) {
		URL url = S.class.getResource("strings_" + language + ".txt");
		strings = new Properties();
		try {
			strings.load(new InputStreamReader(url.openStream(),"utf-8"));
		} catch (Exception e) {
			e.printStackTrace();
		};
	}
	
	public static String g(String key) {
		try {
			String ret = strings.getProperty(key);
			if (ret == null) return key;
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
}
