package org.hackday.stickman.upload.youtube.model;

import com.google.api.client.util.Key;

public class MediaCategory 
{
	@Key("@scheme")
	public String scheme = "http://gdata.youtube.com/schemas/2007/categories.cat";
	                          
	@Key("text()")
	public String Cat;
}
