package com.uf.jsonutils;

import com.uf.jsonutils.object.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class JSONFile {
	private static Runnable fileLoadEvent = ()->{}, fileNotExistEvent = ()->{};
	public static void setFileLoadEvent(Runnable fileLoadEvent) {
		JSONFile.fileLoadEvent = fileLoadEvent;
	}
	public static void setFileNotExistEvent(Runnable fileNotExistEvent) {
		JSONFile.fileNotExistEvent = fileNotExistEvent;
	}
	public static final String USER_HOME = System.getProperty("user.home");
	public static final String APPDATA = System.getProperty("user.home") + "/AppData/Roaming/";
	private static String directory;
	private static JSONObject load;
	public static String getDirectory() { return directory; }
	public static void setDirectory(String arg0) { directory = arg0; load(); }
	public static void load() {
		try {
			File f = new File(directory);
			boolean b = false;
			if(!f.isFile()) {
			f.createNewFile();
			FileWriter w = new FileWriter(f);
			w.write("{}");
			w.close();
			b = true;
			}
			JSONFile.load = JSONObject.parse(new FileReader(f));
			if(b) fileNotExistEvent.run();
			fileLoadEvent.run();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void save() {
		try {
			FileWriter filewriter = new FileWriter(new File(directory));
			filewriter.write(JSONString.Tab(load.toJSONString(), true));
			filewriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void save(String arg0, Object arg1) { load.put(arg0, arg1); save(); }
	public static Object get(String arg0) { return load.get(arg0); }
	public static void remove(String arg0) { load.remove(arg0); save(); }
	public static boolean containsKey(String arg0) { return load.containsKey(arg0); }
	public static void putAll(JSONObject o) { load.putAll(o); save(); }
	public static void overwrite(JSONObject o) { load = o; save(); }
}