/**
 * Legend of Rogue
 * An AHS Gaming Production
 * (c) 2013 Jami Couch
 * fbcouch 'at' gmail 'dot' com
 * Licensed under Apache 2.0
 * See www.ahsgaming.com for more info
 * 
 * LibGDX
 * (c) 2011 see LibGDX authors file
 * Licensed under Apache 2.0
 * 
 * Pixelated Fonts by Kenney, Inc. Licensed as CC-SA.
 * See http://kenney.nl for more info.
 * 
 * All other art assets (c) 2013 Jami Couch, licensed CC-BY-SA
 */
package com.ahsgaming.valleyofbones;

import java.util.Random;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * @author jami
 *
 */
public class Utils {
	public static final String LOG = "Utils";
	
	public static String toJsonProperty(String key, Object value) {
		String valueStr = value.toString();
		if (value instanceof ObjectMap) {
			valueStr = "{";
			ObjectMap<String, Object> om = (ObjectMap<String, Object>)value;
			for (String k: om.keys()) {
				valueStr += toJsonProperty(k, om.get(k));
			}
			valueStr += "}";
		}
		else if (value instanceof Array) {
			valueStr = "[";
			Array<Object> oa = (Array<Object>)value;
			for (Object o: oa) {
				valueStr += toJsonProperty(null, o);
			}
			valueStr += "]";
		}
		if (value instanceof String)
			return (key != null ? "\"" + key + "\":" : "" ) + "\""+ valueStr + "\",";
		return (key != null ? "\"" + key + "\":" : "" ) + valueStr + ",";
	}

    public static Array<Object> jsonAsArray(JsonValue json) {
        Array<Object> returnVal = new Array<Object>();
        for (JsonValue v: json) {
            returnVal.add((v.isValue() ? v.asString() : (v.isObject() ? v : jsonAsArray(v))));
        }
        return returnVal;
    }
	
	/**
	 * Random id code adapted from 
	 * http://stackoverflow.com/questions/41107/how-to-generate-a-random-alpha-numeric-string-in-java
	 */
	
	private static final char[] symbols = new char[36];
	private static final Random random = new Random();

	static {
		for (int idx = 0; idx < 10; ++idx)
			symbols[idx] = (char) ('0' + idx);
		for (int idx = 10; idx < 36; ++idx)
			symbols[idx] = (char) ('a' + idx - 10);
	}
	
	public static String getRandomId() {
		return getRandomId(12);
	}
	
	public static String getRandomId(int length)
	{
		if (length < 1)
			length = 12;
		
		char[] buf = new char[length];
		
		for (int idx = 0; idx < length; ++idx) 
			buf[idx] = symbols[random.nextInt(symbols.length)];
		return new String(buf);
	}

}
