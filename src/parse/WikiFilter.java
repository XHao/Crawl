package parse;

import java.util.regex.Pattern;

public class WikiFilter implements UrlFilter {
	
	private static Pattern p = Pattern.compile("(http:|https:)(?!.*((#))).*");
	/**
	 * check this url is legal
	 */
	public boolean isValidate(String url) {
		boolean flag = p.matcher(url).matches();
		return flag;
	}
}
