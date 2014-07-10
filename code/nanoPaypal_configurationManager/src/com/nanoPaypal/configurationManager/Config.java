package com.nanoPaypal.configurationManager;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Configuration Manager supporting auto-reloading of config
 * @author vivek
 *
 */
public class Config {

	private static Set<String> notPresentSet = Collections
			.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
	private static Map<Object, String> cache = new ConcurrentHashMap<Object, String>();

	static String configFile = "config.properties";
	public static long lastChangeTime1 = 0;
	private static Properties props = new Properties();
	private static Map<Object, Object> staticVals = new HashMap<Object, Object>();
	static Map<String, String> files = new HashMap<String, String>();
	static Map<String, Long> fileTimes = new HashMap<String, Long>();
	private static Properties prop1 = new Properties();
	private static boolean timeTrack = false;
	private static boolean debugEnabled = false;
	static Long lastReloaded = 0L;

	private static String multiConfig_list = "";
	private static Map<String, Long> multiConfig_changeTimes = new HashMap<String, Long>();
	private static Map<String, Properties> multiConfig_properties = new HashMap<String, Properties>();

	private static void checkDynamicConfig() {
		int IGNORE_TIME = 20000;
		if (System.currentTimeMillis() - lastReloaded < IGNORE_TIME)
			return;

		synchronized (lastReloaded) {
			if (System.currentTimeMillis() - lastReloaded < IGNORE_TIME)
				return;
			lastReloaded = System.currentTimeMillis();
		}

		long changed1 = 0;
		boolean reload = false;

		synchronized (prop1) {
			changed1 = loadDataFromFile(configFile, prop1, lastChangeTime1);
		}

		if (lastChangeTime1 < changed1) {
			lastChangeTime1 = changed1;
			reload = true;
		}

		Properties tmp_prop_collector = new Properties();
		tmp_prop_collector.putAll(prop1);

		String new_multiConfig_list = prop1.getProperty("multiConfigFileList");

		try {
			File[] files = new File(prop1.getProperty("autoConfigPath"))
					.listFiles(new FilenameFilter() {
						@Override
						public boolean accept(File dir, String fileName) {
							if (fileName != null
									&& fileName.endsWith(".properties"))
								return true;
							return false;
						}
					});

			if (files != null && files.length > 0) {
				Arrays.sort(files);

				for (File file : files) {
					new_multiConfig_list += ","
							+ prop1.getProperty("autoConfigPath_relative")
							+ file.getName();
					System.out.println(new Date() + " LOADING "
							+ file.getAbsolutePath());
				}

				System.out.println(new Date() + " NEW_MULTIFILE "
						+ new_multiConfig_list);
			}

		} catch (Exception e) {
//			e.printStackTrace();
		}
		multiConfig_list = new_multiConfig_list;
		if (multiConfig_list != null) {
			for (String fileName : multiConfig_list.split(",")) {
				Properties tmp_prop = multiConfig_properties.get(fileName);
				if (tmp_prop == null)
					tmp_prop = new Properties();
				Long changeTime = multiConfig_changeTimes.get(fileName);
				if (changeTime == null)
					changeTime = 0L;
				Long changeTime_final = loadDataFromFile(fileName, tmp_prop,
						changeTime);
				if (changeTime < changeTime_final) {
					changeTime = changeTime_final;
					reload = true;
					multiConfig_changeTimes.put(fileName, changeTime);
				}
				tmp_prop_collector.putAll(tmp_prop);
				multiConfig_properties.put(fileName, tmp_prop);
			}
		}

		if (reload) {
			synchronized (props) {
				props.clear();
				props.putAll(tmp_prop_collector);
				notPresentSet.removeAll(props.keySet());
			}
			String tc = (String) props.get("TIMETRACK_ENABLED");
			if (tc != null && tc.equalsIgnoreCase("true"))
				timeTrack = true;
			else
				timeTrack = false;
			String debug = (String) props.get("debug");
			if (debug != null && debug.equalsIgnoreCase("true"))
				debugEnabled = true;
			else
				debugEnabled = false;
		}
		System.out.println(Calendar.getInstance().getTime()
				+ " CONFIGURATION CHECK-RELOAD " + reload);
	}

