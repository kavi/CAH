package dk.javacode.cah.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

public class ConfigValues {
	private Properties properties;

	public ConfigValues(Properties properties) {
		super();
		this.properties = properties;
	}

	public static ConfigValues load(String filename) {
		return load(new File(filename));
	}

	public static ConfigValues load(File file) {
		try {
			return load(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static ConfigValues load(InputStream stream) {
		try {
			Properties p = new Properties();
			List<String> lines = IOUtils.readLines(stream);
			String prefix = null;
			for (String l : lines) {
				if (l == null || l.trim().equals("")) {
					continue;
				}
				if (l.startsWith("[") && l.endsWith("]")) {
					prefix = l.substring(1, l.length() - 1);
				} else {
					String[] keyval = l.split("=");
					String key = keyval[0].trim();
					String value = keyval[1].trim();
					if (prefix == null) {
						p.setProperty(key, value);
					} else {
						p.setProperty(prefix + "." + key, value);
					}
				}
			}
			return new ConfigValues(p);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String getString(String key) {
		return properties.getProperty(key);
	}

	public String getString(String key, String defaultValue) {
		String propertyValue = properties.getProperty(key);
		if (propertyValue == null) {
			return defaultValue;
		} else {
			return propertyValue;
		}
	}

	public int getInteger(String key) {
		return Integer.parseInt(properties.getProperty(key));
	}

	public int getInteger(String key, int defaultValue) {
		try {
			return Integer.parseInt(properties.getProperty(key));
		} catch (RuntimeException e) {
			return defaultValue;
		}
	}

	@Override
	public String toString() {
		return "ConfigValues [properties=" + properties + "]";
	}

}
