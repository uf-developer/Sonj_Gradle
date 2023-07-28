package com.uf.jsonutils.object;

import com.uf.jsonutils.JSONString;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

@SuppressWarnings("serial")
public class JSONObject extends HashMap<String, Object>{
	
	@Override
	public Object put(String key, Object value) {
		if(value instanceof Number ||
			value instanceof String ||
			value instanceof AbstractMap ||
			value instanceof AbstractList ||
			value instanceof Boolean) 
		return super.put(key, value);
		return new IllegalArgumentException();
	}
	public JSONObject() {
		super();
	}
	public JSONObject(Map<? extends String, ? extends Object> m) {
        super();
        putAll(m);
	}
	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		for(Entry<? extends String, ? extends Object> entry : m.entrySet())
			if(!(entry.getValue() instanceof Number ||
				entry.getValue() instanceof String ||
				entry.getValue() instanceof AbstractMap ||
				entry.getValue() instanceof AbstractList ||
				entry.getValue() instanceof Boolean))
			return;
		super.putAll(m);
	}
	public String toJSONString() {
		if(size() == 0) return JSONString.Tab("{}", false);
		Set<Entry<String, Object>> set = entrySet();
		String result = "{" + entryString(set.iterator().next());
		int index = 0;
		for(Entry<String, Object> o : set) if(index++ != 0) result += "," + entryString(o);
		return JSONString.Tab(result + "}", false);
	}
	private String entryString(Entry<String, Object> e) {
		return '\"' + e.getKey() + '\"' + ":" + StringOrDefault(e.getValue());
	}
	@SuppressWarnings("unchecked")
	private Object StringOrDefault(Object o) {
		return o instanceof String ? String.valueOf('\"') + replaceStr(o) + '\"' :
			(o instanceof AbstractList && !(o instanceof JSONArray)) ?
			new JSONArray((AbstractList<?>) o) :
			(o instanceof AbstractMap && !(o instanceof AbstractMap)) ?
			new JSONObject((AbstractMap<String, ?>) o) : o;
	}
	private Object replaceStr(Object o) {
		String oo = o.toString();
			if (count(oo, '\"') >= 2) {
				return oo.replace("\"", "\\\"");
			}else return oo.replace("\"", "\\\"");
	}
	@Override
	public String toString() {
		return toJSONString();
	}
	public static JSONObject parse(String s){
		return new JSONParser(s).parseObject();
	}
	public static JSONObject parse(Reader r){
		try {
		BufferedReader br = new BufferedReader(r);
		String result = "";
		String s;
		while((s = br.readLine()) != null) {
			result += s;
		}
		return new JSONParser(result).parseObject();
		}catch(IOException e) {return new JSONObject();}
	}
	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(super.equals(o)) return true;
		return toString().equals(o.toString());
	}
	private static int count(String input, char... selector) {
		int count = 0;
		for (char a : input.toCharArray()) {
			for (char z : selector) {
				if (z == a) count++;
			}
		}
		return count;
	}
}
