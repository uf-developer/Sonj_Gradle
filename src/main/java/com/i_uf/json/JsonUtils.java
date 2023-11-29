package com.i_uf.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class is useful for working with Json.
 * <p>This class can wrap a Json String,
 * convert a Json String to a map or list,
 * and convert a map or list to a Json String.
 *
 * @author I-uf
 * @since 2.0.0
 */
public final class JsonUtils {
    private JsonUtils() {}
    /**
     * This method adds a newline character to a Json string.
     *
     * @param text The Json string to be formatted.
     *
     * @return The formatted Json string.
     */
    public static String tab(String text) { return tab(text, false); }
    /**
     * This method adds a newline character to a Json string.
     *
     * @param text The Json string to be formatted.
     * @param file A boolean indicating whether to format as a file.
     *
     * @return The formatted Json string.
     */
    public static String tab(String text, boolean file) {
        int tab = 0;
        boolean isString = false;
        StringBuilder returnString = new StringBuilder();
        for (char letter : text.toCharArray()) {
            if (!isString && (letter == '\t' || letter == ' ') || letter == '\n' || letter == '\r') continue;
            if ((letter == '}' || letter == ']') && !isString) {
                returnString.append(file ? "\r\n" : "\n").append("\t".repeat(--tab)).append(letter);
                continue;
            }
            returnString.append(letter);
            switch (letter) {
                case '{':
                case '[':
                    if (!isString) tab++;
                case ',':
                    if (!isString) returnString.append(file ? "\r\n" : "\n").append("\t".repeat(tab));
                    break;
                case ':':
                    if (!isString) returnString.append(' ');
                    break;
                case '\"':
                    isString = !isString;
                    break;
            }
        }
        return returnString.toString();
    }
    /**
     * This method converts a {@link Map} into a Json String.
     *
     * @param map The {@link Map} to be converted.
     *
     * @return The converted Json String.
     */
    public static String mapToJsonString(Map<?, ?> map){
        return mapToJsonString(map, false);
    }
    /**
     * This method converts a {@link Map} into a Json String.
     *
     * @param map The {@link Map} to be converted.
     * @param file A boolean indicating whether to format as a file.
     *
     * @return The converted Json String.
     */
    public static String mapToJsonString(Map<?, ?> map, boolean file){
        StringBuilder result = new StringBuilder("{");
        AtomicInteger index = new AtomicInteger();
        map.forEach((key, value) -> {
            if(!(key instanceof String)) throw new IllegalArgumentException("Key isn't of type String: " + key.toString());
            result.append(elementToJsonString(key, file));
            result.append(':');
            result.append(elementToJsonString(value, file));
            if(index.incrementAndGet() < map.size()) result.append(',');
        });
        return tab(result.append("}").toString());
    }
    /**
     * This method converts a {@link List} into a Json String.
     *
     * @param list The {@link List} to be converted.
     *
     * @return The converted Json String.
     */
    public static String listToJsonString(List<?> list){ return listToJsonString(list, false); }
    /**
     * This method converts a {@link List} into a Json String.
     *
     * @param list The {@link List} to be converted.
     * @param file A boolean indicating whether to format as a file.
     *
     * @return The converted Json String.
     */
    public static String listToJsonString(List<?> list, boolean file){
        StringBuilder result = new StringBuilder("[");
        AtomicInteger index = new AtomicInteger();
        list.forEach(obj -> {
            result.append(elementToJsonString(obj, file));
            if(index.incrementAndGet() < list.size()) result.append(',');
        });
        return tab(result.append("]").toString());
    }
    /**
     * This method converts an object into a Json string.
     * <p>It handles different types of objects: Number, Boolean, null, Map, List, and others.
     * If the object is a Map or a List, it calls the appropriate method to convert it into a Json string.
     * If the object is of any other type, it converts the object into a string.
     * If the 'file' parameter is true, it also escapes special characters in the string.
     *
     * @param obj The object to be converted into a Json string.
     * @param file A boolean indicating whether to escape special characters in the string.
     *
     * @return The Json string representation of the object.
     */
    private static String elementToJsonString(Object obj, boolean file){
        if(obj instanceof Number || obj instanceof Boolean || obj == null) return String.valueOf(obj);
        else if(obj instanceof Map<?, ?> map) return mapToJsonString(map);
        else if(obj instanceof List<?> list) return listToJsonString(list);
        else return "\"" + (file ? escape_sequence(obj) : obj) + "\"";
    }
    /**
     * This method escapes special characters in a string.
     * <p>It replaces each special character with its escaped equivalent.
     *
     * @param obj The object whose string representation needs to have special characters escaped.
     *
     * @return The string with special characters escaped.
     */
    private static String escape_sequence(Object obj) {
        return obj.toString().replace("\"", "\\\"")
                .replace("\\", "\\\\")
                .replace("\t", "\\t")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\f", "\\f")
                .replace("\b", "\\b");
    }
    /**
     * This method converts a Json String into a {@link Map}.
     *
     * @param text The Json String to be converted.
     * @throws IllegalArgumentException If the Json String is not in the correct format for a {@link Map}.

     * @return The converted {@link Map}.
     */
    public static Map<String, Object> jsonStringToMap(String text){ return jsonStringToMap(text, false); }
    /**
     * This method converts a Json String into a {@link Map}.
     *
     * @param file A boolean indicating whether to format as a file.
     * @param text The Json String to be converted.
     * @throws IllegalArgumentException If the Json String is not in the correct format for a {@link Map}.

     * @return The converted {@link Map}.
     */
    public static Map<String, Object> jsonStringToMap(String text, boolean file){
        Map<String, Object> map = new HashMap<>();
        JsonType type = JsonType.EMPTY;
        boolean empty = true, escape_sequence = false;
        StringBuilder key = new StringBuilder();
        StringBuilder obj = new StringBuilder();
        int index = -1;
        for(char letter : text.toCharArray()) {
            index++;
            if (escape_sequence) {
                if(letter != '/' && letter != '\"' &&letter != 't' &&letter != 'r'
                && letter != 'n' && letter != 'f' && letter != 'b' && letter != '\\')
                throw invalidJsonException(text, index);
                else escape_sequence = false;
                (type == JsonType.KEY_OPENED ? key : obj).append(switch (letter) {
                    case 't' -> '\t';
                    case 'r' -> '\r';
                    case 'n' -> '\n';
                    case 'f' -> '\f';
                    case 'b' -> '\b';
                    default -> letter;
                });
            } else {
                if (type == JsonType.EMPTY) {
                    if (letter == '{') type = JsonType.OPENED;
                    else if(letter != ' ' && letter != '\n' && letter != '\t' && letter != '\r')
                    throw invalidJsonException(text, index);
                } else if (type == JsonType.OPENED) {
                    if (letter == '\"') type = JsonType.KEY_OPENED;
                    else if (letter == '}') if(empty) return map;
                    else throw invalidJsonException(text, index);
                    empty = (empty && type == JsonType.OPENED);
                } else if (type == JsonType.KEY_OPENED) {
                    if (letter == '\"') type = JsonType.KEY_CLOSED;
                    else if (letter == '\\' && file) escape_sequence = true;
                    else key.append(letter);
                } else if (type == JsonType.KEY_CLOSED) {
                    if (letter == ':') type = JsonType.COLON;
                } else if (type == JsonType.COLON) {
                    if (letter >= '0' && letter <= '9') {
                        type = JsonType.NUMBER;
                        obj.append(letter);
                    }
                    else if (letter == '\"') type = JsonType.STRING;
                    else if (letter == 'f') type = JsonType.FALSE;
                    else if (letter == 't') type = JsonType.TRUE;
                    else if (letter == 'n') type = JsonType.NULL;
                    else if (letter == '{') {
                        type = JsonType.OBJECT;
                        obj.append(letter);
                    }
                    else if (letter == '[') {
                        type = JsonType.LIST;
                        obj.append(letter);
                    }
                } else if (type == JsonType.NUMBER) {
                    if(letter == ',' || letter == '}') {
                        try {
                            map.put(key.toString(), stringToNumber(obj.toString().trim()));
                            if(letter == ',') type = JsonType.OPENED; else return map;
                            key.setLength(0);
                            obj.setLength(0);
                        } catch(NumberFormatException e) {
                            throw invalidJsonException(text, index);
                        }
                    } else obj.append(letter);
                } else if (type == JsonType.STRING) {
                    if (letter == '\"') {
                        type = JsonType.END;
                        map.put(key.toString(), obj.toString());
                    }
                    else obj.append(letter);
                } else if (type == JsonType.FALSE) if(letter == 'a') type = JsonType.FALSE1; else throw invalidJsonException(text, index);
                else if (type == JsonType.FALSE1) if(letter == 'l') type = JsonType.FALSE2; else throw invalidJsonException(text, index);
                else if (type == JsonType.FALSE2) if(letter == 's') type = JsonType.FALSE3; else throw invalidJsonException(text, index);
                else if (type == JsonType.FALSE3) if(letter == 'e') {
                    type = JsonType.END;
                    map.put(key.toString(), false);
                } else throw invalidJsonException(text, index);
                else if (type == JsonType.TRUE) if(letter == 'r') type = JsonType.TRUE1; else throw invalidJsonException(text, index);
                else if (type == JsonType.TRUE1) if(letter == 'u') type = JsonType.TRUE2; else throw invalidJsonException(text, index);
                else if (type == JsonType.TRUE2) if(letter == 'e') {
                    type = JsonType.END;
                    map.put(key.toString(), true);
                } else throw invalidJsonException(text, index);
                else if (type == JsonType.NULL) if(letter == 'u') type = JsonType.NULL1; else throw invalidJsonException(text, index);
                else if (type == JsonType.NULL1) if(letter == 'l') type = JsonType.NULL2; else throw invalidJsonException(text, index);
                else if (type == JsonType.NULL2) if(letter == 'l') {
                    type = JsonType.END;
                    map.put(key.toString(), null);
                } else throw invalidJsonException(text, index);
                else if (type == JsonType.OBJECT) {
                    obj.append(letter);
                    try {
                        map.put(key.toString(), jsonStringToMap(obj.toString()));
                        type = JsonType.END;
                    } catch(IllegalArgumentException ignored) { }
                } else if (type == JsonType.LIST) {
                    obj.append(letter);
                    try {
                        map.put(key.toString(), jsonStringToList(obj.toString()));
                        type = JsonType.END;
                    } catch(IllegalArgumentException ignored) { }
                } else if (type == JsonType.END) {
                    if(letter == ',' || letter == '}' || letter == '\n' || letter == '\t' || letter == '\r' || letter == ' ') {
                        if(letter == ',') type = JsonType.OPENED;
                        if(letter == '}') return map;
                        key.setLength(0);
                        obj.setLength(0);
                    } else {
                        throw invalidJsonException(text, index);
                    }
                }
            }
        }
        throw invalidJsonException(text, index);
    }
    /**
     * This method converts a Json String into a {@link List}.
     *
     * @param text The Json String to be converted.
     * @throws IllegalArgumentException If the Json String is not in the correct format for a {@link List}.
     *
     * @return The converted {@link List}.
     */
    public static List<Object> jsonStringToList(String text){ return jsonStringToList(text, false); }
    /**
     * This method converts a Json String into a {@link List}.
     *
     * @param text The Json String to be converted.
     * @param file A boolean indicating whether to format as a file.
     * @throws IllegalArgumentException If the Json String is not in the correct format for a {@link List}.
     *
     * @return The converted {@link List}.
     */
    public static List<Object> jsonStringToList(String text, boolean file){
        List<Object> list = new ArrayList<>();
        JsonType type = JsonType.EMPTY;
        boolean empty = true, escape_sequence = false;
        StringBuilder obj = new StringBuilder();
        int index = -1;
        for(char letter : text.toCharArray()) {
            index++;
            if (escape_sequence) {
                if(letter != '/' && letter != '\"' &&letter != 't' &&letter != 'r'
                && letter != 'n' && letter != 'f' && letter != 'b' && letter != '\\')
                throw invalidJsonException(text, index);
                else escape_sequence = false;
                obj.append(switch (letter) {
                    case 't' -> '\t';
                    case 'r' -> '\r';
                    case 'n' -> '\n';
                    case 'f' -> '\f';
                    case 'b' -> '\b';
                    default -> letter;
                });
            } else {
                if (letter == '\\' && file) escape_sequence = true;
                if (type == JsonType.EMPTY) {
                    if (letter == '[') type = JsonType.OPENED;
                    else if(letter != ' ' && letter != '\n' && letter != '\t' && letter != '\r')
                    throw invalidJsonException(text, index);
                } else if (type == JsonType.OPENED) {
                    if (letter >= '0' && letter <= '9') {
                        type = JsonType.NUMBER;
                        obj.append(letter);
                    } else if (letter == '\"') type = JsonType.STRING;
                    else if (letter == 'f') type = JsonType.FALSE;
                    else if (letter == 't') type = JsonType.TRUE;
                    else if (letter == 'n') type = JsonType.NULL;
                    else if (letter == '{') {
                        type = JsonType.OBJECT;
                        obj.append(letter);
                    } else if (letter == '[') {
                        type = JsonType.LIST;
                        obj.append(letter);
                    } else if (letter == ']') if(empty) return list;
                    else throw invalidJsonException(text, index);
                    empty = (empty && type == JsonType.OPENED);
                } else if (type == JsonType.NUMBER) {
                    if(letter == ',' || letter == ']') {
                        try {
                            list.add(stringToNumber(obj.toString().trim()));
                            if(letter == ',') type = JsonType.OPENED; else return list;
                            obj.setLength(0);
                        } catch(NumberFormatException e) {
                            throw invalidJsonException(text, index);
                        }
                    } else obj.append(letter);
                } else if (type == JsonType.STRING) {
                    if (letter == '\"') {
                        type = JsonType.END;
                        list.add(obj.toString());
                    }
                    else obj.append(letter);
                } else if (type == JsonType.FALSE) if(letter == 'a') type = JsonType.FALSE1; else throw invalidJsonException(text, index);
                else if (type == JsonType.FALSE1) if(letter == 'l') type = JsonType.FALSE2; else throw invalidJsonException(text, index);
                else if (type == JsonType.FALSE2) if(letter == 's') type = JsonType.FALSE3; else throw invalidJsonException(text, index);
                else if (type == JsonType.FALSE3) if(letter == 'e') {
                    type = JsonType.END;
                    list.add(false);
                } else throw invalidJsonException(text, index);
                else if (type == JsonType.TRUE) if(letter == 'r') type = JsonType.TRUE1; else throw invalidJsonException(text, index);
                else if (type == JsonType.TRUE1) if(letter == 'u') type = JsonType.TRUE2; else throw invalidJsonException(text, index);
                else if (type == JsonType.TRUE2) if(letter == 'e') {
                    type = JsonType.END;
                    list.add(true);
                } else throw invalidJsonException(text, index);
                else if (type == JsonType.NULL) if(letter == 'u') type = JsonType.NULL1; else throw invalidJsonException(text, index);
                else if (type == JsonType.NULL1) if(letter == 'l') type = JsonType.NULL2; else throw invalidJsonException(text, index);
                else if (type == JsonType.NULL2) if(letter == 'l') {
                    type = JsonType.END;
                    list.add(null);
                } else throw invalidJsonException(text, index);
                else if (type == JsonType.OBJECT) {
                    obj.append(letter);
                    try {
                        list.add(jsonStringToMap(obj.toString()));
                        type = JsonType.END;
                    } catch(IllegalArgumentException ignored) { }
                } else if (type == JsonType.LIST) {
                    obj.append(letter);
                    try {
                        list.add(jsonStringToList(obj.toString()));
                        type = JsonType.END;
                    } catch(IllegalArgumentException ignored) { }
                } else if (type == JsonType.END) {
                    if(letter == ',' || letter == ']' || letter == '\n' || letter == '\t' || letter == '\r' || letter == ' ') {
                        if(letter == ',') type = JsonType.OPENED;
                        if(letter == ']') return list;
                        obj.setLength(0);
                    } else throw invalidJsonException(text, index);
                }
            }
        }
        throw invalidJsonException(text, index);
    }
    /**
     * This method checks if a Json string is in the correct format for a {@link Map}.
     *
     * @param text The Json string to be checked.
     *
     * @return true if the Json string is in the correct format for a {@link Map}, false otherwise.
     */
    public static boolean checkMap(String text){ return checkMap(text, false) || checkMap(text, true); }
    /**
     * This method checks if a Json string is in the correct format for a {@link Map}.
     *
     * @param text The Json string to be checked.
     * @param file A boolean indicating whether to format as a file.
     *
     * @return true if the Json string is in the correct format for a {@link Map}, false otherwise.
     */
    public static boolean checkMap(String text, boolean file){
        JsonType type = JsonType.EMPTY;
        boolean empty = true, escape_sequence = false;
        StringBuilder obj = new StringBuilder();
        for(char letter : text.toCharArray()) {
            if (escape_sequence) {
                if(letter != '/' && letter != '\"' &&letter != 't' &&letter != 'r'
                && letter != 'n' && letter != 'f' && letter != 'b' && letter != '\\')
                return false;
                else escape_sequence = false;
            } else {
                if (type == JsonType.EMPTY) {
                    if (letter == '{') type = JsonType.OPENED;
                    else if(letter != ' ' && letter != '\n' && letter != '\t' && letter != '\r')
                    return false;
                } else if (type == JsonType.OPENED) {
                    if (letter == '\"') type = JsonType.KEY_OPENED;
                    else if (letter == '}') return empty;
                    empty = (empty && type == JsonType.OPENED);
                } else if (type == JsonType.KEY_OPENED) {
                    if (letter == '\"') type = JsonType.KEY_CLOSED;
                    else if (letter == '\\' && file) escape_sequence = true;
                } else if (type == JsonType.KEY_CLOSED) {
                    if (letter == ':') type = JsonType.COLON;
                } else if (type == JsonType.COLON) {
                    if (letter >= '0' && letter <= '9') {
                        type = JsonType.NUMBER;
                        obj.append(letter);
                    }
                    else if (letter == '\"') type = JsonType.STRING;
                    else if (letter == 'f') type = JsonType.FALSE;
                    else if (letter == 't') type = JsonType.TRUE;
                    else if (letter == 'n') type = JsonType.NULL;
                    else if (letter == '{') {
                        type = JsonType.OBJECT;
                        obj.append(letter);
                    }
                    else if (letter == '[') {
                        type = JsonType.LIST;
                        obj.append(letter);
                    }
                } else if (type == JsonType.NUMBER) {
                    if(letter == ',' || letter == '}') {
                        try {
                            stringToNumber(obj.toString().trim());
                            if(letter == ',') type = JsonType.OPENED; else return true;
                            obj.setLength(0);
                        } catch(NumberFormatException e) {
                            return false;
                        }
                    } else obj.append(letter);
                } else if (type == JsonType.STRING) {
                    if (letter == '\"') type = JsonType.END;
                    else obj.append(letter);
                } else if (type == JsonType.FALSE) if(letter == 'a') type = JsonType.FALSE1; else return false;
                else if (type == JsonType.FALSE1) if(letter == 'l') type = JsonType.FALSE2; else return false;
                else if (type == JsonType.FALSE2) if(letter == 's') type = JsonType.FALSE3; else return false;
                else if (type == JsonType.FALSE3) if(letter == 'e') type = JsonType.END;
                else return false;
                else if (type == JsonType.TRUE) if(letter == 'r') type = JsonType.TRUE1; else return false;
                else if (type == JsonType.TRUE1) if(letter == 'u') type = JsonType.TRUE2; else return false;
                else if (type == JsonType.TRUE2) if(letter == 'e') type = JsonType.END;
                else return false;
                else if (type == JsonType.NULL) if(letter == 'u') type = JsonType.NULL1; else return false;
                else if (type == JsonType.NULL1) if(letter == 'l') type = JsonType.NULL2; else return false;
                else if (type == JsonType.NULL2) if(letter == 'l') type = JsonType.END;
                else return false;
                else if (type == JsonType.OBJECT) {
                    obj.append(letter);
                    try {
                        jsonStringToMap(obj.toString());
                        type = JsonType.END;
                    } catch(IllegalArgumentException ignored) { }
                } else if (type == JsonType.LIST) {
                    obj.append(letter);
                    try {
                        jsonStringToList(obj.toString());
                        type = JsonType.END;
                    } catch(IllegalArgumentException ignored) { }
                } else if (type == JsonType.END) {
                    if(letter == ',' || letter == '}' || letter == '\n' || letter == '\t' || letter == '\r' || letter == ' ') {
                        if(letter == ',') type = JsonType.OPENED;
                        if(letter == '}') return true;
                        obj.setLength(0);
                    } else {
                        return false;
                    }
                }
            }
        }
        return false;
    }
    /**
     * This method checks if a Json string is in the correct format for a {@link List}.
     *
     * @param text The Json string to be checked.
     *
     * @return true if the Json string is in the correct format for a {@link List}, false otherwise.
     */
    public static boolean checkList(String text){ return checkList(text, false) || checkList(text, true); }
    /**
     * This method checks if a Json string is in the correct format for a {@link List}.
     *
     * @param text The Json string to be checked.
     * @param file A boolean indicating whether to format as a file.
     *
     * @return true if the Json string is in the correct format for a {@link List}, false otherwise.
     */
    public static boolean checkList(String text, boolean file){
        JsonType type = JsonType.EMPTY;
        boolean empty = true, escape_sequence = false;
        StringBuilder obj = new StringBuilder();
        for(char letter : text.toCharArray()) {
            if (escape_sequence) {
                if(letter != '/' && letter != '\"' &&letter != 't' && letter != 'r'
                && letter != 'n' && letter != 'f' && letter != 'b' && letter != '\\')
                return false;
                else escape_sequence = false;
                obj.append(switch (letter) {
                    case 't' -> '\t';
                    case 'r' -> '\r';
                    case 'n' -> '\n';
                    case 'f' -> '\f';
                    case 'b' -> '\b';
                    default -> letter;
                });
            } else {
                if (letter == '\\' && file) escape_sequence = true;
                if (type == JsonType.EMPTY) {
                    if (letter == '[') type = JsonType.OPENED;
                    else if(letter != ' ' && letter != '\n' && letter != '\t' && letter != '\r')
                    return false;
                } else if (type == JsonType.OPENED) {
                    if (letter >= '0' && letter <= '9') {
                        type = JsonType.NUMBER;
                        obj.append(letter);
                    } else if (letter == '\"') type = JsonType.STRING;
                    else if (letter == 'f') type = JsonType.FALSE;
                    else if (letter == 't') type = JsonType.TRUE;
                    else if (letter == 'n') type = JsonType.NULL;
                    else if (letter == '{') {
                        type = JsonType.OBJECT;
                        obj.append(letter);
                    } else if (letter == '[') {
                        type = JsonType.LIST;
                        obj.append(letter);
                    } else if (letter == ']') return empty;
                    empty = (empty && type == JsonType.OPENED);
                } else if (type == JsonType.NUMBER) {
                    if(letter == ',' || letter == ']') {
                        try {
                            stringToNumber(obj.toString().trim());
                            if(letter == ',') type = JsonType.OPENED; else return true;
                            obj.setLength(0);
                        } catch(NumberFormatException e) {
                            return false;
                        }
                    } else obj.append(letter);
                } else if (type == JsonType.STRING) {
                    if (letter == '\"') type = JsonType.END;
                    else obj.append(letter);
                } else if (type == JsonType.FALSE) if(letter == 'a') type = JsonType.FALSE1; else return false;
                else if (type == JsonType.FALSE1) if(letter == 'l') type = JsonType.FALSE2; else return false;
                else if (type == JsonType.FALSE2) if(letter == 's') type = JsonType.FALSE3; else return false;
                else if (type == JsonType.FALSE3) if(letter == 'e') type = JsonType.END;
                else return false;
                else if (type == JsonType.TRUE) if(letter == 'r') type = JsonType.TRUE1; else return false;
                else if (type == JsonType.TRUE1) if(letter == 'u') type = JsonType.TRUE2; else return false;
                else if (type == JsonType.TRUE2) if(letter == 'e') type = JsonType.END;
                else return false;
                else if (type == JsonType.NULL) if(letter == 'u') type = JsonType.NULL1; else return false;
                else if (type == JsonType.NULL1) if(letter == 'l') type = JsonType.NULL2; else return false;
                else if (type == JsonType.NULL2) if(letter == 'l') type = JsonType.END;
                else return false;
                else if (type == JsonType.OBJECT) {
                    obj.append(letter);
                    try {
                        jsonStringToMap(obj.toString());
                        type = JsonType.END;
                    } catch(IllegalArgumentException ignored) { }
                } else if (type == JsonType.LIST) {
                    obj.append(letter);
                    try {
                        jsonStringToList(obj.toString());
                        type = JsonType.END;
                    } catch(IllegalArgumentException ignored) { }
                } else if (type == JsonType.END) {
                    if(letter == ',' || letter == ']' || letter == '\n' || letter == '\t' || letter == '\r' || letter == ' ') {
                        if(letter == ',') type = JsonType.OPENED;
                        if(letter == ']') return true;
                        obj.setLength(0);
                    } else return false;
                }
            }
        }
        return false;
    }
    /**
     * This method attempts to convert a {@link String} into a {@link Number}.
     * <p>It tries to parse {@link String} as an Integer, Long, Float, and Double in that order.
     *
     * @param text The {@link String} to be converted into a {@link Number}.
     * @throws NumberFormatException If the {@link String} cannot be parsed into an Integer, Long, Float, or Double.

     * @return The {@link Number} object parsed from the String.
     */
    private static Number stringToNumber(String text){
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            try {
                return Long.parseLong(text);
            } catch (NumberFormatException ex) {
                try {
                    return Float.parseFloat(text);
                } catch (NumberFormatException exc) {
                    return Double.parseDouble(text);
                }
            }
        }
    }
    /**
     * This method generates an {@link IllegalArgumentException} for the {@link JsonUtils#jsonStringToMap} and {@link JsonUtils#jsonStringToList} methods.
     * <p>It is called when the Json string is not in the correct format for a {@link Map} or a {@link List}.
     * The exception message includes the location of the invalid syntax in the Json string.
     *
     * @param text The Json string with invalid syntax.
     * @param index The index in the Json string where the invalid syntax starts.
     *
     * @return An IllegalArgumentException with a custom error message.
     */
    private static IllegalArgumentException invalidJsonException(String text, int index) { return new IllegalArgumentException("Invalid Json Syntax at:\n" + tab(text.substring(0, index+1)) + "<-" + tab(text.substring(index+1))); }

    /**
     * This class is an enumeration that represents each step in the Json parsing process.
     *
     * @see JsonUtils
     *
     * @author I-uf
     * @since 2.0.0
     */
    private enum JsonType {
        EMPTY, OPENED,
        KEY_OPENED, KEY_CLOSED, COLON,
        NUMBER, STRING, OBJECT, LIST,
        FALSE, FALSE1, FALSE2, FALSE3,
        TRUE, TRUE1, TRUE2,
        NULL, NULL1, NULL2,
        END
    }
}