package crawl;

public interface Crawl {

	/**
	 * init the crawl
	 * @param crawlConfig
	 */
	void setConfig(CrawlConfig crawlConfig);
	CrawlStatus getStatus();
	/**
	 * create the working nodes: CrawlSlave
	 * @param task 
	 * @param conf
	 */
	void dispatch(CrawlTask task, CrawlTaskConf conf);
	void accept(CrawlResult result);
	/**
	 * 
	 */
	void run();
	/**
	 * close the crawl
	 */
	void close();

	void addTask();
	void reduceTask();
}
