package crawl;

import data.Link;

public interface CrawlTask extends Runnable{

	void setConf(CrawlTaskConf conf);

	void dispatchResultLinks(Link[] links);

	void end();
}
