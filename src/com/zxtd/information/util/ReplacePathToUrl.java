package com.zxtd.information.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ReplacePathToUrl {
	private String mContent;
	public ReplacePathToUrl(String content){
		this.mContent = content;
	}
	
	public String replace(PathToUrl pathToUrl){
		int diffNum = 0;
		StringBuffer buffer = new StringBuffer(mContent);
		Pattern pattern = Pattern.compile("<\\s*img\\s+src\\s*=\\s*\"([\\s\\S]*?)\"\\s*\\/?>");
		Matcher matcher = pattern.matcher(mContent);
		while (matcher.find()) {
			String path = matcher.group(1);
			if(pathToUrl != null){
				String url = pathToUrl.getUrl(path);
				if(Utils.isEmpty(url)){
					return "";
				}
				buffer.replace(matcher.start(1) + diffNum, matcher.end(1) + diffNum, url);
				diffNum = url.length() - path.length() + diffNum;
			}
		}
		return buffer.toString();
	}
	public interface PathToUrl{
		String getUrl(String path);
	}
}
