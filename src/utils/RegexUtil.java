package utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {
	public static String getSubStringByRegex(String REGEX,String input){				
		Pattern p = Pattern.compile(REGEX);
		Matcher m = p.matcher(input); // get a matcher object
		if (m.find())
			return input.substring(m.start(),m.end());
		return null;	
	}
	
	public static String getSubStringByRegex(String REGEX,String input,int groupIndex){				
		Pattern p = Pattern.compile(REGEX);
		Matcher m = p.matcher(input); // get a matcher object
		if (m.find())
			return input.substring(m.start(groupIndex),m.end(groupIndex));
		return null;	
	}
}
