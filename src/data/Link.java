package data;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Link {

	private String url;
	@JsonIgnore
	private String protocol;
	
	/* now disable fetch time. crawl control the update time
	 * private Date lastFetchedTime;
	 * */
	/**
	 * for Json
	 */
	public Link() {}
	
	/**
	 * @param url with protocol
	 */
	public Link(String url)
	{
		this.setUrl(url);
	}
	
	private void guessProtocol() {
		if(url.startsWith(LinkConstant.PROTOCOL_FILE))
		{
			protocol = LinkConstant.PROTOCOL_FILE;
		}
		else if(url.startsWith(LinkConstant.PROTOCOL_HTTP))
		{
			protocol = LinkConstant.PROTOCOL_HTTP;
		}
		else
		{
			protocol = LinkConstant.PROTOCOL_NONE;
		}
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
		guessProtocol();
	}
	
	@JsonIgnore
	public String getProtocol() {
		return protocol;
	}
	
	/**
	 * @return path without protocol 
	 */
	@JsonIgnore
	public String getPath() {
		if(protocol.equals(LinkConstant.PROTOCOL_FILE))
			return url.substring(5);
		return url;
	}
	
	@JsonIgnore
	public String getTitle() {		
		String title = url.substring(url.lastIndexOf("/") + 1);
		int endIndex = title.lastIndexOf(".");
		if(endIndex > 0)
		{
			title.substring(0, endIndex);
		}
		return title;
	}
	
	@JsonIgnore
	public String getLocation() {
		if(url.lastIndexOf("/")>0)
		{
			return url.substring(0, url.lastIndexOf("/"));
		}
		return null;
	}

	@Override
 	public boolean equals(Object o)
	{
		if(this == o)
		{
			return true;
		}
		
		if(!(o instanceof Link))
		{
			return false;
		}
		else
		{
			Link other = (Link)o;
			return this.url.equals(other.url);
		}
	}
	
	@Override
	public int hashCode()
	{
		return url.hashCode();
	}
	
	@Override
	public String toString()
	{
		return url + "\n";		
	}	
}
