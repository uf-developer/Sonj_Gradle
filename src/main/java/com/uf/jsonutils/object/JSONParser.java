package com.uf.jsonutils.object;

public final class JSONParser {
	private final String s;
	public JSONParser(String s) {
		this.s = s;
	}
	public JSONObject parseObject(){
		JSONObject object = new JSONObject();
		int lock = 0, state = -1, phase = 0, type = 0 /*num, str, false, true*/, numtype = 0 /*int, long, float, double*/;
		long low = 0, float_num = 0;
		boolean isString = false, isNegative = false;
		String name = "";
		String varstr = "";
		Number varnum = 0;
		for(char c : s.toCharArray()) {
			if(lock >= ++state) continue;
			if(c == ST) {
				if(!(s.charAt(state-1) == '\\' && s.charAt(state-2) != '\\')) {
					isString = !isString;
					if(phase <= 1) phase++;
					if(phase == 7) phase++;
					if(phase == 3) {
						phase=7;
						type = 2;
					}
					continue;
				}

			}
			if(!isString) {
				if(phase == 3) {
					if(c == OO) {
						int OC = findnextcount0(state, JSONParser.OC, JSONParser.OO);
						object.put(name, new JSONParser(s.substring(state, OC + 1)).parseObject());
						lock = OC;
						phase = 8;
						type = 0;
					}
					if(c == LO) {
						int LC = findnextcount0(state, JSONParser.LC, JSONParser.LO);
						object.put(name, new JSONParser(s.substring(state, LC + 1)).parseArray());
						lock = LC;
						phase = 8;
						type = 0;
					}
					if(c == '1' || c == '2' || c == '3' || c == '4' ||
						c == '5' || c == '6' || c == '7' || c == '8' ||
						c == '9' || c == '0' || c == '.' || c == '-') {
						type = 1;
						if(c == '.') {
							low = 10;
							numtype = Math.max(2, numtype);
						}else if(c == '-') {
							isNegative = true;
						}
						else{
							if(low == 0) {
								if(numtype == 0) if(varnum.intValue() > 214748364 || (varnum.intValue() == 214748364 && c-48 > (isNegative ? 8 : 7))) numtype++;
								else if(numtype == 1) if(varnum.longValue() > -9223372036854775807L || (varnum.longValue() == 922337203685477580L && c-48 > (isNegative ? 8 : 7))) numtype++;
								else if(numtype == 2) if(varnum.floatValue() > 34028235677973366163753939545814256844f || (varnum.floatValue() == 34028235677973366163753939545814256844f && c-48 > (isNegative ? 8 : 7))) numtype++;
								varnum = varnum.doubleValue() * 10 + (c-48);
							}else {
								if(numtype == 2) if(low > 100000000 || (low == 100000000 && float_num >= 4778888 && c-48 == 9)) numtype++;
								if(numtype == 2) float_num = float_num * 10 + c-48;
								varnum = varnum.doubleValue() + (double)(c-48) / low;
								low *= 10;
							}
						}
					}
					if(c == 'f') {
						phase = 8;
						type = 3;
					}
					if(c == 't') {
						phase = 8;
						type = 4;
					}
				}else if(phase == 2 && c == CL) phase++;
				if(c == CM || s.length() == state+1) {
					phase = 0;
					if(type != 0) {
						switch (type) {
							case 1:
								if(numtype == 0) object.put(name, isNegative ? -varnum.intValue() : varnum.intValue());
								else if(numtype == 1) object.put(name, isNegative ? -varnum.longValue() : varnum.longValue());
								else if(numtype == 2) object.put(name, isNegative ? -varnum.floatValue() : varnum.floatValue());
								else object.put(name, isNegative ? -varnum.doubleValue() : varnum.doubleValue());
								break;
							case 2:
								object.put(name, varstr);
								break;
							case 3:
								object.put(name, false);
								break;
							case 4:
								object.put(name, true);
								break;
						}
						type = 0;
					}
					name = "";
					varstr = "";
					varnum = 0;
					numtype = 0;
					low = 0;
					isNegative = false;
				}
			}else {
				if(phase == 1) name += c;
				if(phase == 7) {
					varstr += c;
				}
			}


		}
		return object;
	}
	public JSONArray parseArray(){
		JSONArray array = new JSONArray();
		int lock = 0, state = -1, type = 0 /*num, str, false, true*/, numtype = 0 /*int, long, float, double*/;
		long low = 0, float_num = 0;
		boolean isString = false, isNegative = false;
		String varstr = "";
		Number varnum = 0;
		for(char c : s.toCharArray()) {
			if(lock >= ++state) continue;
			if(c == ST) {
				if(!(s.charAt(state-1) == '\\' && s.charAt(state-2) != '\\')) {
					isString = !isString;
					type = 2;
					continue;
				}

			}
			if(!isString) {
				if(c == OO) {
					int OC = findnextcount0(state, JSONParser.OC, JSONParser.OO);
					array.add(new JSONParser(s.substring(state, OC + 1)).parseObject());
					lock = OC;
					type = 0;
				}
				if(c == LO) {
					int LC = findnextcount0(state, JSONParser.LC, JSONParser.LO);
					array.add(new JSONParser(s.substring(state, LC + 1)).parseArray());
					lock = LC;
					type = 0;
				}
				if(c == '1' || c == '2' || c == '3' || c == '4' ||
						c == '5' || c == '6' || c == '7' || c == '8' ||
						c == '9' || c == '0' || c == '.' || c == '-') {
					type = 1;
					if(c == '.') {
						low = 10;
						numtype = Math.max(2, numtype);
					}else if(c == '-') {
						isNegative = true;
					}
					else{
						if(low == 0) {
							if(numtype == 0) if(varnum.intValue() > 214748364 || (varnum.intValue() == 214748364 && c-48 > (isNegative ? 8 : 7))) numtype++;
							else if(numtype == 1) if(varnum.longValue() > -9223372036854775807L || (varnum.longValue() == 922337203685477580L && c-48 > (isNegative ? 8 : 7))) numtype++;
							else if(numtype == 2) if(varnum.floatValue() > 34028235677973366163753939545814256844f || (varnum.floatValue() == 34028235677973366163753939545814256844f && c-48 > (isNegative ? 8 : 7))) numtype++;
							varnum = varnum.doubleValue() * 10 + (c-48);
						}else {
							if(numtype == 2) if(low > 100000000 || (low == 100000000 && float_num >= 4778888 && c-48 == 9)) numtype++;
							if(numtype == 2) float_num = float_num * 10 + c-48;
							varnum = varnum.doubleValue() + (double)(c-48) / low;
							low *= 10;
						}
					}
				}
				if(c == 'f') {
					type = 3;
				}
				if(c == 't') {
					type = 4;
				}
			}
			if(c == CM || s.length() == state+1) {
				if(type != 0) {
					switch (type) {
						case 1:
							if(numtype == 0) array.add(isNegative ? -varnum.intValue() : varnum.intValue());
							else if(numtype == 1) array.add(isNegative ? -varnum.longValue() : varnum.longValue());
							else if(numtype == 2) array.add(isNegative ? -varnum.floatValue() : varnum.floatValue());
							else array.add(isNegative ? -varnum.doubleValue() : varnum.doubleValue());
							break;
							case 2:
								array.add(varstr);
								break;
							case 3:
								array.add(false);
								break;
							case 4:
								array.add(true);
								break;
					}
					type = 0;
				}
				varstr = "";
				varnum = 0;
				numtype = 0;
				low = 0;
				isNegative = false;
			} else varstr += c;
		}
		return array;
	}
	private int findnextcount0(int min, char finder, char... mask) {
		int index = -1, count = 0;
		for(char c : s.toCharArray()) {
			if(++index > min) {
				for(char cc : mask) if(c == cc) count++;
				if(c == finder) {
					if(count == 0)
					return index;
					else count--;
				}
			}
		}
		return -3;
	}
	private static final char
	OO='{', LO='[', LC=']', OC='}', CL = ':', CM = ',', ST = '\"';
}