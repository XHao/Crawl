package crawl;

import data.Link;

public class CrawlResult {

	private Link[] links;
	private String protocol;
	private boolean duplicate;
	
	public CrawlResult(Link[] links, String protocol, boolean duplicate) {
		this.links = links;
		this.protocol = protocol;
		this.duplicate = duplicate;
	}
	
	public Link[] getLinks() {
		return links;
	}
	
	public String getProtocol() {
		return protocol;
	}
	
	public boolean needResolveDuplicate() {
		return duplicate;
	}
}
