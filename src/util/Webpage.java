package util;

public class Webpage {
	
	private String content;
	private String url;
		
	public Webpage(String url, String content)
	{
		this.url = url;
		this.content = content;
		//this.lastFetchedTime = lastFetchedTime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public String getUrl() {
		return url;
	}
}