	private static long loadDataFromFile(String file, Properties props,
			long lastChangeTime) {
		long changedTime = lastChangeTime;
		try {
			File f = new File(file);
			long ltime = f.lastModified();
			if (ltime > lastChangeTime) {
				Properties propsDyn = new Properties();
				FileReader is = new FileReader(f);
				propsDyn.load(is);
				is.close();
				if (propsDyn != null && propsDyn.size() > 0) {
					String debug = (String) propsDyn.get("debug");
					props.clear();
					props.putAll(propsDyn);

					if (debug != null && debug.equalsIgnoreCase("true"))
						System.out.println("DEBUG LOADED SIZE " + props.size());

					System.out.println(Calendar.getInstance().getTime()
							+ " CONFIGURATION RELOADED with size :"
							+ propsDyn.size() + " " + file);
					System.out.println(Calendar.getInstance().getTime()
							+ " CONFIGURATION RELOADED with vaules :"
							+ propsDyn + " " + file);
					changedTime = ltime;
				} else {
					System.out.println(Calendar.getInstance().getTime()
							+ " CONFIGURATION RELOAD ERROR ");
				}
			}
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return changedTime;
	}

	public static String getFile(String fileName) throws Exception {
		String ret = files.get(fileName);
		File f = new File(fileName);
		if (f.exists()) {
			long ltime = f.lastModified();
			Long lastTime = fileTimes.get(fileName);
			if (lastTime == null)
				lastTime = 0L;
			if (ret == null || ltime > lastTime) {
				char[] b = new char[new Long(f.length()).intValue()];
				FileReader br = new FileReader(f);
				br.read(b);
				br.close();
				ret = new String(b);
				files.put(fileName, ret);
				fileTimes.put(fileName, ltime);
			}
		} else {
			System.out.println("FILENOTFOUND " + fileName);
		}
		return ret;
	}

	public static void setStaticVal(String key, Object val) {
		staticVals.put(key, val);
	}

	public static Object getStaticVal(String key) {
		return staticVals.get(key);
	}

	public static String getValue(String property) {
		checkDynamicConfig();

		if (cache.containsKey(property))
			return cache.get(property);
		String s = null;
		try {
			String debug = props.getProperty("debug");
			s = (String) props.get(property);
			if (debug == null || debug.equalsIgnoreCase("true"))
				System.out.println("DEBUG: CONFIG: " + property + " " + s);
			if (s == null && isDebugEnabled()) {
				System.out.println("Exception : Configuration error for "
						+ property);
			}
			return s;
		} finally {
			cache.put(property, s);
		}
	}

	public static boolean isTimeTrackEnabled() {
		return timeTrack;
	}

	public static boolean isDebugEnabled() {
		return debugEnabled;
	}

	public static String getValue(String suffix, ArrayList<String> property,
			String defaultValue) {
		checkDynamicConfig();
		String debug = props.getProperty("debug");
		String s = null;

		if (cache.containsKey(property))
			return cache.get(property);
		try {
			for (String key : property) {
				String id = key + "_" + suffix;
				s = (String) props.get(id);
				if (debug == null || debug.equalsIgnoreCase("true"))
					System.out.println("DEBUG: CONFIG: " + id + " " + s);
				if (s == null && isDebugEnabled()) {
					System.out.println("Exception : Configuration error for "
							+ id);
				}
				if (s != null)
					return s;
				else
					notPresentSet.add(id);
			}
			if (s == null)
				s = (String) props.get(suffix);

			if (s == null && isDebugEnabled()) {
				System.out.println("Exception : Configuration error for "
						+ suffix);
			}
			if (s == null)
				s = defaultValue;

			if (isDebugEnabled()) {
				System.out.println("FOUND " + suffix + "=" + s);
			}
			return s;
		} finally {
			cache.put(property, s);
		}
	}

	public static String getValue(ArrayList<String> property,
			String defaultValue) {
		checkDynamicConfig();

		if (cache.containsKey(property))
			return cache.get(property);
		String s = null;

		try {
			String debug = props.getProperty("debug");
			if (property != null)
				for (String key : property) {
					s = (String) props.get(key);
					if (debug == null || debug.equalsIgnoreCase("true"))
						System.out.println("DEBUG: CONFIG:=" + key + ";" + s);
					if (s == null && isDebugEnabled()) {
						System.out
								.println("Exception : Configuration error for= "
										+ key + ";");
					}
					if (s != null)
						return s;
					else
						notPresentSet.add(key);
				}
			if (s == null)
				s = defaultValue;
			return s;
		} finally {
			cache.put(property, s);
		}
	}

	public static ArrayList<String> getConfigKeys_withoutPrefix(
			String... configKeys) {
		ArrayList<String> al1 = new ArrayList<String>(Arrays.asList(configKeys));
		al1 = getOrderList(al1);
		return al1;
	}

	public static ArrayList<String> getConfigKeys(String suffix,
			String... configKeys) {
		ArrayList<String> al1 = new ArrayList<String>(Arrays.asList(configKeys));

		al1 = getOrderList(suffix, al1);
		al1.add(suffix);
		return al1;
	}

	public static ArrayList<String> getOrderList(String suffix,
			ArrayList<String> orig) {
		if (orig.size() > 1) {
			ArrayList<String> newList = new ArrayList<String>(orig);
			String val = newList.remove(0);
			ArrayList<String> combo = getOrderList(suffix, newList);
			int len = combo.size();
			ArrayList<String> combo1 = new ArrayList<String>();
			if (!notPresentSet.contains(val + "_" + suffix))
				combo1.add(val + "_" + suffix);
			for (int i = len - 1; i >= 0; i--) {
				if (!notPresentSet.contains(val + "_" + combo.get(i)))
					combo1.add(0, val + "_" + combo.get(i));
			}
			combo1.addAll(combo);
			return combo1;
		} else {
			orig.add(orig.remove(0) + "_" + suffix);
			return orig;
		}
	}

	public static ArrayList<String> getOrderList(ArrayList<String> orig) {
		if (orig.size() > 1) {
			ArrayList<String> newList = new ArrayList<String>(orig);
			String val = newList.remove(0);
			ArrayList<String> combo = getOrderList(newList);
			int len = combo.size();
			ArrayList<String> combo1 = new ArrayList<String>();
			if (!notPresentSet.contains(val))
				combo1.add(val);
			for (int i = len - 1; i >= 0; i--) {
				if (!notPresentSet.contains(val + "_" + combo.get(i)))
					combo1.add(0, val + "_" + combo.get(i));
			}
			combo1.addAll(combo);
			return combo1;
		} else {
			orig.add(orig.remove(0));
			return orig;
		}
	}
}