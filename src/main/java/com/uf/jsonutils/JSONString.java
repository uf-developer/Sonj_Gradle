package com.uf.jsonutils;

public class JSONString {
	public static String Tab(String target, boolean file) {
		int tab = 0;
		boolean isString = false;
		String returnString = "";
		int j = -1;
		for(char c : target.toCharArray()) {
			j++;
			if(!isString && (c == '\t' || c == ' ') || c == '\n' || c == '\r') continue;
			if(c == '}' || c == ']') {
				if(!isString) returnString+=(file?"\r\n":"\n") + "\t".repeat(--tab) + c;
				if(!isString) continue;
			}
			returnString+=c;
			switch (c) {
				case '{':
				case '[':
					if(!isString) tab++;
				case ',':
					if(!isString) returnString+=(file?"\r\n":"\n") + "\t".repeat(tab);
					break;
				case ':':
					if(!isString) returnString+=" ";
					break;
				case '\"':
					if(!(target.charAt(j-1) == '\\' && target.charAt(j-2) != '\\')) isString = !isString;
					break;
			}
		}
		return returnString;
	}
}
