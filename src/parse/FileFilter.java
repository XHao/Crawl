package parse;

import java.util.regex.Pattern;

public class FileFilter implements UrlFilter {

	/*
	 * find the index file types
	 
	private static Pattern p = Pattern.compile(".*(.lnk|.exe|.bin|.zip|.wrf|.msi|.cmd|.chm|" +
			".jpg|.bmp|.gif|.jpeg|.png|" +
			".mpp|.jude|.rpt|.db|" +
			".sln|.suo|.pdb|.dll|.resx|.cache|.resources|.csproj|.settings|.user)", Pattern.CASE_INSENSITIVE);
	 */
	private static Pattern p = Pattern.compile(".*\\.(doc|docx|txt|ppt|pptx|xls|xlsx|msg|" +
			"xml|" +
			"cpp|cs|java|h|c|" +
			"pdf|wrf" +
			")", Pattern.CASE_INSENSITIVE);
	
	public boolean isValidate(String url) {
		boolean flag = p.matcher(url).matches();
		return flag;
	}
}
