package index;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import crawl.CrawlConfig;
import data.Link;
import data.SolrData;
import util.Webpage;

public class SolrService {
	
	private static final Logger LOG = LoggerFactory.getLogger(SolrService.class);
	private static SolrServer server;
	
	static {
		String url = CrawlConfig.getSolrConfig().getServerUrl();
		server = new HttpSolrServer(url);
	}
	
	public void addFiles(Link[] links)
	{
		int num = 0;
		File file;
		for(Link link : links)
		{
			//temp
			String id = link.getUrl();
			try {
				server.deleteById(id);
			} catch (SolrServerException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				file = new File(link.getPath());
				ContentStreamUpdateRequest request = new ContentStreamUpdateRequest("/update/extract");
				request.addFile(file, null);
				request.setParam("literal.title", link.getTitle());
				request.setParam("literal.url", link.getUrl());
				request.setParam("fmap.content", "content");		    
				request.setParam("literal.location", link.getLocation());
				request.setAction(UpdateRequest.ACTION.COMMIT, true, true);
				server.request(request);
				num++;
			} catch (SolrException e) {
				if(LOG.isWarnEnabled())
				{
					LOG.warn(e.getMessage() + "\n" + link.getUrl());
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SolrServerException e) {
				e.printStackTrace();
			} 
		}
		LOG.info("SolrUpdate: add " + num + " documents.");	
	}
	
	
	public void addPages(Webpage[] pages)
	{
		List<String> ids = new ArrayList<String>();
		if(pages.length > 0)
		{
			List<SolrInputDocument> documents = new ArrayList<SolrInputDocument>();
			for(Webpage page : pages)
			{
				SolrInputDocument doc = new SolrInputDocument();
				doc.addField("url", page.getUrl());
				doc.addField("content_type", "text/html");
				doc.addField("content", page.getContent());
				//doc.addField("title", page.getTitle());
				documents.add(doc);
				ids.add(page.getUrl());
			}
			try {
				server.deleteById(ids);
				server.add(documents);
				server.commit();
				LOG.info("SolrUpdate: add " + documents.size() + " documents.");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SolrServerException e) {
				e.printStackTrace();
			}
		}
	}
	

	public void add(String id, SolrData content) {
		try {
			server.deleteById(id);
			server.addBean(content);
			server.optimize();
			server.commit();
		} catch (SolrServerException e) {		
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void add(List<String>ids, List<SolrData> contents)
	{
		if(ids.size() > 0)
		{
			try {
				server.deleteById(ids);
				server.addBeans(contents);
				server.optimize();
				server.commit();
			} catch (SolrServerException e) {		
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void deleteAll()
	{
		try {
			server.deleteByQuery("*:*");
			LOG.info("SolrClean: clean.");
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}


	
	public void addFolders(Link[] array) {
		List<SolrInputDocument> documents = new ArrayList<SolrInputDocument>();
		for(Link l : array){
			SolrInputDocument doc = new SolrInputDocument();
			doc.addField("url", l.getUrl());
			doc.addField("content_type", "folder");
			doc.addField("title", l.getPath());
			doc.addField("location", l.getLocation());
			documents.add(doc);
		}
		try {
			server.add(documents);
			server.commit();
			LOG.info("SolrUpdate: add " + documents.size() + " documents.");
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
