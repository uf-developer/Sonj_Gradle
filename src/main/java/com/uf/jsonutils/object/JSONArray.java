package com.uf.jsonutils.object;

import com.uf.jsonutils.JSONString;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;

@SuppressWarnings("serial")
public class JSONArray extends ArrayList<Object>{
	public JSONArray() {
		super();
	}
	public JSONArray(Collection<? extends Object> c) {
		super(c);
	}
	@Override
	public void add(int index, Object element) {
		if(element instanceof Number ||
			element instanceof String ||
			element instanceof AbstractMap ||
			element instanceof AbstractList ||
			element instanceof Boolean)
		super.add(index, element);
	}
	@Override
	public boolean add(Object element) {
		add(size(), element);
		return false;
	}
	public String toJSONString() {
		if(size() == 0) return JSONString.Tab("[]", false);
		String result = "[" + StringOrDefault(get(0));
		int index = 0;
		for(Object o : this) if(index++ != 0) result += "," + StringOrDefault(o);
		return JSONString.Tab(result + "]", false);
	}
	private Object StringOrDefault(Object o) {
		return o instanceof String ? String.valueOf('\"') + replaceStr(o) + '\"' : o;
	}
	private Object replaceStr(Object o) {
		String oo = o.toString();
			if (count(oo, '\"') >= 2) {
				oo.replaceAll("\"", "\\\"");
			}else oo.replace("\"", "\\\"");
		
		return oo;
	}
	@Override
	public String toString() {
		return toJSONString();
	}
	public static JSONArray parse(String s) throws IOException{
		return parse(new StringReader(s));
	}
	public static JSONArray parse(Reader r) throws IOException{
		BufferedReader br = new BufferedReader(r);
		String result = "";
		String s;
		while((s = br.readLine()) != null) result += s;
		return new JSONParser(result).parseArray();
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
