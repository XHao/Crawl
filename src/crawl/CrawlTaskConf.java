package crawl;

import index.IndexConf;
import parse.ParseConf;

import data.Link;

public class CrawlTaskConf {
	
	private String protocol;
	private Link[] links;
	private Crawl crawl;
	private ParseConf parseConf;
	private IndexConf indexConf;
	
	public String getProtocol() {
		return protocol;
	}
	
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	
	public Link[] getLinks() {
		return links;
	}
	
	public void setLinks(Link[] links) {
		this.links = links;
	}

	public Crawl getCrawl() {
		return crawl;
	}

	public void setCrawl(Crawl crawl) {
		this.crawl = crawl;
	}

	public ParseConf getParseConf() {
		return parseConf;
	}

	public void setParseConf(ParseConf parseConf) {
		this.parseConf = parseConf;
	}

	public IndexConf getIndexConf() {
		return indexConf;
	}

	public void setIndexConf(IndexConf indexConf) {
		this.indexConf = indexConf;
	}
}
