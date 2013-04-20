/**
 * Copyright 2012 Jami Couch
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * This project uses:
 * 
 * LibGDX
 * Copyright 2011 see LibGDX AUTHORS file
 * Licensed under Apache License, Version 2.0 (see above).
 * 
 */
package com.ahsgaming.valleyofbones.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;


/**
 * @author jami
 *
 */
public class JsonUnitModelTool {
	Array<String> headerLines, footerLines;
	Array<ProtoClass> jsonUnitModels;
	
	public Array<ProtoClass> loadUnitModels(String file) {
		headerLines = new Array<String>();
		footerLines = new Array<String>();
		
		jsonUnitModels = null;
		
		boolean inPrototypes = false;
		
		try (BufferedReader reader = Files.newBufferedReader(Paths.get(file), Charset.forName("US-ASCII"))) {
			
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (!inPrototypes) { // read until we enter the 'prototypes class'
					if (line.contains("class") && line.contains("Prototypes")) {
						inPrototypes = true;
						headerLines.add(line);
						jsonUnitModels = new Array<ProtoClass>();
					} else {
						if (jsonUnitModels == null) {
							headerLines.add(line);
						} else {
							footerLines.add(line);
						}
					}
				} else {
					// in prototypes, read until we enter a class!
					if (line.contains("class") && line.contains("public") && line.contains("static")) {
						jsonUnitModels.add(loadModel(line, reader));
					} else {
						if (jsonUnitModels.size == 0) {
							headerLines.add(line);
						}
					}
				}
			}
			
		} catch (IOException ex) {
			System.err.format("IOException: %s%n", ex);
		}
		
		return jsonUnitModels;
	}
	
	public ProtoClass loadModel(String curLine, BufferedReader reader) throws IOException {
		int bracketsOpen = 0;
		
		ProtoClass retClass = new ProtoClass();
		// get the class name
		retClass.className = getClassName(curLine);
		// get the superclass
		retClass.superClass = getSuperClass(curLine);
		
		while (!curLine.contains("{") && (curLine = reader.readLine()) != null) {
			
		}
		
		// get the type
		bracketsOpen += 1;
		retClass.type = getClassType(curLine = reader.readLine());
		System.out.println("class " + retClass.className + " extends " + retClass.superClass);
		// get the attributes!
		
		while ((curLine = reader.readLine()) != null && bracketsOpen > 0) {
			if (curLine.contains("{")) {
				bracketsOpen += 1;
			}
			if (curLine.contains("}")) {
				bracketsOpen -= 1;
			}
			
			if (bracketsOpen == 1) {
				ProtoDataTypes type = null;
				Array<String> names = null;
				if (curLine.contains(ProtoDataTypes.STRING.toString())) {
					type = ProtoDataTypes.STRING;
					names = getVarNames(curLine.substring(curLine.indexOf(type.toString()) + type.toString().length() + 1));
				} else if (curLine.contains(ProtoDataTypes.INT.toString())) {
					type = ProtoDataTypes.INT;
					names = getVarNames(curLine.substring(curLine.indexOf(type.toString()) + type.toString().length() + 1));
				} else if (curLine.contains(ProtoDataTypes.FLOAT.toString())) {
					type = ProtoDataTypes.FLOAT;
					names = getVarNames(curLine.substring(curLine.indexOf(type.toString()) + type.toString().length() + 1));
				} else if (curLine.contains(ProtoDataTypes.STRINGS.toString())) {
					type = ProtoDataTypes.STRINGS;
					names = getVarNames(curLine.substring(curLine.indexOf(type.toString()) + type.toString().length() + 1));
				} else if (curLine.contains("Array")) {
					//type = ProtoDataTypes.ARRAY;
					//names = getVarNames(curLine.substring(curLine.indexOf(type.toString()) + type.toString().length()));
				}
				
				if (type != null && names != null) {
					for (String k: names) {
						System.out.println("\t" + k);
						retClass.attributes.put(k, type);
					}
				}
			}
		}
		
		return retClass;
	}
	
	public String getClassName(String line) {
		int start = 0, end = 0;
		int i = 0;
		if ((end = line.indexOf("{")) == -1) {
			end = line.length();
		}
		
		if ((i = line.indexOf("extends")) < end && i > -1) end = i;
		
		start = line.indexOf("class") + 6;
		
		return line.substring(start, end).trim();
	}
	
	public String getSuperClass(String line) {
		int start = 0, end = 0;
		
		if ((start = line.indexOf("extends")) == -1) {
			return "";
		}
		
		start += 8;
		
		if ((end = line.indexOf("{")) == -1) {
			end = line.length();
		}
		
		return line.substring(start, end).trim();
	}
	
	public String getClassType(String line) {
		if (!line.contains("TYPE")) return "";
		int start = line.indexOf("\"");
		
		int end = line.indexOf("\"", start + 1);
		
		return line.substring(start + 1, end);
	}
	
	public Array<String> getVarNames(String text) {
		Array<String> retVal = new Array<String>();
		int split = 0;
		while (split < text.length()) {
			int end = text.indexOf("=");
			if (end == -1) { 
				end = text.indexOf(",");
			}
			
			if (end == -1) {
				end = text.indexOf(";");
			}
			
			if (end == -1) return retVal;
			
			retVal.add(text.substring(0, end).trim());
			
			
			split = text.indexOf(",");
			text = text.substring(split + 1, text.length());
			if (split == -1) {
				split = text.length();
			}
		}
		return retVal;
	}
	
	public static void main(String[] args) {
		JsonUnitModelTool tool = new JsonUnitModelTool();
		if (args.length == 1) {
			tool.loadUnitModels(args[0]);
		} else {
			tool.loadUnitModels("/home/jami/workspace/space-tactics/src/com/ahsgaming/spacetactics/units/Prototypes.java");
		}
		
		
	}
	
	public static class ProtoClass {
		String className = "";
		String superClass = "";
		String type = "";
		ObjectMap<String, ProtoDataTypes> attributes = new ObjectMap<String, ProtoDataTypes>();
		Array<ProtoClass> subClass = new Array<ProtoClass>();
	}
	
	public static enum ProtoDataTypes {
		STRING("String"), INT("int"), FLOAT("float"),
		STRINGS("Array<String>");
		
		
		private ProtoDataTypes(final String text) {
			this.text = text;
		}
		private final String text;
		
		@Override
		public String toString() {
			return text;
		}
		
	}
}
