package com.example.util;

import org.springframework.stereotype.Component;

@Component
public class IgnoreFilterFileExtensions {

	private static final String[] ignoreFileExt = {"css","js","svg","ttf","ico"};
	private static final String EMPTY_URL = "EMPTY_URL";
	
	public IgnoreFilterFileExtensions() {
		
	}
	
	public static boolean ignoredFileExtensions(String extension) {
		
		for(String s : ignoreFileExt)
			if (s.equals(extension))
				return true;
		
		return false;
	}
	
	public static String getExtensionFromURI(String URI) {
		
		if(URI.lastIndexOf(".") == -1 || URI.lastIndexOf(".") == URI.length())
			return EMPTY_URL;
		
		return URI.substring(URI.lastIndexOf(".")+1,URI.length());
	}
}
