package crawl;

import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrawlConfig {
	private static final Logger LOG = LoggerFactory.getLogger(CrawlConfig.class);
	private boolean initSucceed;
	private static SolrConfig solrConfig = new SolrConfig();
	private String seedPath;
	private int threads;
	private String fetchedLinksDir;
	
	public void init()
	{
		SAXBuilder builder = new SAXBuilder();
		Document document;
		try {
			document = builder.build("config.xml");
			Element root = document.getRootElement();
			if(root != null)
			{
				setProxy(root.getChild("Proxy"));
				setTruststore(root.getChild("TrustStore"));
				setSolr(root.getChild("Solr"));
				setSeedDir(root.getChild("SeedDir"));
				setThreads(root.getChild("Threads"));
				setFetchedLinksDir(root.getChild("FetchedLinksDir"));
				setParsers(root.getChild("Parsers"));
				initSucceed = true;
			}
		} catch (JDOMException e) {
			LOG.warn(e.getMessage());
			initSucceed = false;
		} catch (IOException e) {
			LOG.warn(e.getMessage());
			initSucceed = false;
		}
	}
	
	private void setParsers(Element child) {
		if(child != null)
		{
			
		}
	}
	
	private void setFetchedLinksDir(Element child) {
		if(child != null)
			fetchedLinksDir = child.getValue();
	}

	private void setThreads(Element thread) {
		if(thread != null)
		{
			threads = Integer.parseInt(thread.getValue());
		}
	}

	private void setSeedDir(Element seedDir) {
		if(seedDir != null)
		{
			seedPath = seedDir.getValue();
		}
	}

	private void setTruststore(Element truststore) {
		if(truststore != null)
		{
			String name = truststore.getChildText("name");
			String value = truststore.getChildText("value");
			System.setProperty(name, value);
		}
	}

	private void setProxy(Element proxy) {
		if(proxy != null)
		{
			String set = proxy.getChildText("proxySet");
			String host = proxy.getChildText("proxyHost");
			String port = proxy.getChildText("proxyPort");		
			System.setProperty("proxySet", set);
			System.setProperty("proxyHost", host);
			System.setProperty("proxyPort", port);			
		}
	}
	
	private void setSolr(Element solr)
	{
		if(solr != null)
		{
			solrConfig.setServerUrl(solr.getValue());
		}
	}
	
	public boolean isInitSucceed() {
		return initSucceed;
	}
	

	public static SolrConfig getSolrConfig() {
		
		return solrConfig;
	}

	public String getSeedPath() {
		return seedPath;
	}

	public int getThreads() {
		if(threads < 1)
			return 1;
		else 
			return threads;
	}

	public String getFetchedLinksPath() {
		return fetchedLinksDir;
	}
	
	
}
