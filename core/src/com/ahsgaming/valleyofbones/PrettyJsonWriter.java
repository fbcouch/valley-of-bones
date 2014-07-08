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

import java.io.IOException;
import java.io.Writer;

import com.badlogic.gdx.Gdx;

/**
 * @author jami
 *
 */
public class PrettyJsonWriter {
	public static final String LOG = "PrettyJsonWriter";
	Writer writer;
	
	/**
	 * Pass the writer to use
	 */
	public PrettyJsonWriter(Writer writer) {
		this.writer = writer;
	}

	/**
	 * writes a string in json format to the writer
	 * @param json
	 * @return
	 * @throws IOException
	 */
	public String write(String json) throws IOException {
		String prettyJson = prettify(json);
		writer.write(prettyJson);
		return prettyJson;
	}
	
	/**
	 * closes the writer
	 * @throws IOException
	 */
	public void close() throws IOException {
		writer.close();
	}
	
	private String prettify(String json) {
		int bracesOpen = 0;
		boolean quoteOpen = false, braceOpened = false;
		String prettyJson = "";
		char insertPre, insertPost;
		
		int i = 0;
		while (i < json.length()) {
			insertPre = Character.UNASSIGNED;
			insertPost = Character.UNASSIGNED;
			
			char c = json.charAt(i);
			
			braceOpened = false;
			
			if (c == '\"') {
				quoteOpen = !quoteOpen;
			} 
			
			if (quoteOpen) {
				// no formatting until close quote
			} else if (c == '{' || c == '[') {
				bracesOpen += 1;
				
				if (!(prettyJson.endsWith("\n") || prettyJson.length() == 0))
					insertPre = '\n';
				
				insertPost = '\n';
				
				braceOpened = true;
			} else if (c == '}' || c == ']') {
				bracesOpen -= 1;
				
				if (!prettyJson.endsWith("\n"))
					insertPre = '\n';
			
			} else if (c == ',') {
				insertPost = '\n';
			} else if (c == ':') {
				if (i + 1 < json.length() && json.charAt(i+1) != ' ' && json.charAt(i+1) != '\n') {
					insertPost = ' ';
				}
			}
			
			if (insertPre != Character.UNASSIGNED) prettyJson += insertPre;
			if (prettyJson.endsWith("\n")) prettyJson += getSpacer(bracesOpen - (braceOpened? 1 : 0)); 
			prettyJson += c;
			if (insertPost != Character.UNASSIGNED) prettyJson += insertPost;
			
			i += 1;
		}
		
		return prettyJson;
	}
	
	private String getSpacer(int bracesOpen) {
		return (bracesOpen > 0 ? "  " + getSpacer(bracesOpen - 1) : "");
	}
}
