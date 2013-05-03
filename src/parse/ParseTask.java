package parse;

import crawl.CrawlTask;
import data.Link;

public interface ParseTask {

	void setCrawlNode(CrawlTask node);
	void parse(Link[] links);
}
