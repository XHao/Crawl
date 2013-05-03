package parse;


import index.SolrService;

import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.BrowserHelper;
import util.BrowserHelperListener;
import util.Webpage;

import crawl.CrawlTask;

import data.Link;
import data.SolrData;

public class BrowserParser implements ParseTask, BrowserHelperListener {
	
	private static final Logger LOG = LoggerFactory.getLogger(BrowserParser.class);
	private BrowserHelper browser;
	private CrawlTask node = null;
	private UrlFilter filter;
	
	public BrowserParser() {}
	
	@Override
	public final void setCrawlNode(CrawlTask node) {
		this.node = node;
	}

	@Override
	public final void parse(Link[] links) {
		if(node == null)
		{
			if(LOG.isErrorEnabled())
				LOG.error("Parser work without crawlnode, links update will faile, so program skip parse.");
		}
		else
		{
			browser = new BrowserHelper(links);
			browser.addBrowserHelperListener(this);
			browser.run();
		}
	}
	
	@Override
	public void callback(final Webpage page) {
		filter = new WikiFilter();
		ArrayList<Link> outLinks = new ArrayList<Link>(); 
		
		final String url = page.getUrl();
		Document doc = Jsoup.parse(page.getContent(), url);
		for(Element content : doc.getElementsByClass("wiki-content"))
		{
			 for (Element element : content.select("a[href]")) {
			  String linkHref = element.absUrl("href");
			  if(!filter.isValidate(linkHref))
				  continue;
			  Link outlink = new Link(linkHref);
			  outLinks.add(outlink);
			 } 
		}
		
		node.dispatchResultLinks(outLinks.toArray(new Link[0]));
		
		final SolrData data = new SolrData();
		data.setContent(doc.getElementsByClass("wiki-content").text());
		data.setUrl(url);
		data.setContent_type("text/html");
		data.setTitle(doc.title());
		
		if(!data.getContent().isEmpty())
		{
			SolrService service = new SolrService();
			service.add(url, data);
		}
	}
}
